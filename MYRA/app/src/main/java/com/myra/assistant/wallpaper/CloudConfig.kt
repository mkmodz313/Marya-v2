package com.myra.assistant.wallpaper

/**
 * Firebase project settings for the wallpaper catalog (see backend/README_BACKEND.md).
 *
 * These are PUBLIC client identifiers (not secrets). Security comes from the Firestore/Storage rules in /backend,
 * which only let a signed-in administrator write. There is NO admin password anywhere in the APK.
 *
 * Until you paste your own values the app shows only the bundled sample wallpapers and tells the user that
 * the cloud catalog is not configured - it never pretends cloud sync works.
 */
object CloudConfig {
    const val FIREBASE_API_KEY = "AIzaSyCsr2uBUu3pYQFE2kaC2fwQ9QAV0LxcyMM"
    const val FIREBASE_PROJECT_ID = "mkmodzboy2026"

    /** Admin Panel website (Firebase Hosting). The Settings button opens this page; login happens there. */
    const val ADMIN_PANEL_URL = "https://mkmodzboy2026.web.app"

    /** How often the open gallery re-checks the catalog (periodic refresh). */
    const val REFRESH_INTERVAL_MS = 45_000L

    val isConfigured: Boolean
        get() = !FIREBASE_API_KEY.startsWith("YOUR_") && !FIREBASE_PROJECT_ID.startsWith("YOUR_")
}
