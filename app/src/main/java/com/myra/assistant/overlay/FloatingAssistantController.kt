package com.myra.assistant.overlay

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.myra.assistant.service.MyraOverlayService
import com.myra.assistant.utils.Constants

object FloatingAssistantController {

    fun show(context: Context) {
        if (!OverlayPermissionManager.hasPermission(context)) {
            OverlayPermissionManager.requestPermission(context)
            return
        }
        val i = Intent(context, MyraOverlayService::class.java).apply {
            action = Constants.ACTION_SHOW_OVERLAY
        }
        ContextCompat.startForegroundService(context, i)
    }

    fun hide(context: Context) {
        context.startService(Intent(context, MyraOverlayService::class.java).apply {
            action = Constants.ACTION_HIDE_OVERLAY
        })
    }

    fun toggle(context: Context) {
        val i = Intent(context, MyraOverlayService::class.java).apply {
            action = Constants.ACTION_TOGGLE_OVERLAY
        }
        ContextCompat.startForegroundService(context, i)
    }

    fun isRunning() = MyraOverlayService.isRunning
}
