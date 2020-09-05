package com.vasylstoliarchuk.waveform

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.min


class WaveformView : View {
    private val TAG = WaveformView::class.java.simpleName

    private var scaleFactor = 0.95f
    var data: FloatArray = FloatArray(0)
        set(value) {
            field = value
            requestLayout()
        }

    private var drawingPath: Path = Path()
    private val barWidth: Int = 10
    private val barSpacing: Int = 10
    private var offsetX = 0f
        set(value) {
            Log.d(TAG, "setOffsetX($value)")
            val validatedValue = when {
                value > 0 -> 0f
                value < -desiredWidth -> -desiredWidth.toFloat()
                else -> value
            }
            drawingPath.offset(validatedValue - field, 0f)
            field = validatedValue
            invalidate()
        }

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    private val scaleMatrix = Matrix()
    private val scaleRectF = RectF()

    private val desiredWidth: Int
        get() = data.size * (barWidth + barSpacing) - barSpacing

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        measureDrawingPath()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 100

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize;
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize);
            else -> desiredWidth;
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize;
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize);
            else -> desiredHeight;
        }

        setMeasuredDimension(width, height);
    }


    private fun measureDrawingPath() {
        drawingPath.reset()
        data.forEachIndexed { index, amp ->
            val middle = height / 2
            val barLeft = index * (barWidth + barSpacing).toFloat()
            val scaledHeight = height * amp / 2
            val barTop = middle - scaledHeight
            val barBottom = middle + scaledHeight
            val barRight = barLeft + barWidth

            drawingPath.addRect(barLeft, barTop, barRight, barBottom, Path.Direction.CW)
        }
        scale(scaleFactor)
        drawingPath.offset(width / 2f + offsetX, 0f)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(drawingPath, paint)
    }

    private fun scale(scaleFactor: Float) {
        drawingPath.computeBounds(scaleRectF, true)
        val startMatrix = Matrix(scaleMatrix)
        scaleMatrix.setScale(1.0f, scaleFactor, scaleRectF.centerX(), scaleRectF.centerY())
        startAnimation(MatrixAnimation(startMatrix, scaleMatrix).apply {
            duration = 100
            interpolator = DecelerateInterpolator()
        })
    }

    private var dx = 0f
    private var x1 = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
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
                dx = event.x - x1
                Log.d(TAG, "event.x=${event.x}, x1=$x1, dx=$dx")
                x1 = event.x
                offsetX += dx
                return true
            }
            else -> false
        }
    }
}