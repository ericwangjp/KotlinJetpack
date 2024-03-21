package com.example.kotlinjetpack.view.exercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

/**
 * desc: UnionView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/2/27 19:28
 */
class UnionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
    }

    private val rect1 by lazy {
        Rect(10, 10, 20, 20)
    }

    private val rect2 by lazy {
        Rect(100, 100, 110, 110)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = Color.RED
        canvas.drawRect(rect1, mPaint)
        mPaint.color = Color.GREEN
        canvas.drawRect(rect2, mPaint)
        mPaint.color = Color.YELLOW
        rect1.union(rect2)
        canvas.drawRect(rect1, mPaint)
    }
}