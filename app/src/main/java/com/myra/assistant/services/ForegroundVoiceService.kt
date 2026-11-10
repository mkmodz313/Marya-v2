package com.myra.assistant.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.myra.assistant.R
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

    companion object {
        var instance: ForegroundVoiceService? = null
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        isRunning = true
        startForeground(Constants.NOTIF_ID_VOICE, buildNotification())
        initComponents()
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
        geminiClient = GeminiWebSocketClient(
            apiKey = apiKey,
            systemPrompt = buildSystemPrompt(),
            onConnected = {
                VoiceStateManager.setListening()
                audioRecorder.start()
            },
            onAudioReceived = { data ->
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
                    }
                    // Broadcast to UI
                    sendBroadcast(Intent("MYRA_RESPONSE").putExtra("text", clean))
                }
            },
            onTurnComplete = {
                // Resume listening after AI finishes
                if (!audioRecorder.isActive()) audioRecorder.start()
            },
            onError = { msg ->
                Logger.e(TAG, "Gemini error: $msg")
                VoiceStateManager.setError("Reconnecting...")
                // Auto-reconnect after delay
                scope.launch {
                    kotlinx.coroutines.delay(3000)
                    geminiClient?.connect()
                }
            }
        )
        geminiClient?.connect()
    }

    fun sendTextToGemini(text: String) {
        ConversationMemory.addUser(text)
        VoiceStateManager.setThinking()
        geminiClient?.sendTextMessage(text)
    }

    fun reconnectGemini() {
        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isNotEmpty()) {
            geminiClient?.disconnect()
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
        audioRecorder.stop()
        audioPlayer.release()
        geminiClient?.disconnect()
        job.cancel()
        super.onDestroy()
    }
}
