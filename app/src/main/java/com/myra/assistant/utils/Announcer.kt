package com.myra.assistant.utils

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Speaks short announcements (charging, new message) with the phone's text-to-speech voice.
 * If no Hindi voice is installed, the optional English [fallback] text is spoken instead (so it is never silent).
 */
object Announcer : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ready = false
    private var hindiOk = false
    private val waiting = ArrayList<Pair<String, String?>>()

    @Synchronized
    fun speak(context: Context, text: String, fallback: String? = null) {
        try {
            if (tts == null) {
                ready = false
                tts = TextToSpeech(context.applicationContext, this)
            }
            if (ready) say(text, fallback) else waiting.add(text to fallback)
        } catch (_: Throwable) { }
    }

    @Synchronized
    override fun onInit(status: Int) {
        val t = tts ?: return
        if (status != TextToSpeech.SUCCESS) { tts = null; waiting.clear(); return }
        val r = t.setLanguage(Locale("hi", "IN"))
        hindiOk = !(r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED)
        if (!hindiOk) t.setLanguage(Locale.getDefault())
        t.setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()
        )
        ready = true
        waiting.forEach { say(it.first, it.second) }
        waiting.clear()
    }

    private fun say(text: String, fallback: String?) {
        val line = if (!hindiOk && fallback != null) fallback else text
        try { tts?.speak(line, TextToSpeech.QUEUE_ADD, null, "myra_${System.nanoTime()}") } catch (_: Throwable) { }
    }
}
