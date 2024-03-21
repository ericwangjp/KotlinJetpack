package com.example.kotlinjetpack.view.exercise

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.kotlinjetpack.R

/**
 * desc: CusTranslateView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/3/5 19:20
 */
class CusTranslateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = 10f
            color = Color.GREEN
        }
    }

    private var mBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_green_plants)

    private val rect1 by lazy {
        Rect(0, 0, 300, 300)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(300f, 300f, 150f, mPaint)
        canvas.translate(300f, 300f)
        canvas.drawRect(rect1, mPaint)
        mPaint.color = Color.LTGRAY
        canvas.drawCircle(300f, 300f, 150f, mPaint)
    }
}