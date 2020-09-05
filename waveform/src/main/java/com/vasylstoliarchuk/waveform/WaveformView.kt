package com.vasylstoliarchuk.waveform

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveformView : View {

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
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(drawingPath, paint)
    }
}