package com.myra.assistant.websocket

import android.util.Base64
import android.util.Log
import com.myra.assistant.utils.Constants
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Gemini Live WebSocket client.
 *
 * Same model / voice / API key / audio format as before. Added (all optional, default off for old callers):
 *  - input/output transcription callbacks (voice -> chat text)
 *  - session resumption (conversation continuity across reconnects)
 *  - stale-socket protection (a replaced socket can never deliver duplicate audio)
 *  - goAway / interrupted / close handling
 */
class GeminiWebSocketClient(
    private val apiKey: String,
    private val systemPrompt: String,
    private val onConnected: () -> Unit,
    private val onAudioReceived: (ByteArray) -> Unit,
    private val onTextReceived: (String) -> Unit,
    private val onTurnComplete: () -> Unit,
    private val onError: (String) -> Unit,
    private val onInputTranscription: ((String) -> Unit)? = null,
    private val onOutputTranscription: ((String) -> Unit)? = null,
    private val onInterrupted: (() -> Unit)? = null,
    private val onGoAway: (() -> Unit)? = null
) {
    companion object {
        private const val TAG = "GEMINI_WS"

        /** Shared client: TLS sessions / connection pool are reused, so reconnects are faster. */
        private val sharedClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .pingInterval(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        /** Latest resumption handle issued by the server (lets a reconnect continue the same conversation). */
        @Volatile var resumptionHandle: String? = null

        /**
         * Transcription + session resumption are optional setup fields. If the server ever rejects the setup
         * twice in a row, they are switched off automatically so the original voice pipeline keeps working.
         */
        @Volatile var extrasEnabled: Boolean = true
        @Volatile private var setupFailures = 0

        fun resetSession() { resumptionHandle = null }
    }

    private var webSocket: WebSocket? = null
    @Volatile private var isSetupComplete = false
    @Volatile private var generation = 0

    private val url = "${Constants.GEMINI_WS_BASE}?key=$apiKey"

    fun connect() {
        val myGen = ++generation
        isSetupComplete = false
        try { webSocket?.cancel() } catch (_: Exception) {}

        val request = Request.Builder()
            .url(url)
            .addHeader("Origin", "https://generativelanguage.googleapis.com")
            .build()

        webSocket = sharedClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                if (myGen != generation) { ws.cancel(); return }
                Log.d(TAG, "Connected ✅")
                sendSetup(ws)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                if (myGen != generation) return
                handleResponse(text)
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                if (myGen != generation) return
                try { handleResponse(bytes.utf8()) } catch (e: Exception) { Log.e(TAG, "Binary parse error") }
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                ws.close(code, reason)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                if (myGen != generation) return
                noteFailureBeforeSetup()
                isSetupComplete = false
                Log.e(TAG, "WS Failure: ${t.message}")
                onError("Connection failed: ${t.message}")
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                if (myGen != generation) return
                noteFailureBeforeSetup()
                isSetupComplete = false
                Log.d(TAG, "WS Closed: $code $reason")
                // A close we did not ask for (server timeout, bad setup...) must trigger a reconnect.
                onError("Closed: $code $reason")
            }
        })
    }

    private fun noteFailureBeforeSetup() {
        if (!isSetupComplete && extrasEnabled) {
            setupFailures++
            if (setupFailures >= 2) {
                Log.w(TAG, "Setup failed twice - disabling optional transcription/resumption fields")
                extrasEnabled = false
                resumptionHandle = null
            }
        }
    }

    private fun sendSetup(ws: WebSocket) {
        val setup = JSONObject().apply {
            put("setup", JSONObject().apply {
                put("model", Constants.GEMINI_MODEL)
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("AUDIO"))
                    put("speechConfig", JSONObject().apply {
                        put("voiceConfig", JSONObject().apply {
                            put("prebuiltVoiceConfig", JSONObject().apply {
                                put("voiceName", Constants.GEMINI_VOICE)
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
                if (extrasEnabled) {
                    put("inputAudioTranscription", JSONObject())
                    put("outputAudioTranscription", JSONObject())
                    put("sessionResumption", JSONObject().apply {
                        resumptionHandle?.let { put("handle", it) }
                    })
                }
            })
        }
        ws.send(setup.toString())
        Log.d(TAG, "Setup sent ✅ (extras=$extrasEnabled, resumed=${resumptionHandle != null})")
    }

    fun sendAudioChunk(pcm: ByteArray) {
        if (!isSetupComplete) return
        try {
            // Hand-built JSON: avoids allocating JSONObject trees for every ~32 ms mic chunk.
            val b64 = Base64.encodeToString(pcm, Base64.NO_WRAP)
            webSocket?.send(
                "{\"realtimeInput\":{\"mediaChunks\":[{\"mimeType\":\"audio/pcm;rate=16000\",\"data\":\"$b64\"}]}}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Audio send error: ${e.message}")
        }
    }

    fun sendTextMessage(text: String) {
        if (!isSetupComplete) return
        try {
            val msg = JSONObject().apply {
                put("clientContent", JSONObject().apply {
                    put("turns", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", text)))
                    }))
                    put("turnComplete", true)
                })
            }
            webSocket?.send(msg.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Text send error: ${e.message}")
        }
    }

    private fun handleResponse(json: String) {
        try {
            val obj = JSONObject(json)

            // Setup complete
            if (obj.has("setupComplete")) {
                isSetupComplete = true
                setupFailures = 0
                Log.d(TAG, "Gemini Ready ✅")
                onConnected()
                return
            }

            obj.optJSONObject("sessionResumptionUpdate")?.let { upd ->
                val handle = upd.optString("newHandle", "")
                if (upd.optBoolean("resumable", true) && handle.isNotEmpty()) resumptionHandle = handle
            }

            if (obj.has("goAway")) {
                Log.w(TAG, "Server goAway - reconnecting with resumption")
                onGoAway?.invoke()
                return
            }

            val serverContent = obj.optJSONObject("serverContent") ?: return

            if (serverContent.optBoolean("interrupted", false)) {
                onInterrupted?.invoke()
            }

            serverContent.optJSONObject("inputTranscription")?.optString("text", "")?.let {
                if (it.isNotEmpty()) onInputTranscription?.invoke(it)
            }

            val modelTurn = serverContent.optJSONObject("modelTurn")
            val parts = modelTurn?.optJSONArray("parts")
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    // Audio: handed to the player immediately, chunk by chunk (streaming playback)
                    if (part.has("inlineData")) {
                        val data = part.getJSONObject("inlineData").getString("data")
                        onAudioReceived(Base64.decode(data, Base64.DEFAULT))
                    }
                    // Text (internal "thought" parts are never shown or spoken)
                    if (part.has("text") && !part.optBoolean("thought", false)) {
                        onTextReceived(part.getString("text"))
                    }
                }
            }

            serverContent.optJSONObject("outputTranscription")?.optString("text", "")?.let {
                if (it.isNotEmpty()) onOutputTranscription?.invoke(it)
            }

            if (serverContent.optBoolean("turnComplete", false)) {
                onTurnComplete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message} | ${json.take(200)}")
        }
    }

    fun disconnect() {
        generation++   // invalidates every callback of the socket being closed
        try { webSocket?.close(1000, "Bye") } catch (_: Exception) {}
        webSocket = null
        isSetupComplete = false
    }

    fun isConnected() = isSetupComplete
}
