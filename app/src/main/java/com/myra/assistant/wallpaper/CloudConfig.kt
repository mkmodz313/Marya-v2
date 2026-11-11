package com.myra.assistant.wallpaper

object CloudConfig {
    const val FIREBASE_API_KEY = "AIzaSyCsr2uBUu3pYQFE2kaC2fwQ9QAV0LxcyMM"
    const val FIREBASE_PROJECT_ID = "mkmodzboy2026"
    const val STORAGE_BUCKET = "mkmodzboy2026.firebasestorage.app"

    /** The ONLY account allowed to use the in-app Admin Panel (also enforced by the Firestore/Storage rules). */
    const val ADMIN_EMAIL = "umarmalang496@gmail.com"

    /** Admin Panel website (Firebase Hosting). The Settings button opens this page; login happens there. */
    const val ADMIN_PANEL_URL = "https://mkmodzboy2026.web.app"

    /** How often the open gallery re-checks the catalog (periodic refresh). */
    const val REFRESH_INTERVAL_MS = 45_000L

    val isConfigured: Boolean
        get() = !FIREBASE_API_KEY.startsWith("YOUR_") && !FIREBASE_PROJECT_ID.startsWith("YOUR_")
}
