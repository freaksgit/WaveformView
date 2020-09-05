package com.vasylstoliarchuk.waveform

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


class WaveformView : View {
    private var scaleFactor = 0.9f
    private val myListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            scale(1 / scaleFactor)
            invalidate()
            return true
        }
    }
    private val detector: GestureDetector = GestureDetector(context, myListener)

    var data: FloatArray = FloatArray(0)
        set(value) {
            field = value
            requestLayout()
        }

    private var drawingPath: Path = Path()
    private val barWidth: Int = 10
    private val barSpacing: Int = 10

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    private val scaleMatrix = Matrix()
    private val scaleRectF = RectF()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        measureDrawingPath()
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
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(drawingPath, paint)
    }

    private fun scale(scaleFactor: Float) {
        drawingPath.computeBounds(scaleRectF, true)
        scaleMatrix.setScale(scaleFactor, scaleFactor, scaleRectF.centerX(), scaleRectF.centerY())
        drawingPath.transform(scaleMatrix)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event).let { result ->
            if (!result) {
                if (event.action == MotionEvent.ACTION_UP) {
                    scale(scaleFactor)
                    invalidate()
                    true
                } else false
            } else true
        }
    }
}