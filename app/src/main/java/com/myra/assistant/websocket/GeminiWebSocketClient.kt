package com.myra.assistant.websocket

import android.util.Base64
import android.util.Log
import com.myra.assistant.utils.Constants
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiWebSocketClient(
    private val apiKey: String,
    private val systemPrompt: String,
    private val onConnected: () -> Unit,
    private val onAudioReceived: (ByteArray) -> Unit,
    private val onTextReceived: (String) -> Unit,
    private val onTurnComplete: () -> Unit,
    private val onError: (String) -> Unit
) {
    private val TAG = "GEMINI_WS"
    private var webSocket: WebSocket? = null
    private var isSetupComplete = false

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(20, TimeUnit.SECONDS)
        .pingInterval(25, TimeUnit.SECONDS)
        .build()

    private val url = "${Constants.GEMINI_WS_BASE}?key=$apiKey"

    fun connect() {
        isSetupComplete = false
        val request = Request.Builder()
            .url(url)
            .addHeader("Origin", "https://generativelanguage.googleapis.com")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(TAG, "Connected ✅")
                sendSetup(ws)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                handleResponse(text)
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                try { handleResponse(bytes.utf8()) } catch (e: Exception) { Log.e(TAG, "Binary parse error") }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                isSetupComplete = false
                Log.e(TAG, "WS Failure: ${t.message}")
                onError("Connection failed: ${t.message}")
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                isSetupComplete = false
                Log.d(TAG, "WS Closed: $reason")
            }
        })
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
            })
        }
        ws.send(setup.toString())
        Log.d(TAG, "Setup sent ✅")
    }

    fun sendAudioChunk(pcm: ByteArray) {
        if (!isSetupComplete) return
        try {
            val b64 = Base64.encodeToString(pcm, Base64.NO_WRAP)
            val msg = JSONObject().apply {
                put("realtimeInput", JSONObject().apply {
                    put("mediaChunks", JSONArray().put(JSONObject().apply {
                        put("mimeType", "audio/pcm;rate=16000")
                        put("data", b64)
                    }))
                })
            }
            webSocket?.send(msg.toString())
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
            if (obj.has("setupComplete") || obj.optJSONObject("setupComplete") != null) {
                isSetupComplete = true
                Log.d(TAG, "Gemini Ready ✅")
                onConnected()
                return
            }

            val serverContent = obj.optJSONObject("serverContent") ?: return

            val modelTurn = serverContent.optJSONObject("modelTurn")
            if (modelTurn != null) {
                val parts = modelTurn.optJSONArray("parts") ?: return
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    // Audio
                    if (part.has("inlineData")) {
                        val data = part.getJSONObject("inlineData").getString("data")
                        val bytes = Base64.decode(data, Base64.DEFAULT)
                        onAudioReceived(bytes)
                    }
                    // Text
                    if (part.has("text")) {
                        onTextReceived(part.getString("text"))
                    }
                }
            }

            if (serverContent.optBoolean("turnComplete", false)) {
                onTurnComplete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message} | ${json.take(200)}")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Bye")
        webSocket = null
        isSetupComplete = false
    }

    fun isConnected() = isSetupComplete
}
