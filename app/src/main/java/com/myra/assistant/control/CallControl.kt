package com.myra.assistant.control

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.myra.assistant.service.SmartAccessibilityEngine
import com.myra.assistant.utils.Logger
import kotlinx.coroutines.delay

/**
 * Real phone-call control by voice: answer, reject / end, speaker, and "talk on the phone" mode
 * (MYRA answers, switches to speaker and talks to the caller through the speaker + microphone).
 *
 * Android does not let a normal app tap into the call audio stream directly, so talk mode works over the
 * speakerphone (caller's voice comes out of the speaker -> MYRA's mic hears it; MYRA's voice goes out of the
 * speaker -> the caller hears it). Quality depends on the phone model / Android version.
 */
object CallControl {
    private const val TAG = "CALL_CTRL"
    private val main = Handler(Looper.getMainLooper())

    @Volatile var talkMode = false
    /** Set by the spoken-command fast path; cleared when the model's own tool call arrives (or a fallback prompt is sent). */
    @Volatile var talkPromptPending = false

    private fun has(ctx: Context, p: String) = ContextCompat.checkSelfPermission(ctx, p) == PackageManager.PERMISSION_GRANTED

    /** CALL_STATE_IDLE / RINGING / OFFHOOK (or -1 when unknown). */
    fun callState(ctx: Context): Int = try {
        if (!has(ctx, Manifest.permission.READ_PHONE_STATE)) -1
        else (ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).callState
    } catch (_: Throwable) { -1 }

    fun isRinging(ctx: Context) = callState(ctx) == TelephonyManager.CALL_STATE_RINGING
    fun isInCall(ctx: Context) = callState(ctx) == TelephonyManager.CALL_STATE_OFFHOOK

    /** Answers the ringing call. Returns a "success: ..." / "failed: ..." string for the voice model. */
    fun answer(ctx: Context): String {
        val state = callState(ctx)
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) return "success: a call is already connected"
        var done = false
        try {
            if (has(ctx, Manifest.permission.ANSWER_PHONE_CALLS)) {
                (ctx.getSystemService(Context.TELECOM_SERVICE) as TelecomManager).acceptRingingCall()
                done = true
            }
        } catch (e: Throwable) { Logger.e(TAG, "acceptRingingCall failed: ${e.message}") }
        if (!done) {
            // fallback: tap the on-screen Answer button through the accessibility service
            done = try {
                SmartAccessibilityEngine.click(text = "Answer") || SmartAccessibilityEngine.click(contentDesc = "Answer") ||
                    SmartAccessibilityEngine.click(text = "Accept") || SmartAccessibilityEngine.click(contentDesc = "Accept") ||
                    SmartAccessibilityEngine.click(text = "उत्तर दें") || SmartAccessibilityEngine.click(text = "Pick up")
            } catch (_: Throwable) { false }
        }
        return when {
            done -> "success: call answered"
            state == TelephonyManager.CALL_STATE_IDLE -> "failed: there is no incoming call right now"
            !has(ctx, Manifest.permission.ANSWER_PHONE_CALLS) ->
                "failed: the phone-call permission is not granted. Tell the user to allow 'Phone' permission for MYRA in app settings."
            else -> "failed: could not answer the call"
        }
    }

    /** Rejects a ringing call or ends the current call. */
    fun end(ctx: Context): String {
        val state = callState(ctx)
        if (state == TelephonyManager.CALL_STATE_IDLE) return "failed: there is no call right now"
        var done = false
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && has(ctx, Manifest.permission.ANSWER_PHONE_CALLS)) {
                done = (ctx.getSystemService(Context.TELECOM_SERVICE) as TelecomManager).endCall()
            }
        } catch (e: Throwable) { Logger.e(TAG, "endCall failed: ${e.message}") }
        if (!done) {
            done = try {
                SmartAccessibilityEngine.click(text = "Decline") || SmartAccessibilityEngine.click(contentDesc = "Decline") ||
                    SmartAccessibilityEngine.click(text = "End call") || SmartAccessibilityEngine.click(contentDesc = "End call") ||
                    SmartAccessibilityEngine.click(text = "Reject")
            } catch (_: Throwable) { false }
        }
        if (done) setSpeaker(ctx, false)
        talkMode = false
        return if (done) "success: call ended" else "failed: could not end the call"
    }

    @Suppress("DEPRECATION")
    private fun legacySpeaker(am: AudioManager, on: Boolean) { am.isSpeakerphoneOn = on }

    fun setSpeaker(ctx: Context, on: Boolean): Boolean {
        return try {
            val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (on) {
                    val dev = am.availableCommunicationDevices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
                    if (dev != null) am.setCommunicationDevice(dev) else legacySpeaker(am, true)
                } else {
                    am.clearCommunicationDevice()
                    legacySpeaker(am, false)
                }
            } else {
                legacySpeaker(am, on)
            }
            true
        } catch (e: Throwable) { Logger.e(TAG, "speaker failed: ${e.message}"); false }
    }

    /**
     * "Talk on the phone": answer (if it is still ringing), speaker ON, and tell the model to talk to the caller.
     * Watches the call and switches the speaker off again when the call ends.
     */
    suspend fun talk(ctx: Context): String {
        talkPromptPending = false
        if (isRinging(ctx)) {
            val r = answer(ctx)
            if (r.startsWith("failed")) return r
            delay(1200)
        } else if (!isInCall(ctx)) {
            return "failed: there is no incoming or active call to talk on"
        }
        val spk = setSpeaker(ctx, true)
        talkMode = true
        watchUntilCallEnds(ctx)
        return "success: call connected" + (if (spk) ", speakerphone ON" else ", but speaker could not be switched on") +
            ". You are now speaking DIRECTLY to the CALLER on a live phone call (their voice reaches you through the microphone). " +
            "Greet them in their language, e.g. 'Hello, main MYRA bol rahi hoon, <user> ki assistant. Boliye, kya baat hai?'. " +
            "Keep sentences short, listen, take a message and ask who is calling. Never share the user's private info, passwords, OTPs or money details. " +
            "If the caller wants the user, say you will pass the message. When the caller says goodbye, say bye and call end_call."
    }

    private var watching = false
    private fun watchUntilCallEnds(ctx: Context) {
        if (watching) return
        watching = true
        val app = ctx.applicationContext
        val r = object : Runnable {
            override fun run() {
                if (callState(app) == TelephonyManager.CALL_STATE_IDLE) {
                    setSpeaker(app, false)
                    talkMode = false
                    watching = false
                    return
                }
                main.postDelayed(this, 2000)
            }
        }
        main.postDelayed(r, 3000)
    }
}
