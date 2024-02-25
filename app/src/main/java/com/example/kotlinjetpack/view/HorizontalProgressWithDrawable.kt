package com.example.kotlinjetpack.view

/**
 * desc: HorizontalProgressWithDrawable
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/7/2 3:01 下午
 */

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.ProgressBar
import com.example.kotlinjetpack.R


/**
 * createtime:2019/8/12
 * author:Yang
 * describe:
 */
class HorizontalProgressWithDrawable @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    ProgressBar(context, attrs, defStyle) {

    private val progressPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
        }
    }
    private var mRealWidth = 0F
    private val bitmap by lazy {
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_cloud)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
        mRealWidth = (measuredWidth - paddingLeft - paddingRight).toFloat()
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(paddingLeft.toFloat(), (height / 2).toFloat())
        val radio = progress * 1.0f / max
        var needDrawBg = false
        // 当前进度
        var progressX = radio * mRealWidth
        if (progressX + bitmap.width + progressPaint.strokeWidth > mRealWidth) {
//            progressX = mRealWidth - bitmap.width
            needDrawBg = true
        }
        // 绘制背景
//        progressPaint.color = Color.GRAY
//        progressPaint.strokeWidth = 25f
//        progressPaint.style = Paint.Style.STROKE
//        progressPaint.strokeCap = Paint.Cap.ROUND
//        canvas.drawLine(progressPaint.strokeWidth / 2, 0f, progressX, 0f, progressPaint)
        //绘制进度
        if (progressX > 0) {
            progressPaint.color = Color.GREEN
            progressPaint.strokeWidth = 25f
            progressPaint.style = Paint.Style.STROKE
            progressPaint.strokeCap = Paint.Cap.ROUND
            canvas.drawLine(progressPaint.strokeWidth / 2, 0f, progressX, 0f, progressPaint)
        }
        //绘制图片
        if (progressX + bitmap.width > mRealWidth) {
            canvas.drawBitmap(
                bitmap,
                mRealWidth - bitmap.width,
                -(bitmap.height) / 2f,
                progressPaint
            )
        } else {
            canvas.drawBitmap(bitmap, progressX, -(bitmap.height) / 2f, progressPaint)
        }

        //绘制背景
        if (!needDrawBg) {
            progressPaint.color = Color.YELLOW
            val start = progressX + bitmap.width + progressPaint.strokeWidth / 2
            progressPaint.strokeWidth = 25f
            progressPaint.style = Paint.Style.STROKE
            progressPaint.strokeCap = Paint.Cap.ROUND
            canvas.drawLine(
                start,
                0f,
                mRealWidth - progressPaint.strokeWidth/2,
                0f,
                progressPaint
            )
        }
        canvas.restore()
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        Log.e("bitmap height: ", "-->${bitmap.height}")
        return when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> paddingTop + paddingBottom + (bitmap?.height
                ?: 0)
            else -> paddingTop + paddingBottom + (bitmap?.height
                ?: 0)
        }
    }

}

