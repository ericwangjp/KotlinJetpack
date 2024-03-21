package com.example.kotlinjetpack.view.exercise

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.example.kotlinjetpack.R

/**
 * desc: ClipRgnView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/3/7 19:12
 */
class ClipRgnView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = 10f
            color = Color.GREEN
        }
    }

    private val mPath by lazy {
        Path()
    }

    private val mBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_green_plants).apply {
            mWidth = width
            mHeight = height
        }
    private val clipHeight = 20
    private var mClipWidth = 0
    private var mWidth = 0
    private var mHeight = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPath.reset()
        var i = 0
        while (i * clipHeight < mHeight) {
            if (i % 2 == 0) {
                mPath.addRect(
                    0f,
                    i * clipHeight.toFloat(),
                    mClipWidth.toFloat(),
                    i * clipHeight + clipHeight.toFloat(),
                    Path.Direction.CCW
                )
            } else {
                mPath.addRect(
                    mWidth - mClipWidth.toFloat(),
                    i * clipHeight.toFloat(),
                    mWidth.toFloat(),
                    i * clipHeight + clipHeight.toFloat(),
                    Path.Direction.CCW
                )
            }
            i++
        }
        canvas.clipPath(mPath)
        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
        if (mClipWidth > mWidth){
            return
        }
        mClipWidth += 10
        postInvalidateDelayed(100)
    }

}