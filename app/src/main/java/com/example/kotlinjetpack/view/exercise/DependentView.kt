package com.example.kotlinjetpack.view.exercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat

/**
 * desc: DependentView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/3/11 17:13
 */
class DependentView @JvmOverloads constructor(
    private val mContext: Context,
    val attributeSet: AttributeSet? = null,
    private val flag: Int = 0
) : View(mContext, attributeSet, flag) {

    private val paint: Paint by lazy {
        Paint().apply {
            color = Color.parseColor("#0000FF")
            style = Paint.Style.FILL
        }
    }
    private var mStartX = 0
    private var mStartY = 0
    private val mRect: Rect by lazy {
        Rect().apply {
            left = 200
            top = 200
            right = 400
            bottom = 400
        }
    }

    init {
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.e("TAG", "onDraw")
        canvas.drawRect(
            mRect,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("TAG", "ACTION_DOWN")
                mStartX = event.rawX.toInt()
                mStartY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("TAG", "ACTION_MOVE")

                val endX = event.rawX.toInt()
                val endY = event.rawY.toInt()
                val dx = endX - mStartX
                val dy = endY - mStartY

                ViewCompat.offsetTopAndBottom(this, dy)
                ViewCompat.offsetLeftAndRight(this, dx)
                postInvalidate()

                mStartX = endX
                mStartY = endY
            }
        }

        return super.onTouchEvent(event)
    }

}