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
 * desc: CusCircleHeader
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/3/6 15:43
 */
class CusCircleHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = 10f
            color = Color.GREEN
        }
    }

    private val mBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_green_plants)

    private val mPath by lazy {
        Path().apply {
            addCircle(
                mBitmap.width / 2f,
                mBitmap.height / 2f,
                mBitmap.height / 2f,
                Path.Direction.CCW
            )
        }
    }

    init {
        // 据说不关闭硬件加速不会有效果，实测不关闭效果正常
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.clipPath(mPath)
        canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
        canvas.restore()
        canvas.drawRect(300f, 300f, 600f, 600f, mPaint)
    }
}