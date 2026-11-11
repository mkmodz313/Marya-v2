package com.myra.assistant.utils

import android.content.Context

/**
 * Gemini key used by the app. If the user typed their own key in Settings it wins; otherwise MYRA's built-in key is
 * used, so people can use the app without any setup.
 *
 * NOTE: any key stored inside an APK can be extracted by someone determined. Limit its damage in Google AI Studio /
 * Cloud Console (quota + budget alerts) and rotate it if it is ever abused.
 */
object ApiKeys {
    private const val DEFAULT_GEMINI = ""

    fun gemini(context: Context): String {
        val own = context.applicationContext.prefs().getString(Constants.KEY_API_KEY, "")?.trim().orEmpty()
        return if (own.isNotEmpty()) own else DEFAULT_GEMINI
    }
}
