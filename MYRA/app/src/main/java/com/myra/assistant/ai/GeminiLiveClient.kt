package com.myra.assistant.ai

import android.util.Base64
import android.util.Log
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiLiveClient(
    private val apiKey: String,
    private val systemPrompt: String,
    private val callback: LiveListener
) {
    private val TAG = "MYRA_LIVE"
    private var isSetupComplete = false
    private var webSocket: WebSocket? = null

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(20, TimeUnit.SECONDS)
        .pingInterval(20, TimeUnit.SECONDS) // Connection ko zinda rakhne ke liye
        .build()

    // Aapka original model name
    private val URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent?key=$apiKey"

    interface LiveListener {
        fun onAudioReceived(data: ByteArray)
        fun onTextReceived(text: String)
        fun onConnected()
        fun onTurnComplete()
        fun onError(msg: String)
    }

    fun start() {
        isSetupComplete = false
        val request = Request.Builder()
            .url(URL)
            .addHeader("Origin", "https://generativelanguage.googleapis.com")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "Connected ✅")
                sendSetup()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Saara data JSON mein aata hai, use handle karein
                handleResponse(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Gemini Live API hamesha JSON Text bhejta hai.
                // Binary frame ki handle karne ki zaroorat nahi hai, par safety ke liye:
                try {
                    handleResponse(bytes.utf8())
                } catch (e: Exception) {
                    Log.e(TAG, "Binary Error: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isSetupComplete = false
                callback.onError("Connection Failed: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isSetupComplete = false
            }
        })
    }

    private fun sendSetup() {
        try {
            val setupJson = JSONObject().apply {
                put("setup", JSONObject().apply {
                    put("model", "models/gemini-2.5-flash-native-audio-preview-12-2025")
                    put("generationConfig", JSONObject().apply {
                        put("responseModalities", JSONArray().put("AUDIO"))
                        put("speechConfig", JSONObject().apply {
                            put("voiceConfig", JSONObject().apply {
                                put("prebuiltVoiceConfig", JSONObject().apply {
                                    put("voiceName", "Aoede")
                                })
                            })
                        })
                    })
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                    })
                })
            }
            webSocket?.send(setupJson.toString())
            Log.d(TAG, "Setup Sent ✅")
        } catch (e: Exception) {
            Log.e(TAG, "Setup Error: ${e.message}")
        }
    }

    fun sendTextMessage(text: String) {
        if (!isSetupComplete) return
        try {
            val msg = JSONObject().apply {
                put("clientContent", JSONObject().apply {
                    put("turns", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().apply {
                            put("text", text)
                        }))
                    }))
                    put("turnComplete", true)
                })
            }
            webSocket?.send(msg.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Send Error: ${e.message}")
        }
    }

    private fun handleResponse(jsonString: String) {
        try {
            val json = JSONObject(jsonString)

            // Setup confirm karne ka sahi tarika
            if (json.has("setupComplete")) {
                isSetupComplete = true
                Log.d(TAG, "GEMINI READY ✅")
                callback.onConnected()
                return
            }

            val serverContent = json.optJSONObject("serverContent") ?: return

            // AI Response handle karein
            val modelTurn = serverContent.optJSONObject("modelTurn")
            if (modelTurn != null) {
                val parts = modelTurn.optJSONArray("parts") ?: return
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)

                    // Audio Data (Awaaz)
                    if (part.has("inlineData")) {
                        val base64Data = part.getJSONObject("inlineData").getString("data")
                        callback.onAudioReceived(Base64.decode(base64Data, Base64.DEFAULT))
                    }

                    // Text Data (Subtitles)
                    if (part.has("text")) {
                        callback.onTextReceived(part.getString("text"))
                    }
                }
            }

            if (serverContent.optBoolean("turnComplete", false)) {
                callback.onTurnComplete()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Parse Error: ${e.message}")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Bye")
        isSetupComplete = false
    }
}