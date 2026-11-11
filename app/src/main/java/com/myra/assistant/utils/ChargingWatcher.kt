package com.myra.assistant.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.core.content.ContextCompat

/**
 * Announces "phone is charging + battery %" the moment the charger is plugged in.
 * ACTION_POWER_CONNECTED cannot be declared in the manifest (Android 8+), so it is registered once at process
 * level (MyraApp) - it works whenever MYRA's process is alive (i.e. while the voice service is running).
 */
object ChargingWatcher {
    private var registered = false
    private var lastAnnounce = 0L

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) {
            if (i.action != Intent.ACTION_POWER_CONNECTED) return
            try { announce(c.applicationContext) } catch (_: Throwable) { }
        }
    }

    @Synchronized
    fun register(app: Context) {
        if (registered) return
        try {
            ContextCompat.registerReceiver(
                app, receiver, IntentFilter(Intent.ACTION_POWER_CONNECTED), ContextCompat.RECEIVER_EXPORTED
            )
            registered = true
        } catch (_: Throwable) { }
    }

    private fun batteryPercent(ctx: Context): Int {
        val sticky = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (sticky != null) {
            val level = sticky.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = sticky.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) return level * 100 / scale
        }
        return try {
            (ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager)
                .getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } catch (_: Exception) { -1 }
    }

    private fun announce(ctx: Context) {
        val sp = ctx.prefs()
        if (!sp.getBoolean("charging_announce", true)) return
        val now = System.currentTimeMillis()
        if (now - lastAnnounce < 4000) return
        lastAnnounce = now
        val pct = batteryPercent(ctx)
        val love = sp.getBoolean("girlfriend_mode", false)
        val hi = (if (love) "जानी, " else "") + "आपका मोबाइल चार्जिंग पर लग गया है।" +
            if (pct in 0..100) " बैटरी $pct प्रतिशत है।" else ""
        val en = (if (love) "Jaani, " else "") + "Your phone is now charging." +
            if (pct in 0..100) " Battery is at $pct percent." else ""
        Announcer.speak(ctx, hi, en)
    }
}
