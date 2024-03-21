package com.example.kotlinjetpack.view.exercise

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * desc: GestureContainView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/2/27 11:53
 */
class GestureContainView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    private val mPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
    }

    private val mRect by lazy {
        Rect(100, 10, 500, 300)
    }

    private var mX = -1
    private var mY = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mX = (event?.x ?: -1).toInt()
        mY = (event?.y ?: -1).toInt()
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                mX = -1
                mY = -1
            }
        }
        postInvalidate()
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mRect.contains(mX, mY)) {
            mPaint.color = Color.RED
        } else {
            mPaint.color = Color.GREEN
        }
        canvas.drawRect(mRect, mPaint)
    }

}