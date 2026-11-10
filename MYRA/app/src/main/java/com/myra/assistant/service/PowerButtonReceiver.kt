package com.myra.assistant.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat

class PowerButtonReceiver : BroadcastReceiver() {
    private var pressCount = 0
    private var lastPressTime = 0L

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF ||
            intent.action == Intent.ACTION_SCREEN_ON) {

            val now = System.currentTimeMillis()
            if (now - lastPressTime < 600) {
                pressCount++
                if (pressCount >= 2) {
                    pressCount = 0
                    // Trigger MYRA overlay
                    val overlayIntent = Intent(context, MyraOverlayService::class.java).apply {
                        action = "SHOW_OVERLAY"
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, overlayIntent)
                    } else {
                        context.startService(overlayIntent)
                    }
                }
            } else {
                pressCount = 1
            }
            lastPressTime = now
        }
    }
}
