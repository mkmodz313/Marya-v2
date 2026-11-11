package com.myra.assistant.ui.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.myra.assistant.R
import com.myra.assistant.utils.prefs

/**
 * Modern neon "Welcome to MKMODZ" dialog with the WhatsApp channel link.
 * Shown on every fresh app start (once per process) until the user ticks "Dobara mat dikhao".
 */
object WelcomeDialog {

    const val CHANNEL_URL = "https://whatsapp.com/channel/0029VbDRuV990x2tlVt0FW2Y"
    private const val PREF_HIDE = "welcome_dialog_hide"

    @Volatile private var shownThisProcess = false

    fun showIfNeeded(activity: Activity) {
        if (shownThisProcess) return
        if (activity.isFinishing || activity.isDestroyed) return
        if (activity.prefs().getBoolean(PREF_HIDE, false)) return
        shownThisProcess = true
        try { show(activity) } catch (_: Throwable) { }
    }

    fun show(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_welcome)
        dialog.setCancelable(true)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.78f)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val dm = activity.resources.displayMetrics
            val maxW = (420 * dm.density).toInt()
            setLayout(minOf((dm.widthPixels * 0.94f).toInt(), maxW), WindowManager.LayoutParams.WRAP_CONTENT)
        }

        val card = dialog.findViewById<View>(R.id.wlCard)
        val ring = dialog.findViewById<View>(R.id.wlRing)
        val portrait = dialog.findViewById<View>(R.id.wlPortrait)
        val crown = dialog.findViewById<View>(R.id.wlCrown)
        val title = dialog.findViewById<TextView>(R.id.wlTitle)
        val dont = dialog.findViewById<CheckBox>(R.id.wlDontShow)

        // circular portrait
        portrait.clipToOutline = true
        portrait.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }

        // golden gradient on the MKMODZ title
        title.post {
            val w = title.width.toFloat().coerceAtLeast(1f)
            title.paint.shader = LinearGradient(
                0f, 0f, w, title.height.toFloat(),
                intArrayOf(Color.parseColor("#FFFFF1B0"), Color.parseColor("#FFFFC933"), Color.parseColor("#FFE08A00")),
                null, Shader.TileMode.CLAMP
            )
            title.invalidate()
        }

        fun finish() {
            if (dont.isChecked) activity.prefs().edit().putBoolean(PREF_HIDE, true).apply()
            try { dialog.dismiss() } catch (_: Throwable) { }
        }

        dialog.findViewById<View>(R.id.wlJoin).setOnClickListener {
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CHANNEL_URL)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (_: Throwable) {
                Toast.makeText(activity, "Link khul nahi paya: $CHANNEL_URL", Toast.LENGTH_LONG).show()
            }
        }
        dialog.findViewById<View>(R.id.wlStart).setOnClickListener { finish() }
        dialog.findViewById<View>(R.id.wlClose).setOnClickListener { finish() }
        dialog.setOnCancelListener { finish() }

        // entrance + gentle glow pulse
        card.alpha = 0f
        card.scaleX = 0.86f
        card.scaleY = 0.86f
        card.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(420)
            .setInterpolator(OvershootInterpolator(1.1f)).start()
        crown.translationY = -40f
        crown.alpha = 0f
        crown.animate().translationY(0f).alpha(1f).setStartDelay(220).setDuration(450)
            .setInterpolator(DecelerateInterpolator()).start()

        val pulse = ObjectAnimator.ofFloat(ring, View.ALPHA, 1f, 0.55f).apply {
            duration = 1300
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
        dialog.setOnDismissListener { pulse.cancel() }

        dialog.show()
    }
}
