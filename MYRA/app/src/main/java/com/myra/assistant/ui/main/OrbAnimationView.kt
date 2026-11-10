package com.myra.assistant.ui.main

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.*

class OrbAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // State
    private var isActive = false
    private var isSpeaking = false
    private var isThinking = false
    private var isPulsating = false
    private var speakAmplitude = 0f

    // Animation
    private var rotationAngle = 0f
    private var pulseScale = 1f
    private var glowAlpha = 180
    private var waveOffset = 0f
    private var thinkingAngle = 0f

    // Animators
    private val rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 4000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            rotationAngle = it.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }

    private val pulseAnimator = ValueAnimator.ofFloat(1f, 1.15f, 1f).apply {
        duration = 1500
        repeatCount = ValueAnimator.INFINITE
        interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        addUpdateListener {
            pulseScale = it.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }

    private val glowAnimator = ValueAnimator.ofInt(120, 220, 120).apply {
        duration = 2000
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            glowAlpha = it.animatedValue as Int
            postInvalidateOnAnimation()
        }
    }

    private val waveAnimator = ValueAnimator.ofFloat(0f, (2 * Math.PI).toFloat()).apply {
        duration = 1200
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            waveOffset = it.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }

    private val thinkingAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            thinkingAngle = it.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }

    // Paints (Optimized: hardware acceleration friendly)
    private val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Colors
    private val activeColor = Color.parseColor("#FF1744")
    private val speakColor = Color.parseColor("#E040FB")
    private val thinkColor = Color.parseColor("#40C4FF")
    private val glowColor = Color.parseColor("#FF1744")

    // Particles
    private data class Particle(var angle: Float, var radius: Float, var size: Float, var alpha: Int)
    private val particles = (0..12).map {
        Particle(
            angle = (it * 360f / 12f),
            radius = 0f,
            size = (4..8).random().toFloat(),
            alpha = (100..255).random()
        )
    }

    init {
        // Hardware acceleration enable
        setLayerType(LAYER_TYPE_HARDWARE, null)
        startIdleAnimation()
    }

    private fun startIdleAnimation() {
        pulseAnimator.start()
        glowAnimator.start()
    }

    fun setActive(active: Boolean) {
        isActive = active
        if (active) {
            rotationAnimator.start()
            waveAnimator.start()
        } else {
            rotationAnimator.cancel()
            waveAnimator.cancel()
        }
        postInvalidateOnAnimation()
    }

    fun setSpeaking(speaking: Boolean) {
        isSpeaking = speaking
        if (speaking) {
            waveAnimator.duration = 600
            if (!waveAnimator.isStarted) waveAnimator.start()
        } else {
            waveAnimator.duration = 1200
        }
        postInvalidateOnAnimation()
    }

    fun setThinking(thinking: Boolean) {
        isThinking = thinking
        if (thinking) {
            if (!thinkingAnimator.isStarted) thinkingAnimator.start()
        } else {
            thinkingAnimator.cancel()
        }
        postInvalidateOnAnimation()
    }

    fun setPulsating(pulsating: Boolean) {
        isPulsating = pulsating
        postInvalidateOnAnimation()
    }

    fun setAmplitude(amplitude: Float) {
        speakAmplitude = (amplitude + 10f) / 20f
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val baseRadius = minOf(cx, cy) * 0.55f

        canvas.save()
        canvas.scale(pulseScale, pulseScale, cx, cy)

        drawGlow(canvas, cx, cy, baseRadius)
        drawCoreOrb(canvas, cx, cy, baseRadius)
        drawRings(canvas, cx, cy, baseRadius)

        if (isActive || isSpeaking) {
            drawWaves(canvas, cx, cy, baseRadius)
            drawParticles(canvas, cx, cy, baseRadius)
        }

        if (isThinking) {
            drawThinkingArc(canvas, cx, cy, baseRadius)
        }

        canvas.restore()
        drawInnerHighlight(canvas, cx, cy, baseRadius * pulseScale)
    }

    private fun drawGlow(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val glowRadius = radius * 1.6f
        val color = when {
            isSpeaking -> speakColor
            isThinking -> thinkColor
            isActive -> activeColor
            else -> glowColor
        }
        
        val shader = RadialGradient(
            cx, cy, glowRadius,
            intArrayOf(
                Color.argb(glowAlpha / 3, Color.red(color), Color.green(color), Color.blue(color)),
                Color.TRANSPARENT
            ),
            floatArrayOf(0.3f, 1f),
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = shader
        canvas.drawCircle(cx, cy, glowRadius, glowPaint)
    }

    private fun drawCoreOrb(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val c1 = if (isSpeaking) "#E040FB" else if (isThinking) "#40C4FF" else "#FF1744"
        val c2 = if (isSpeaking) "#FF1744" else if (isThinking) "#00B0FF" else "#D500F9"
        
        val shader = RadialGradient(
            cx - radius * 0.3f, cy - radius * 0.3f, radius,
            intArrayOf(Color.parseColor(c1), Color.parseColor(c2)),
            null,
            Shader.TileMode.CLAMP
        )
        orbPaint.shader = shader
        canvas.drawCircle(cx, cy, radius, orbPaint)
    }

    private fun drawRings(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val oval = RectF()
        for (i in 0 until 2) {
            val r = radius + (i + 1) * 20f
            ringPaint.color = Color.argb(150 - i * 50, 255, 80, 80)
            oval.set(cx - r, cy - r, cx + r, cy + r)
            
            canvas.save()
            canvas.rotate(rotationAngle + i * 45f, cx, cy)
            canvas.drawArc(oval, 0f, 270f, false, ringPaint)
            canvas.restore()
        }
    }

    private val wavePath = Path()
    private fun drawWaves(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val waveCount = 5
        val amplitude = if (isSpeaking) radius * 0.2f * (1f + speakAmplitude) else radius * 0.1f
        val r = radius + 25f

        wavePath.reset()
        for (j in 0..120 step 2) {
            val angle = (j * 3f).toRadians()
            val wave = amplitude * sin(waveCount * angle + waveOffset)
            val x = cx + (r + wave) * cos(angle)
            val y = cy + (r + wave) * sin(angle)
            if (j == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
        }
        wavePath.close()
        
        wavePaint.color = if (isSpeaking) Color.argb(180, 224, 64, 251) else Color.argb(150, 255, 30, 50)
        canvas.drawPath(wavePath, wavePaint)
    }

    private fun drawThinkingArc(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val r = radius + 40f
        val oval = RectF(cx - r, cy - r, cx + r, cy + r)
        ringPaint.color = thinkColor
        canvas.save()
        canvas.rotate(thinkingAngle, cx, cy)
        canvas.drawArc(oval, 0f, 100f, false, ringPaint)
        canvas.drawArc(oval, 180f, 100f, false, ringPaint)
        canvas.restore()
    }

    private fun drawParticles(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        particles.forEach { p ->
            p.angle = (p.angle + 1.5f) % 360f
            val rad = radius + 35f + 10f * sin(p.angle.toRadians() * 2)
            val x = cx + rad * cos(p.angle.toRadians())
            val y = cy + rad * sin(p.angle.toRadians())
            particlePaint.color = if (isSpeaking) speakColor else activeColor
            particlePaint.alpha = p.alpha
            canvas.drawCircle(x, y, p.size * 0.8f, particlePaint)
        }
    }

    private fun drawInnerHighlight(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.drawCircle(cx - radius * 0.2f, cy - radius * 0.2f, radius * 0.4f, 
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(cx - radius * 0.25f, cy - radius * 0.25f, radius * 0.5f,
                    intArrayOf(Color.argb(100, 255, 255, 255), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
            })
    }

    private fun Float.toRadians() = this * (PI / 180f).toFloat()

    override fun onDetachedFromWindow() {
        listOf(rotationAnimator, pulseAnimator, glowAnimator, waveAnimator, thinkingAnimator).forEach { it.cancel() }
        super.onDetachedFromWindow()
    }
}
