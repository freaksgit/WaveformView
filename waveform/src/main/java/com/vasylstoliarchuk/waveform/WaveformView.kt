package com.vasylstoliarchuk.waveform

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.min


class WaveformView : View {
    private val TAG = WaveformView::class.java.simpleName

    private var scaleFactor = 0.95f
    var data: Array<Float> = emptyArray()
        set(value) {
            field = value
            requestLayout()
        }

    private var drawingPath: Path = Path()
    private val barWidth: Int = 3
    private val barSpacing: Int = 3
    private var offsetX = 0f
        set(value) {
            val validatedValue = when {
                value > 0 -> 0f
                value < -desiredWidth -> -desiredWidth.toFloat()
                else -> value
            }
            drawingPath.offset(validatedValue - field, 0f)
            field = validatedValue
            invalidate()
        }

    private val paintLeft: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#fff7d828")
        strokeCap = Paint.Cap.ROUND
    }

    private val paintRight: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#60f7d828")
        strokeCap = Paint.Cap.ROUND
    }

    private val clipRect = Rect()

    private val desiredWidth: Int
        get() = data.size * (barWidth + barSpacing) - barSpacing

    private val desiredHeight = resources.getDimensionPixelSize(R.dimen.waveform_default_bar_height)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        measureDrawingPath()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }


    private fun measureDrawingPath() {
        drawingPath.reset()
        val middle = height / 2

        data.forEachIndexed { index, amp ->
            drawingPath.addRect(
                index * (barWidth + barSpacing).toFloat(),
                middle - height * amp / 2,
                index * (barWidth + barSpacing).toFloat() + barWidth,
                middle + height * amp / 2,
                Path.Direction.CW
            )
        }
        scale(scaleFactor)
        drawingPath.offset(width / 2f + offsetX, 0f)
    }

    override fun onDraw(canvas: Canvas) {
        drawLeftPart(canvas)
        drawRightPart(canvas)
    }

    private fun drawRightPart(canvas: Canvas) {
        clipRect.set(width / 2, 0, width, height)
        drawPart(canvas, paintRight)
    }

    private fun drawLeftPart(canvas: Canvas) {
        clipRect.set(0, 0, width / 2, height)
        drawPart(canvas, paintLeft)
    }

    private fun drawPart(canvas: Canvas, paint: Paint) {
        canvas.save()
        canvas.clipRect(clipRect)
        canvas.drawPath(drawingPath, paint)
        canvas.restore()
    }

    private fun scale(scaleFactor: Float) {
        animate().scaleY(scaleFactor)
            .setDuration(100)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private var curX = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                curX = event.x
                scale(1 / scaleFactor)
                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {
                scale(scaleFactor)
                invalidate()
                true
            }

            MotionEvent.ACTION_MOVE -> {
                offsetX += event.x - curX
                curX = event.x
                return true
            }
            else -> false
        }
    }
}