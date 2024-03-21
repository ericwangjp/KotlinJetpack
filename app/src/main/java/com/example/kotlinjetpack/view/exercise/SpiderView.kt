package com.example.kotlinjetpack.view.exercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * desc: SpiderView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/2/29 19:50
 */
class SpiderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val gridPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = 8f
        }
    }
    private val dataPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLUE
        }
    }
    private val textPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.rgb(155, 55, 155)
            textSize = 60f
            typeface = mTypeface
        }
    }

    private val gridPath by lazy {
        Path()
    }

    private val linePath by lazy {
        Path()
    }

    private val dataPath by lazy {
        Path()
    }
    private val mTypeface: Typeface by lazy {
        Typeface.createFromAsset(context.assets, "fonts/zhuxiatingxiaomeng.ttf")
    }
    private var radius: Float = 100f
    private var centerX: Int = 200
    private var centerY: Int = 200
    private val count: Int = 9
    private val data = listOf(2, 5, 1, 6, 5, 4, 3, 7)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = min(h, w) / 2 * 0.9f
        centerX = w / 2
        centerY = h / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
//        drawLines(canvas)
        drawRegion(canvas)
        drawText(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val angle = 360 / count
        val r: Float = radius / count
        // 中心点不用绘制
        for (i in 1..count) {
            val curR = r * i
            gridPath.reset()
            (0 until count).forEach { j ->
                val x = centerX + curR * cos(Math.toRadians(angle * j.toDouble()))
                val y = centerY + curR * sin(Math.toRadians(angle * j.toDouble()))

                if (j == 0) {
                    gridPath.moveTo(centerX + curR, centerY.toFloat())
                } else {
                    gridPath.lineTo(x.toFloat(), y.toFloat())
                }


//                if (j == 0) {
//                    gridPath.moveTo(centerX + curR, centerY.toFloat())
//                } else {
//                    val x = centerX + curR * cos(Math.toRadians(angle * j.toDouble()))
//                    val y = centerY + curR * sin(Math.toRadians(angle * j.toDouble()))
//                    gridPath.lineTo(x.toFloat(), y.toFloat())
//                }

                // 绘制中心线
                if (i == count) {
                    Log.e("TAG", "curR: $curR - radius:$radius")
                    linePath.reset()
                    linePath.moveTo(centerX.toFloat(), centerY.toFloat())
//                    val rx = centerX + curR * cos(Math.toRadians(angle * j.toDouble()))
//                    val ry = centerY + curR * sin(Math.toRadians(angle * j.toDouble()))
                    linePath.lineTo(
                        x.toFloat(), y.toFloat()
                    )
                    canvas.drawPath(linePath, gridPaint)
                }

            }
            gridPath.close()
            canvas.drawPath(gridPath, gridPaint)

            // 绘制文本
            if (i == count) {
                canvas.drawTextOnPath(
                    "床前明月光，疑是地上霜，举头望明月，低头思故乡。",
                    gridPath,
                    0f,
                    0f,
                    textPaint
                )
            }
        }
    }

    private fun drawLines(canvas: Canvas) {
        (0 until count).forEach {
            linePath.reset()
            linePath.moveTo(centerX.toFloat(), centerY.toFloat())
//            linePath.lineTo()
        }
    }

    private fun drawRegion(canvas: Canvas) {
        val angle = 360 / count
        val dr: Float = radius / count
        data.forEachIndexed { index, element ->
            val curR = dr * element
            val x = centerX + curR * cos(Math.toRadians(angle * index.toDouble())).toFloat()
            val y = centerY + curR * sin(Math.toRadians(angle * index.toDouble())).toFloat()
            if (index == 0) {
                dataPath.moveTo(x, y)
            } else {
                dataPath.lineTo(x, y)
            }
            canvas.drawCircle(x, y, 10f, dataPaint)
        }
//        dataPath.close()
        dataPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 8f
            alpha = (255 * 0.2).toInt()
        }
        canvas.drawPath(dataPath, dataPaint)
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText("床前明月光，疑是地上霜", 10f, 90f, textPaint)
    }
}