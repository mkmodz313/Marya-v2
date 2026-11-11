package com.myra.assistant

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.myra.assistant.ui.main.SplashActivity
import com.myra.assistant.utils.ChargingWatcher
import com.myra.assistant.utils.prefs
import com.myra.assistant.wallpaper.ThumbLoader
import com.myra.assistant.wallpaper.WallpaperItem
import com.myra.assistant.wallpaper.WallpaperRepository
import java.io.File

/**
 * Process-level setup:
 *  - crash safety net (stray exceptions are logged and swallowed instead of closing the app)
 *  - charging announcement receiver
 *  - wallpaper thumbnails are pre-loaded from disk so the gallery is instant
 */
class MyraApp : Application() {

    override fun onCreate() {
        super.onCreate()
        installCrashGuard()
        try { ChargingWatcher.register(this) } catch (_: Throwable) { }
        try {
            val urls = (WallpaperRepository.cached(this) + WallpaperItem.SAMPLES).map { it.thumbUrl }
            ThumbLoader.prewarm(this, urls)
        } catch (_: Throwable) { }
    }

    private fun installCrashGuard() {
        val recent = ArrayDeque<Long>()

        fun log(t: Thread, e: Throwable) {
            Log.e("MYRA_CRASH", "Guarded exception on ${t.name}", e)
            try {
                val f = File(filesDir, "crash.log")
                if (f.length() > 200_000) f.delete()
                f.appendText("${System.currentTimeMillis()} [${t.name}] ${Log.getStackTraceString(e)}\n\n")
            } catch (_: Throwable) { }
        }

        // 1) background threads: never take the whole app down
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            // Main-thread errors thrown from Looper.loop() are caught by the loop below; errors delivered here
            // (e.g. by coroutines) are logged and swallowed as well so the app is not killed.
            log(t, e)
        }

        // 2) main thread: keep the Looper running after an exception. If it keeps failing in a tight loop,
        //    restart the app cleanly instead of freezing.
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                    return@post
                } catch (e: Throwable) {
                    log(Looper.getMainLooper().thread, e)
                    val now = System.currentTimeMillis()
                    recent.addLast(now)
                    while (recent.isNotEmpty() && now - recent.first() > 3000) recent.removeFirst()
                    if (recent.size >= 6) {
                        try {
                            startActivity(Intent(this, SplashActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        } catch (_: Throwable) { }
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
                }
            }
        }
    }
}
