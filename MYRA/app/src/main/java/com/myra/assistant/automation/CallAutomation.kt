package com.myra.assistant.automation

import android.content.Context
import android.content.Intent
import android.net.Uri

object CallAutomation {

    fun makeCall(context: Context, number: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$number")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}