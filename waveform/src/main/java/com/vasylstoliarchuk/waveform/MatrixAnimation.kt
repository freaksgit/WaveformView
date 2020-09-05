package com.vasylstoliarchuk.waveform

import android.graphics.Matrix
import android.graphics.PointF
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation

class MatrixAnimation @JvmOverloads constructor(startMatrix: Matrix, endMatrix: Matrix, private var animated: Boolean = true) : Animation() {
    private val scaleStart: PointF
    private val scaleEnd: PointF
    private val translateStart: PointF
    private val translateEnd: PointF

    init {
        setAnimated(animated)
        val a = FloatArray(9)
        val b = FloatArray(9)
        startMatrix.getValues(a)
        endMatrix.getValues(b)
        scaleStart = PointF(a[Matrix.MSCALE_X], a[Matrix.MSCALE_Y])
        scaleEnd = PointF(b[Matrix.MSCALE_X], b[Matrix.MSCALE_Y])
        translateStart = PointF(a[Matrix.MTRANS_X], a[Matrix.MTRANS_Y])
        translateEnd = PointF(b[Matrix.MTRANS_X], b[Matrix.MTRANS_Y])
        fillAfter = true
    }

    fun setAnimated(animated: Boolean): MatrixAnimation {
        this.animated = animated
        duration = if (animated) 300 else 0.toLong()
        return this
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        val matrix = t.matrix
        val sFactor = PointF(
            scaleEnd.x * interpolatedTime / scaleStart.x + 1 - interpolatedTime,
            scaleEnd.y * interpolatedTime / scaleStart.y + 1 - interpolatedTime
        )
        val tFactor = PointF(
            (translateEnd.x - translateStart.x) * interpolatedTime,
            (translateEnd.y - translateStart.y) * interpolatedTime
        )

        matrix.postScale(scaleStart.x, scaleStart.y, 0f, 0f)
        matrix.postScale(sFactor.x, sFactor.y, 0f, 0f)
        matrix.postTranslate(translateStart.x, translateStart.y)
        matrix.postTranslate(tFactor.x, tFactor.y)
    }

    override fun start() {
        setAnimated(true)
        super.start()
    }

    fun start(animated: Boolean) {
        setAnimated(animated)
        super.start()
    }


}