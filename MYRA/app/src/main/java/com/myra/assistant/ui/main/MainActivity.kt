package com.myra.assistant.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myra.assistant.R
import com.myra.assistant.ai.GeminiLiveClient
import com.myra.assistant.security.BiometricManager
import com.myra.assistant.service.AccessibilityHelperService
import com.myra.assistant.service.CallMonitorService
import com.myra.assistant.service.MyraOverlayService
import com.myra.assistant.ui.settings.SettingsActivity
import com.myra.assistant.utils.LiveAudioManager
import com.myra.assistant.utils.ScreenReader
import com.myra.assistant.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var orbView: OrbAnimationView
    private lateinit var chatRecycler: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var micButton: ImageButton
    private lateinit var statusText: TextView
    private lateinit var settingsBtn: ImageButton
    private lateinit var waveformView: WaveformView

    private var tts: TextToSpeech? = null
    private var isLiveConnected = false
    private var isCallActive = false
    private var firstConnect = true
    private var isCommandExecuting = false
    private var lastBotResponse = ""
    private var isSpeakingOrPlaying = false
    private var userIsOnMyraScreen = true

    private enum class ConversationState { IDLE, LISTENING, PROCESSING, SPEAKING, WAITING, ERROR }
    private var currentState = ConversationState.IDLE
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecognizerListening = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private var restartRunnable: Runnable? = null
    private var stateTimeoutRunnable: Runnable? = null
    private lateinit var liveClient: GeminiLiveClient
    private lateinit var liveAudioManager: LiveAudioManager
    private var consecutiveErrors = 0

    private val callReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                CallMonitorService.ACTION_CALL_ACTIVE -> {
                    isCallActive = true
                    transitionToState(ConversationState.IDLE)
                    cancelPendingRestart(); stopRecognizer(); liveAudioManager.stop()
                    updateStatus("On call... 📞")
                }
                CallMonitorService.ACTION_CALL_ENDED -> {
                    isCallActive = false
                    mainHandler.postDelayed({
                        if (isLiveConnected) {
                            transitionToState(ConversationState.LISTENING)
                            updateStatus("MYRA Ready ❤️"); triggerListenCycle()
                        }
                    }, 3000)
                }
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST = 100
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.USE_BIOMETRIC
        )
        private const val TAG = "MYRA_V7"
        private const val STATE_TIMEOUT_MS = 15000L
        private const val COMMAND_EXECUTION_DELAY_MS = 4000L
        private const val ECHO_SIMILARITY_THRESHOLD = 0.7

        private val COMMAND_PREFIXES = listOf(
            "OPEN_APP", "YOUTUBE_PLAY", "SPOTIFY_PLAY", "WHATSAPP_MSG",
            "WHATSAPP_CALL", "CALL ", "SMS ", "FLASHLIGHT_ON", "FLASHLIGHT_OFF",
            "VOLUME_UP", "VOLUME_DOWN", "SLEEP MODE"
        )
        private val INTERNAL_PATTERNS = listOf(
            "**Confirming", "**Clarifying", "**Processing", "**Analyzing",
            "I'm currently focused", "My immediate next step", "I need to ascertain",
            "I'm confirming", "seems ambiguous", "implicitly request",
            "intended recipient", "more specific contact", "misspelling of a contact",
            "informal way to describe", "chain of thought"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true); setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_main)

        BiometricManager.authenticate(this,
            onSuccess = { initViews(); createSpeechRecognizer(); tts = TextToSpeech(this, this); checkPermissions(); checkDefaultAssistant(); setupPostAuth() },
            onError   = { msg: String -> showAuthError(msg) },
            onFallback = { showPinDialog() }
        )
    }

    private fun setupPostAuth() {
        val filter = IntentFilter().apply {
            addAction(CallMonitorService.ACTION_CALL_ACTIVE)
            addAction(CallMonitorService.ACTION_CALL_ENDED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ContextCompat.registerReceiver(this, callReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        else registerReceiver(callReceiver, filter)

        liveAudioManager = LiveAudioManager(this)
        setupGeminiLive()

        val speakText = intent.getStringExtra("SPEAK_TEXT")
        if (!speakText.isNullOrEmpty()) {
            val noFg = intent.getBooleanExtra("NO_FOREGROUND", false)
            mainHandler.postDelayed({
                speakDirectly(speakText)
                if (noFg || !isUserActivelyUsingMyra()) mainHandler.postDelayed({ moveTaskToBack(true) }, 150)
            }, 500)
        }

        viewModel.aiResponse.observe(this) { response ->
            if (response.isNullOrEmpty()) return@observe
            lastBotResponse = response.lowercase()
            addBotMessage(response)
            transitionToState(ConversationState.SPEAKING)
            stopRecognizer(); isSpeakingOrPlaying = true
            if (isLiveConnected) liveClient.sendTextMessage(response)
            else { val p = Bundle(); p.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "CMD"); tts?.speak(response, TextToSpeech.QUEUE_FLUSH, p, "CMD") }
            mainHandler.postDelayed({ isCommandExecuting = false; isSpeakingOrPlaying = false }, (response.length * 100L).coerceIn(1500L, 5000L))
        }

        if (intent.getBooleanExtra("TRIGGER_MIC", false)) mainHandler.postDelayed({ activateMic() }, 800)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent); setIntent(intent)
        if (intent.getBooleanExtra("TRIGGER_MIC", false)) activateMic()
        val speakText = intent.getStringExtra("SPEAK_TEXT")
        if (!speakText.isNullOrEmpty()) {
            val noFg = intent.getBooleanExtra("NO_FOREGROUND", false)
            speakDirectly(speakText)
            if (noFg || !isUserActivelyUsingMyra()) mainHandler.postDelayed({ moveTaskToBack(true) }, 150)
        }
    }

    override fun onResume() {
        super.onResume(); userIsOnMyraScreen = true
        if (currentState == ConversationState.LISTENING && !isCallActive && !isRecognizerListening)
            mainHandler.postDelayed({ triggerListenCycle() }, 600)
    }
    override fun onPause() { super.onPause(); userIsOnMyraScreen = false }

    private fun isUserActivelyUsingMyra() = userIsOnMyraScreen && !isSpeakingOrPlaying && currentState == ConversationState.LISTENING

    private fun speakDirectly(text: String) {
        stopRecognizer(); isSpeakingOrPlaying = true
        // ✅ Status updates from automation should use TTS directly, NOT Gemini Brain
        // This prevents double-speaking and AI loops
        val p = Bundle(); p.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "STATUS")
        tts?.speak(text, TextToSpeech.QUEUE_ADD, p, "STATUS")
        mainHandler.postDelayed({ isSpeakingOrPlaying = false }, (text.length * 100L).coerceIn(1000L, 5000L))
    }

    private fun transitionToState(newState: ConversationState) {
        Log.d(TAG, "State: $currentState -> $newState")
        stateTimeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        currentState = newState
        if (newState in listOf(ConversationState.LISTENING, ConversationState.PROCESSING, ConversationState.WAITING)) {
            stateTimeoutRunnable = Runnable {
                when (newState) {
                    ConversationState.LISTENING -> { stopRecognizer(); transitionToState(ConversationState.IDLE); updateStatus("Tap mic to start") }
                    else -> { isCommandExecuting = false; transitionToState(ConversationState.LISTENING); triggerListenCycle() }
                }
            }
            mainHandler.postDelayed(stateTimeoutRunnable!!, STATE_TIMEOUT_MS)
        }
        when (newState) {
            ConversationState.IDLE       -> { micButton.setImageResource(R.drawable.ic_mic_off); orbView.setPulsating(false); orbView.setSpeaking(false); orbView.setThinking(false) }
            ConversationState.LISTENING  -> { micButton.setImageResource(R.drawable.ic_mic_on);  orbView.setPulsating(true);  orbView.setSpeaking(false); orbView.setThinking(false); updateStatus("Listening... 👂") }
            ConversationState.PROCESSING -> { micButton.setImageResource(R.drawable.ic_mic_on);  orbView.setPulsating(false); orbView.setSpeaking(false); orbView.setThinking(true);  updateStatus("Executing... ⚡") }
            ConversationState.SPEAKING   -> { micButton.setImageResource(R.drawable.ic_mic_on);  orbView.setPulsating(false); orbView.setSpeaking(true);  orbView.setThinking(false); updateStatus("Speaking... 💬") }
            ConversationState.WAITING    -> { micButton.setImageResource(R.drawable.ic_mic_on);  orbView.setPulsating(false); orbView.setSpeaking(false); orbView.setThinking(true);  updateStatus("Thinking... 🤔") }
            ConversationState.ERROR      -> { micButton.setImageResource(R.drawable.ic_mic_off); orbView.setPulsating(false); updateStatus("Error! Tap mic") }
        }
    }

    private fun createSpeechRecognizer() {
        try { speechRecognizer?.destroy(); speechRecognizer = null } catch (_: Exception) {}
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return
        try { speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this); attachRecognitionListener() }
        catch (e: Exception) { Log.e(TAG, "Create recognizer: ${e.message}") }
    }

    private fun attachRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isRecognizerListening = true; runOnUiThread { waveformView.startAnimation(); updateStatus("Listening... 👂") } }
            override fun onBeginningOfSpeech() = runOnUiThread { updateStatus("Hearing you... 🎙️") }
            override fun onResults(results: Bundle?) {
                isRecognizerListening = false; waveformView.stopAnimation()
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull { it.isNotBlank() } ?: ""
                Log.d(TAG, "STT: '$text'")
                if (text.isBlank()) { scheduleRestart(600); return }
                if (isEcho(text)) { Log.d(TAG, "Echo skipped"); scheduleRestart(800); return }
                runOnUiThread { addUserMessage(com.myra.assistant.utils.HindiTransliterator.transliterate(text)) }
                transitionToState(ConversationState.PROCESSING)
                processUserInput(text)
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val p = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: return
                if (p.isNotBlank()) runOnUiThread { updateStatus("$p...") }
            }
            override fun onError(errorCode: Int) {
                isRecognizerListening = false; waveformView.stopAnimation()
                if (currentState == ConversationState.SPEAKING || isSpeakingOrPlaying) return
                if (currentState == ConversationState.PROCESSING || isCommandExecuting) return
                when (errorCode) {
                    SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> if (currentState != ConversationState.IDLE && !isCallActive) { transitionToState(ConversationState.LISTENING); scheduleRestart(400) }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> mainHandler.postDelayed({ createSpeechRecognizer(); if (currentState != ConversationState.IDLE && !isCallActive) scheduleRestart(800) }, 500)
                    SpeechRecognizer.ERROR_AUDIO, SpeechRecognizer.ERROR_CLIENT -> mainHandler.postDelayed({ createSpeechRecognizer(); if (currentState != ConversationState.IDLE && !isCallActive) scheduleRestart(1000) }, 500)
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_SERVER -> { runOnUiThread { updateStatus("Network issue...") }; scheduleRestart(3000) }
                    else -> if (currentState != ConversationState.IDLE && !isCallActive) scheduleRestart(1000)
                }
            }
            override fun onEndOfSpeech() = runOnUiThread { waveformView.stopAnimation(); updateStatus("Processing...") }
            override fun onRmsChanged(rmsdB: Float) = waveformView.setAmplitude(rmsdB)
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun isEcho(userText: String): Boolean {
        if (lastBotResponse.isEmpty()) return false
        val u = userText.lowercase().trim(); val b = lastBotResponse.trim()
        if (u == b) return true
        if (b.contains(u) && u.length > 10) return true
        if (u.contains(b) && b.length > 10) return true
        val uW = u.split(" ").filter { it.length > 3 }.toSet()
        val bW = b.split(" ").filter { it.length > 3 }.toSet()
        if (uW.isEmpty() || bW.isEmpty()) return false
        return uW.intersect(bW).size.toDouble() / uW.size > ECHO_SIMILARITY_THRESHOLD
    }

    private fun triggerListenCycle() {
        if (currentState == ConversationState.IDLE || isCallActive) return
        if (isCommandExecuting) { scheduleRestart(COMMAND_EXECUTION_DELAY_MS); return }
        if (isSpeakingOrPlaying || currentState == ConversationState.SPEAKING) { scheduleRestart(2000); return }
        if (currentState == ConversationState.PROCESSING || currentState == ConversationState.WAITING) { scheduleRestart(3000); return }
        if (isRecognizerListening) return
        startRecognizer()
    }

    private fun startRecognizer() {
        cancelPendingRestart()
        if (speechRecognizer == null || consecutiveErrors > 3) { consecutiveErrors = 0; createSpeechRecognizer() }
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("hi-IN","en-US","en-IN","bn-IN","ta-IN","te-IN","mr-IN","gu-IN","kn-IN","ml-IN","pa-IN","ur-IN"))
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra("android.speech.extra.DICTATION_MODE", true)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500L)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            }
            speechRecognizer?.startListening(intent)
            transitionToState(ConversationState.LISTENING)
        } catch (e: Exception) { Log.e(TAG, "startListening: ${e.message}"); isRecognizerListening = false; consecutiveErrors++; scheduleRestart(1500) }
    }

    private fun scheduleRestart(delayMs: Long) {
        cancelPendingRestart()
        restartRunnable = Runnable {
            restartRunnable = null
            if (currentState != ConversationState.IDLE && !isCallActive && !isCommandExecuting && !isSpeakingOrPlaying) triggerListenCycle()
        }.also { mainHandler.postDelayed(it, delayMs) }
    }

    private fun cancelPendingRestart() { restartRunnable?.let { mainHandler.removeCallbacks(it) }; restartRunnable = null }

    private fun stopRecognizer() {
        cancelPendingRestart(); isRecognizerListening = false
        try { speechRecognizer?.cancel() } catch (_: Exception) {}
        waveformView.stopAnimation(); orbView.setPulsating(false)
    }

    private fun activateMic() {
        if (currentState == ConversationState.IDLE) { transitionToState(ConversationState.LISTENING); updateStatus("MYRA Ready ❤️"); triggerListenCycle() }
    }

    private fun deactivateMic() {
        transitionToState(ConversationState.IDLE); isCommandExecuting = false; isSpeakingOrPlaying = false
        stopRecognizer(); liveAudioManager.stop(); updateStatus("Tap mic to start 🎙️"); micButton.setImageResource(R.drawable.ic_mic_off)
    }

    private fun setupGeminiLive() {
        val prefs = getSharedPreferences("myra_prefs", MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", "") ?: ""
        if (apiKey.isEmpty()) { updateStatus("Add API Key in Settings 🔑"); return }
        val personality = prefs.getString("personality_mode", "gf") ?: "gf"
        val userName    = prefs.getString("user_name", "Sir") ?: "Sir"

        liveClient = GeminiLiveClient(apiKey, buildSystemPrompt(personality, userName),
            object : GeminiLiveClient.LiveListener {
                override fun onAudioReceived(data: ByteArray) {
                    runOnUiThread { stopRecognizer(); isSpeakingOrPlaying = true; transitionToState(ConversationState.SPEAKING); MyraOverlayService.updateState(this@MainActivity, speaking = true) }
                    liveAudioManager.playChunk(data)
                }

                override fun onTextReceived(text: String) {
                    runOnUiThread {
                        val trimmed = text.trim()

                        // Step 1: Discard internal AI thinking completely
                        if (isInternalMessage(trimmed)) {
                            Log.d(TAG, "Discarded internal msg: ${trimmed.take(50)}")
                            return@runOnUiThread
                        }

                        // Step 2: If it's a command — execute it, don't display it
                        val cmd = extractCommand(trimmed)
                        if (cmd != null) {
                            Log.d(TAG, "Executing Gemini cmd: $cmd")
                            isCommandExecuting = true
                            transitionToState(ConversationState.PROCESSING)
                            stopRecognizer()
                            lifecycleScope.launch { viewModel.processCommand(cmd) }
                            mainHandler.postDelayed({
                                isCommandExecuting = false
                                if (currentState != ConversationState.IDLE && !isCallActive) { transitionToState(ConversationState.LISTENING); triggerListenCycle() }
                            }, COMMAND_EXECUTION_DELAY_MS)
                            return@runOnUiThread
                        }

                        // Step 3: Normal spoken response — show + set for echo detection
                        lastBotResponse = trimmed.lowercase()
                        addBotMessage(trimmed)
                        if (trimmed.uppercase().run { contains("SLEEP MODE") || contains("SO JAO") }) deactivateMic()
                    }
                }

                override fun onConnected() {
                    isLiveConnected = true
                    runOnUiThread { updateStatus("MYRA Ready ❤️"); if (firstConnect) { firstConnect = false; greetUser() } }
                }

                override fun onTurnComplete() {
                    runOnUiThread {
                        isSpeakingOrPlaying = false; transitionToState(ConversationState.LISTENING)
                        MyraOverlayService.updateState(this@MainActivity, speaking = false)
                        if (currentState != ConversationState.IDLE && !isCallActive) mainHandler.postDelayed({ triggerListenCycle() }, 2000)
                    }
                }

                override fun onError(msg: String) {
                    isLiveConnected = false
                    runOnUiThread { updateStatus("Reconnecting... 🔄"); mainHandler.postDelayed({ liveClient.start() }, 5000) }
                }
            })
        liveClient.start()
    }

    private fun processUserInput(text: String) {
        Log.d(TAG, "Voice: '$text'")
        val lower = text.lowercase().trim()
        if (lower in listOf("stop", "exit", "sleep", "ruko", "band karo", "so jao", "bye myra", "goodbye")) {
            deactivateMic()
            val msg = "Theek hai, so rahi hoon."
            if (isLiveConnected) liveClient.sendTextMessage(msg) else tts?.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "SLEEP")
            return
        }

        if (viewModel.isDirectCommand(text)) {
            Log.d(TAG, "Direct command path")
            isCommandExecuting = true; transitionToState(ConversationState.PROCESSING); stopRecognizer()
            lifecycleScope.launch { viewModel.processCommand(text) }
            mainHandler.postDelayed({
                isCommandExecuting = false
                if (currentState != ConversationState.IDLE && !isCallActive) { transitionToState(ConversationState.LISTENING); triggerListenCycle() }
            }, COMMAND_EXECUTION_DELAY_MS)
            return
        }

        if (isLiveConnected) {
            val ctx = getScreenContext()
            liveClient.sendTextMessage(if (ctx.isNotEmpty()) "[Screen: $ctx] User: $text" else text)
            transitionToState(ConversationState.WAITING)
        } else {
            lifecycleScope.launch { viewModel.processCommand(text) }
        }
    }

    private fun addUserMessage(text: String) { chatAdapter.addMessage(ChatMessage(text, true)); chatRecycler.scrollToPosition(chatAdapter.itemCount - 1) }

    private fun addBotMessage(text: String) {
        if (isInternalMessage(text)) return
        val d = if (text.length > 100) text.take(97) + "..." else text
        chatAdapter.addMessage(ChatMessage(d, false)); chatRecycler.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun isInternalMessage(text: String) = INTERNAL_PATTERNS.any { text.contains(it, ignoreCase = true) }

    private fun extractCommand(text: String): String? {
        val upper = text.trimStart().uppercase()
        for (prefix in COMMAND_PREFIXES) { if (upper.startsWith(prefix)) return text.trim() }
        val found = Regex("""(?:^|\n)((?:OPEN_APP|YOUTUBE_PLAY|SPOTIFY_PLAY|WHATSAPP_MSG|WHATSAPP_CALL|CALL|SMS|FLASHLIGHT_ON|FLASHLIGHT_OFF|VOLUME_UP|VOLUME_DOWN|SLEEP MODE)\b[^\n]*)""",
            setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)).find(text)?.groupValues?.getOrNull(1)?.trim()
        if (!found.isNullOrEmpty()) return found
        Regex("`([^`]+)`").find(text)?.let { return it.groupValues[1].trim() }
        return null
    }

    private fun updateStatus(msg: String) { statusText.text = msg }

    private fun getScreenContext(): String = try {
        if (!AccessibilityHelperService.isEnabled(this)) ""
        else ScreenReader.dump(AccessibilityHelperService.instance?.rootInActiveWindow)?.take(400)?.replace("\n", " ") ?: ""
    } catch (_: Exception) { "" }

    private fun greetUser() {
        val prefs = getSharedPreferences("myra_prefs", MODE_PRIVATE)
        val name = prefs.getString("user_name", "Sir") ?: "Sir"
        val p = prefs.getString("personality_mode", "gf") ?: "gf"
        val g = if (p == "gf") "Hey $name! Bol, kya chahiye?" else "Hello $name, ready hoon."
        if (isLiveConnected) liveClient.sendTextMessage("Greet: $g") else tts?.speak(g, TextToSpeech.QUEUE_FLUSH, null, "GREET")
    }

    private fun buildSystemPrompt(personality: String, userName: String): String {
        val prefs = getSharedPreferences("myra_prefs", MODE_PRIVATE)
        val pName = prefs.getString("prime_name", "") ?: ""
        val pNum  = prefs.getString("prime_number", "") ?: ""
        val prime = if (pNum.isNotEmpty()) "Prime contact: $pName ($pNum)." else ""
        val desc  = when (personality) { "gf" -> "caring girlfriend, calls user 'Jaan'"; "professional" -> "professional assistant"; else -> "smart assistant" }
        return """
You are MYRA, fast Android voice assistant for $userName. Personality: $desc. $prime

=== STRICT RULES - NEVER BREAK ===
1. MAX 1-2 SHORT SENTENCES. No paragraphs ever.
2. NEVER say: "Confirming", "Clarifying", "I'm focused on", "My next step", "I need to ascertain", "The phrase seems ambiguous"
3. NEVER show thinking or reasoning. Just act.
4. NEVER ask clarifying questions. Guess and act immediately.
5. Reply in Hinglish. SHORT and natural.
6. STT garbles names: "winod"=Vinod, "sportify"=Spotify, "watsp"=WhatsApp, "ytube"=YouTube

=== COMMANDS (output on its OWN LINE, exact format) ===
OPEN_APP <appname>
CALL <name>
WHATSAPP_MSG <name> <message>
WHATSAPP_CALL <name>
YOUTUBE_PLAY <query>
SPOTIFY_PLAY <query>
FLASHLIGHT_ON
FLASHLIGHT_OFF
VOLUME_UP
VOLUME_DOWN
SMS <name> <message>

=== EXAMPLES ===
"Vinod ko WhatsApp karo"       -> WHATSAPP_CALL Vinod
"Vinod ko hi bhejo whatsapp"   -> WHATSAPP_MSG Vinod Hi
"winod ko watsp pe msg karo"   -> WHATSAPP_MSG Vinod Hi
"Arijit Singh sunao youtube"   -> YOUTUBE_PLAY Arijit Singh
"sportify pe kuch bajao"       -> SPOTIFY_PLAY hindi hits
"Priya ko call"                -> CALL Priya

After command, add ONE short Hinglish line:
"Vinod ko WhatsApp call kar rahi hoon!"

[Screen: ...] context use silently. Never mention it.
        """.trimIndent()
    }

    private fun initViews() {
        orbView = findViewById(R.id.orbView); chatRecycler = findViewById(R.id.chatRecycler)
        micButton = findViewById(R.id.micButton); statusText = findViewById(R.id.statusText)
        settingsBtn = findViewById(R.id.settingsBtn); waveformView = findViewById(R.id.waveformView)
        chatAdapter = ChatAdapter()
        chatRecycler.apply { adapter = chatAdapter; layoutManager = LinearLayoutManager(this@MainActivity).apply { stackFromEnd = true } }
        micButton.setImageResource(R.drawable.ic_mic_off); updateStatus("Tap mic to start 🎙️")
        micButton.setOnClickListener { if (currentState == ConversationState.IDLE) activateMic() else deactivateMic() }
        settingsBtn.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val r = tts?.setLanguage(Locale("hi", "IN"))
            if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) tts?.language = Locale.ENGLISH
            tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(id: String?) { isSpeakingOrPlaying = true }
                override fun onDone(id: String?) { isSpeakingOrPlaying = false; if (!isCallActive && !isLiveConnected) runOnUiThread { transitionToState(ConversationState.LISTENING); triggerListenCycle() } }
                override fun onError(id: String?) { isSpeakingOrPlaying = false; if (!isCallActive && !isLiveConnected) runOnUiThread { transitionToState(ConversationState.LISTENING); triggerListenCycle() } }
            })
        }
    }

    private fun checkDefaultAssistant() {
        val a = Settings.Secure.getString(contentResolver, "assistant")
        if (a == null || !a.contains(packageName)) AlertDialog.Builder(this).setTitle("Set MYRA as Default Assistant").setMessage("Power button se activate karne ke liye set karo.").setPositiveButton("Set") { _, _ -> startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)) }.setNegativeButton("Later", null).show()
    }

    private fun checkPermissions() {
        val missing = REQUIRED_PERMISSIONS.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) ActivityCompat.requestPermissions(this, missing.toTypedArray(), PERMISSIONS_REQUEST) else startSystemServices()
    }

    override fun onRequestPermissionsResult(rc: Int, p: Array<out String>, gr: IntArray) {
        super.onRequestPermissionsResult(rc, p, gr)
        if (rc == PERMISSIONS_REQUEST) { startSystemServices(); if (!gr.all { it == PackageManager.PERMISSION_GRANTED }) Toast.makeText(this, "Kuch permissions nahi mili", Toast.LENGTH_LONG).show() }
    }

    private fun startSystemServices() {
        try { ContextCompat.startForegroundService(this, Intent(this, MyraOverlayService::class.java)); ContextCompat.startForegroundService(this, Intent(this, CallMonitorService::class.java)) }
        catch (e: Exception) { Log.e(TAG, "Service start: ${e.message}") }
    }

    private fun showPinDialog() { initViews(); createSpeechRecognizer(); tts = TextToSpeech(this, this); checkPermissions(); checkDefaultAssistant(); setupPostAuth() }
    private fun showAuthError(msg: String) = Toast.makeText(this, "Auth failed: $msg", Toast.LENGTH_LONG).show()

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(callReceiver) } catch (_: Exception) {}
        cancelPendingRestart(); stateTimeoutRunnable?.let { mainHandler.removeCallbacks(it) }
        stopRecognizer(); speechRecognizer?.destroy(); speechRecognizer = null
        tts?.shutdown(); mainHandler.removeCallbacksAndMessages(null)
        if (::liveClient.isInitialized) liveClient.disconnect()
        liveAudioManager.stop()
    }
}