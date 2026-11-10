package com.myra.assistant.security

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

/**
 * PatternLockView — Custom 3x3 Grid Pattern Lock
 */
class PatternLockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val GRID_SIZE = 3
        const val DOT_COUNT = GRID_SIZE * GRID_SIZE
        const val MIN_PATTERN_LENGTH = 4
        const val DOT_RADIUS_RATIO   = 0.06f
        const val TOUCH_RADIUS_RATIO = 0.12f
    }

    enum class PatternState { NORMAL, SUCCESS, ERROR }

    interface PatternListener {
        fun onPatternStarted()
        fun onPatternComplete(pattern: List<Int>)
        fun onPatternCleared()
    }

    private val dotCenters = Array(DOT_COUNT) { Pair(0f, 0f) }
    private val selectedDots = mutableListOf<Int>()
    private var currentX = -1f
    private var currentY = -1f
    private var state = PatternState.NORMAL
    var listener: PatternListener? = null

    private val dotPaintNormal = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFB0BEC5.toInt()
        style = Paint.Style.FILL
    }
    private val dotPaintSelected = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE91E8C.toInt()
        style = Paint.Style.FILL
    }
    private val dotPaintError = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF1744.toInt()
        style = Paint.Style.FILL
    }
    private val dotPaintSuccess = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00E676.toInt()
        style = Paint.Style.FILL
    }
    private val ringPaintSelected = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x33E91E8C.toInt()
        style = Paint.Style.FILL
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAAE91E8C.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }
    private val lineErrorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAAFF1744.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }
    private val lineSuccessPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAA00E676.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = minOf(w, h).toFloat()
        val cellSize = size / GRID_SIZE
        val startX = (w - size) / 2f
        val startY = (h - size) / 2f
        for (i in 0 until DOT_COUNT) {
            val row = i / GRID_SIZE
            val col = i % GRID_SIZE
            dotCenters[i] = Pair(
                startX + cellSize * col + cellSize / 2f,
                startY + cellSize * row + cellSize / 2f
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = minOf(width, height).toFloat()
        val dotRadius = size * DOT_RADIUS_RATIO
        val ringRadius = size * TOUCH_RADIUS_RATIO * 0.7f
        val activeLine = when (state) {
            PatternState.SUCCESS -> lineSuccessPaint
            PatternState.ERROR   -> lineErrorPaint
            PatternState.NORMAL  -> linePaint
        }
        if (selectedDots.size > 1) {
            val path = Path()
            val first = dotCenters[selectedDots[0]]
            path.moveTo(first.first, first.second)
            for (i in 1 until selectedDots.size) {
                val pt = dotCenters[selectedDots[i]]
                path.lineTo(pt.first, pt.second)
            }
            canvas.drawPath(path, activeLine)
        }
        if (selectedDots.isNotEmpty() && currentX >= 0 && state == PatternState.NORMAL) {
            val last = dotCenters[selectedDots.last()]
            canvas.drawLine(last.first, last.second, currentX, currentY, linePaint)
        }
        for (i in 0 until DOT_COUNT) {
            val (cx, cy) = dotCenters[i]
            val isSelected = selectedDots.contains(i)
            val dotPaint = when {
                !isSelected -> dotPaintNormal
                state == PatternState.ERROR   -> dotPaintError
                state == PatternState.SUCCESS -> dotPaintSuccess
                else -> dotPaintSelected
            }
            if (isSelected && state == PatternState.NORMAL) {
                canvas.drawCircle(cx, cy, ringRadius, ringPaintSelected)
            }
            canvas.drawCircle(cx, cy, dotRadius, dotPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (state != PatternState.NORMAL) return true
        val size = minOf(width, height).toFloat()
        val touchRadius = size * TOUCH_RADIUS_RATIO
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clearPatternInternal()
                handleTouch(event.x, event.y, touchRadius)
            }
            MotionEvent.ACTION_MOVE -> {
                currentX = event.x
                currentY = event.y
                handleTouch(event.x, event.y, touchRadius)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                currentX = -1f
                currentY = -1f
                finishPattern()
            }
        }
        return true
    }

    private fun handleTouch(x: Float, y: Float, touchRadius: Float) {
        val dotIndex = findNearestDot(x, y, touchRadius) ?: return
        if (!selectedDots.contains(dotIndex)) {
            if (selectedDots.isEmpty()) listener?.onPatternStarted()
            selectedDots.add(dotIndex)
            invalidate()
        }
    }

    private fun findNearestDot(x: Float, y: Float, radius: Float): Int? {
        for (i in 0 until DOT_COUNT) {
            val (cx, cy) = dotCenters[i]
            val dist = sqrt(((x - cx) * (x - cx) + (y - cy) * (y - cy)).toDouble()).toFloat()
            if (dist <= radius) return i
        }
        return null
    }

    private fun finishPattern() {
        invalidate()
        if (selectedDots.size >= MIN_PATTERN_LENGTH) {
            listener?.onPatternComplete(selectedDots.toList())
        } else if (selectedDots.isNotEmpty()) {
            showError()
        }
    }

    fun showError() {
        state = PatternState.ERROR
        invalidate()
        postDelayed({
            clearPatternInternal()
            state = PatternState.NORMAL
            invalidate()
        }, 800)
    }

    fun showSuccess() {
        state = PatternState.SUCCESS
        invalidate()
        postDelayed({
            clearPatternInternal()
            state = PatternState.NORMAL
            invalidate()
        }, 600)
    }

    fun clearPattern() {
        clearPatternInternal()
        state = PatternState.NORMAL
        invalidate()
        listener?.onPatternCleared()
    }

    private fun clearPatternInternal() {
        selectedDots.clear()
        currentX = -1f
        currentY = -1f
    }

    fun getPattern(): List<Int> = selectedDots.toList()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = minOf(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        )
    }
}
