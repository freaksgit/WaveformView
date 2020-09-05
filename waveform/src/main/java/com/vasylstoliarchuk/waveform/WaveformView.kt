package com.vasylstoliarchuk.waveform

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.min


class WaveformView : View {
    private var scaleFactor = 0.90f
    var data: Array<Float> = emptyArray()
        set(value) {
            field = value
            requestLayout()
        }

    private var drawingPath: Path = Path()
    private var barWidth: Int = 3
    private var barSpacing: Int = 3
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
        strokeCap = Paint.Cap.ROUND
    }

    private val paintRight: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
    }

    private val clipRect = Rect()

    private val desiredWidth: Int
        get() = data.size * (barWidth + barSpacing) - barSpacing

    private val desiredHeight = resources.getDimensionPixelSize(R.dimen.waveform_default_height)

    var waveformChangeListener: WaveformChangeListener? = null

    private var fromUser = false

    var progress: Float = 0f
        set(value) {
            val validatedValue = when {
                value > 1f -> 1f
                value < 0 -> 0f
                else -> value
            }

            field = validatedValue
            offsetX = -desiredWidth * validatedValue
            waveformChangeListener?.onProgressChanged(this, value, fromUser)
        }
        get() {
            return abs(offsetX / desiredWidth)
        }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        attrs ?: return

        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveformView, defStyleAttr, 0)

        try {
            paintLeft.color = a.getColor(R.styleable.WaveformView_barColorAccent, ContextCompat.getColor(context, R.color.waveform_default_bar_color_accent))
            paintRight.color = a.getColor(R.styleable.WaveformView_barColorNormal, ContextCompat.getColor(context, R.color.waveform_default_bar_color_normal))
            scaleFactor = a.getFraction(R.styleable.WaveformView_scaleFactor, 1, 1, 0.90f)
            barWidth = a.getDimensionPixelSize(R.styleable.WaveformView_barWidth, resources.getDimensionPixelSize(R.dimen.waveform_default_bar_width))
            barSpacing = a.getDimensionPixelSize(R.styleable.WaveformView_barSpacing, resources.getDimensionPixelSize(R.dimen.waveform_default_bar_spacing))
        } finally {
            a.recycle()
        }
    }

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
                fromUser = true
                waveformChangeListener?.onStartTrackingTouch(this)
                curX = event.x
                scale(1 / scaleFactor)
                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {
                fromUser = false
                scale(scaleFactor)
                invalidate()
                waveformChangeListener?.onStopTrackingTouch(this)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                offsetX += event.x - curX
                updateProgress()
                curX = event.x
                return true
            }
            else -> false
        }
    }

    private fun updateProgress() {
        progress = abs(offsetX / desiredWidth)
    }
}