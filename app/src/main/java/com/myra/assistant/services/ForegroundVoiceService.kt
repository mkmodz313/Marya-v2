package com.myra.assistant.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.net.ConnectivityManager
import android.net.Network
import androidx.core.app.NotificationCompat
import com.myra.assistant.R
import com.myra.assistant.control.PhoneControl
import com.myra.assistant.data.ChatRepository
import com.myra.assistant.ai.AIResponseManager
import com.myra.assistant.ai.ConversationMemory
import com.myra.assistant.ai.IntentAnalyzer
import com.myra.assistant.ai.DynamicDecisionEngine
import com.myra.assistant.ui.main.MainActivity
import com.myra.assistant.utils.Constants
import com.myra.assistant.utils.Logger
import com.myra.assistant.utils.prefs
import com.myra.assistant.voice.AudioFocusManager
import com.myra.assistant.voice.AudioPlayer
import com.myra.assistant.voice.AudioRecorder
import com.myra.assistant.voice.VoiceActivityDetector
import com.myra.assistant.voice.VoiceStateManager
import com.myra.assistant.websocket.GeminiWebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForegroundVoiceService : Service() {
    private val TAG = "VOICE_SVC"
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var vad: VoiceActivityDetector
    private lateinit var audioFocus: AudioFocusManager
    private var geminiClient: GeminiWebSocketClient? = null

    // reconnect / continuity state
    private var reconnectJob: Job? = null
    private var backoffMs = 1000L
    private var lastConnectAt = 0L
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    // live transcript state for the current turn (voice -> chat)
    private val userTranscript = StringBuilder()
    private var assistantStarted = false
    private var suppressAssistantText = false

    companion object {
        var instance: ForegroundVoiceService? = null
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        isRunning = true
        startForeground(Constants.NOTIF_ID_VOICE, buildNotification())
        ChatRepository.init(applicationContext)
        initComponents()
        registerNetworkWatcher()
    }

    private fun initComponents() {
        audioFocus = AudioFocusManager(this)
        audioPlayer = AudioPlayer()

        audioPlayer.onPlaybackStarted = {
            VoiceStateManager.setSpeaking()
        }
        audioPlayer.onPlaybackFinished = {
            VoiceStateManager.setListening()
        }

        vad = VoiceActivityDetector(
            onSpeechStart = {
                VoiceStateManager.setListening()
            },
            onSpeechEnd = {
                VoiceStateManager.setThinking()
            }
        )

        audioRecorder = AudioRecorder(this) { chunk ->
            // Don't send audio while AI is speaking
            if (!VoiceStateManager.isAiSpeaking()) {
                vad.processChunk(chunk)
                geminiClient?.sendAudioChunk(chunk)
            }
        }

        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isNotEmpty()) {
            connectGemini(apiKey)
        }
    }

    private fun connectGemini(apiKey: String) {
        // Only ONE live socket at a time: closing the old one first means it can never deliver duplicate audio.
        geminiClient?.disconnect()
        audioPlayer.dropPending()
        lastConnectAt = System.currentTimeMillis()
        userTranscript.clear()
        assistantStarted = false
        suppressAssistantText = false

        geminiClient = GeminiWebSocketClient(
            apiKey = apiKey,
            systemPrompt = buildSystemPrompt(),
            onConnected = {
                backoffMs = 1000L
                VoiceStateManager.setListening()
                audioRecorder.start()
            },
            onAudioReceived = { data ->
                // Streaming playback: every chunk goes straight to the player as soon as it arrives.
                audioPlayer.playChunk(data)
            },
            onTextReceived = { text ->
                val clean = AIResponseManager.clean(text)
                if (clean.isNotBlank()) {
                    ConversationMemory.addAssistant(clean)
                    val cmd = AIResponseManager.extractCommand(clean)
                    if (cmd != null) {
                        scope.launch {
                            val intent = IntentAnalyzer.analyze(cmd)
                            DynamicDecisionEngine.execute(this@ForegroundVoiceService, intent)
                        }
                    } else {
                        ChatRepository.addAssistant(clean)
                    }
                    // Broadcast to UI
                    sendBroadcast(Intent("MYRA_RESPONSE").setPackage(packageName).putExtra("text", clean))
                }
            },
            onTurnComplete = {
                finishTurn()
                // Resume listening after AI finishes
                if (!audioRecorder.isActive()) audioRecorder.start()
            },
            onError = { msg -> scheduleReconnect(msg) },
            onInputTranscription = { t -> onUserTranscript(t) },
            onOutputTranscription = { t -> onAssistantTranscript(t) },
            onInterrupted = { audioPlayer.dropPending() },
            onGoAway = { scheduleReconnect("server goAway") }
        )
        geminiClient?.connect()
    }

    /** Exponential back-off reconnect (1s, 2s, 4s ... 15s). One pending reconnect at a time. */
    private fun scheduleReconnect(reason: String) {
        Logger.e(TAG, "Gemini error: $reason")
        VoiceStateManager.setError("Reconnecting...")
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val wait = backoffMs
            backoffMs = (backoffMs * 2).coerceAtMost(15_000L)
            delay(wait)
            if (!isNetworkUp()) {
                VoiceStateManager.setError("No internet")
                return@launch   // the network watcher reconnects as soon as internet is back
            }
            val key = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
            if (key.isNotEmpty()) connectGemini(key)
        }
    }

    private fun isNetworkUp(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun registerNetworkWatcher() {
        try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val cb = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    val connected = geminiClient?.isConnected() == true
                    val justTried = System.currentTimeMillis() - lastConnectAt < 4000
                    if (!connected && !justTried) {
                        reconnectJob?.cancel()
                        backoffMs = 1000L
                        scope.launch {
                            delay(400)
                            val key = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
                            if (key.isNotEmpty()) connectGemini(key)
                        }
                    }
                }
            }
            cm.registerDefaultNetworkCallback(cb)
            networkCallback = cb
        } catch (e: Exception) {
            Logger.e(TAG, "Network watcher unavailable: ${e.message}")
        }
    }

    // ── Voice -> chat transcripts ────────────────────────────────────────────────────────────────
    private fun onUserTranscript(t: String) {
        if (assistantStarted) finishTurn()
        userTranscript.append(t)
        ChatRepository.appendStreaming(true, t)
    }

    private fun onAssistantTranscript(t: String) {
        if (!assistantStarted) {
            assistantStarted = true
            dispatchSpokenCommand()
        }
        if (!suppressAssistantText) ChatRepository.appendStreaming(false, t)
    }

    /** What the user said is checked for phone-control commands once the user's sentence is complete. */
    private fun dispatchSpokenCommand() {
        val said = userTranscript.toString().trim()
        userTranscript.clear()
        if (said.isEmpty()) return
        ChatRepository.finishStreaming()
        val out = PhoneControl.handle(applicationContext, said)
        if (out.handled) {
            // MYRA's own reply for this action is shown as text; Gemini's spoken wording is not duplicated in chat.
            suppressAssistantText = true
            ChatRepository.addAssistant(out.reply)
        }
    }

    private fun finishTurn() {
        if (!assistantStarted && userTranscript.isNotEmpty()) dispatchSpokenCommand()
        ChatRepository.finishStreaming()
        assistantStarted = false
        suppressAssistantText = false
    }

    fun sendTextToGemini(text: String) {
        ConversationMemory.addUser(text)
        VoiceStateManager.setThinking()
        assistantStarted = false
        suppressAssistantText = false
        geminiClient?.sendTextMessage(text)
    }

    fun isGeminiConnected(): Boolean = geminiClient?.isConnected() == true

    fun reconnectGemini() {
        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isNotEmpty()) {
            GeminiWebSocketClient.resetSession()   // manual reconnect = fresh conversation on the server
            reconnectJob?.cancel()
            backoffMs = 1000L
            connectGemini(apiKey)
        }
    }

    private fun buildSystemPrompt(): String {
        val userName = prefs().getString(Constants.KEY_USER_NAME, "Boss") ?: "Boss"
        val personality = prefs().getString(Constants.KEY_PERSONALITY, "friendly") ?: "friendly"
        return """
YOU ARE MYRA - My Yours Responsive Assistant.
User's name is $userName.
Personality: $personality.

STRICT RULES:
- Never explain or think aloud
- Never say: "Responding to", "I've registered", "Formulating", "Interpreting", "Processing"
- For device actions return ONLY the command:
  OPEN_APP <name> | CALL <name> | WHATSAPP_CALL <name>
  WHATSAPP_MSG <name> <message> | YOUTUBE_PLAY <query>
  SPOTIFY_PLAY <query> | FLASHLIGHT_ON | FLASHLIGHT_OFF
  VOLUME_UP | VOLUME_DOWN | SMS <name> <message>
- EXCEPTION to the rule above: when the user asks you to open an app, website or WhatsApp chat, answer with ONE short warm sentence such as 'Sure dear! Opening WhatsApp.' (the app performs the action itself)
- Never say a WhatsApp message was sent. After the user dictates a message, only ask: 'Shall I send it?' and wait for yes/no
- For conversation: Reply short and natural in Hinglish
- Address user as $userName
- Be warm, witty, and human-like
        """.trimIndent()
    }

    private fun buildNotification(): Notification {
        createChannel()
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, Constants.NOTIF_CHANNEL_VOICE)
            .setContentTitle("MYRA is listening ❤️")
            .setContentText("Always ready for you")
            .setSmallIcon(R.drawable.ic_myra_notif)
            .setContentIntent(pi)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                Constants.NOTIF_CHANNEL_VOICE,
                "MYRA Voice Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        isRunning = false
        instance = null
        reconnectJob?.cancel()
        try {
            networkCallback?.let { (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).unregisterNetworkCallback(it) }
        } catch (_: Exception) {}
        audioRecorder.stop()
        audioPlayer.release()
        geminiClient?.disconnect()
        job.cancel()
        super.onDestroy()
    }
}
