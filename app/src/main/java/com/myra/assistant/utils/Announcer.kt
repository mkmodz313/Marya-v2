package com.myra.assistant.utils

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Speaks short announcements (charging, new message) with the phone's text-to-speech voice. */
object Announcer : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ready = false
    private val waiting = ArrayList<String>()

    @Synchronized
    fun speak(context: Context, text: String) {
        if (tts == null) tts = TextToSpeech(context.applicationContext, this)
        if (ready) say(text) else waiting.add(text)
    }

    @Synchronized
    override fun onInit(status: Int) {
        val t = tts ?: return
        if (status != TextToSpeech.SUCCESS) { tts = null; return }
        val r = t.setLanguage(Locale("hi", "IN"))
        if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) t.setLanguage(Locale.getDefault())
        t.setAudioAttributes(
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANT).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()
        )
        ready = true
        waiting.forEach { say(it) }
        waiting.clear()
    }

    private fun say(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, "myra_${System.nanoTime()}")
    }
}
