package com.example.kotlinjetpack.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * desc: CustomSpiderView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/3/12 14:25
 */
class CustomSpiderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        // 半径
        const val SMALL_RADIUS = 200f

        // 几边形
        const val COUNT = 10

        // 有几条边
        const val NUMBER = 7

        // 每一条边的间隔
        const val INTERVAL = 40

        // 实际业务数据
        var data = listOf(3, 2, 4, 1, 7,5,4)
    }

    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GRAY
            style = Paint.Style.FILL
        }
    }

    private val path by lazy {
        Path()
    }

    private val textRect by lazy {
        Rect()
    }

    // 中心位置
    private val centerLocation by lazy {
        PointF(width / 2f, height / 2f)
    }

    override fun onDraw(canvas: Canvas) {
        drawGrid(canvas)
        drawAreaData(canvas)
    }

    private fun drawAreaData(canvas: Canvas) {
        data.forEachIndexed { index, value ->
            val location = getLocation(index, value)

            if (index == 0) {
                path.moveTo(location.x, location.y)
            } else {
                path.lineTo(location.x, location.y)
            }
        }
        path.close()


        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        canvas.drawPath(path, paint) // 绘制边

        paint.style = Paint.Style.FILL
        paint.alpha = (255 * 0.1).toInt()
        canvas.drawPath(path, paint) // 绘制内边
        path.reset()
    }

    private fun drawGrid(canvas: Canvas) {
        val cx = centerLocation.x
        val cy = centerLocation.y
        // 辅助圆
//        canvas.drawCircle(cx, cy, SMALL_RADIUS, paint)

        // 每一个的间隔
        val eachAngle = 360 / COUNT

        (0 until NUMBER).forEachIndexed { index, element ->
            (0 until COUNT).forEach { count ->
                val angle = count * eachAngle.toDouble()
                // 半径 = 当前是第几条边 * 间距 + 最中间的距离
                val radius = element * INTERVAL + SMALL_RADIUS
                Log.e(
                    "wjp",
                    "onDraw: $angle - ${Math.toRadians(angle)} - cos: ${cos(Math.toRadians(angle))}"
                )
                val x =
                    (radius * cos(Math.toRadians(angle)) + centerLocation.x).toFloat()
                val y =
                    (radius * sin(Math.toRadians(angle)) + centerLocation.y).toFloat()

                // 连接每一个点
                if (count == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                // 当前是最后一层，绘制内外圈之间的连线
                if (index == NUMBER - 1) {
                    // 最内层x,y 坐标
                    val stopX =
                        (SMALL_RADIUS * cos(Math.toRadians(angle)) + centerLocation.x).toFloat()
                    val stopY =
                        (SMALL_RADIUS * sin(Math.toRadians(angle)) + centerLocation.y).toFloat()
                    paint.style = Paint.Style.STROKE
                    paint.color = Color.BLUE
                    canvas.drawLine(x, y, stopX, stopY, paint)
                    // 连接中心点
                    // canvas.drawLine(x, y, centerLocation.x, centerLocation.y, paint)
                }

                // 绘制文字
                if (index == NUMBER - 1) {
                    val text = "文字${count}"
                    // 计算文字宽高 计算完成之后会把值赋值给rect
                    paint.getTextBounds(text, 0, text.length, textRect)
                    val textWidth = textRect.width()
                    val textHeight = textRect.height()

                    val tempRadius = radius + textHeight
                    val textX =
                        (tempRadius * cos(Math.toRadians(angle)) + centerLocation.x).toFloat() - textWidth / 2f
                    val textY =
                        (tempRadius * sin(Math.toRadians(angle)) + centerLocation.y).toFloat()

                    paint.textSize = 30f
                    paint.style = Paint.Style.FILL
                    paint.color = Color.RED
                    // 绘制最外层文字
                    canvas.drawText(text, textX, textY, paint)
                }

//            paint.color = Color.BLUE
//            // 绘制每一个小圆
//            canvas.drawCircle(x, y, 20f, paint)
            }
            path.close() // 闭合
            paint.strokeWidth = 4f
            paint.style = Paint.Style.STROKE
            paint.color = Color.BLUE
            canvas.drawPath(path, paint) // 绘制
            path.reset()
        }
    }

    private fun getLocation(number: Int, count: Int): PointF = let {
        // 角度
        val angle = 360 / COUNT * number

        // 半径
        val radius = (count - 1) * INTERVAL + SMALL_RADIUS

        val x =
            (radius * cos(Math.toRadians(angle.toDouble())) + centerLocation.x).toFloat()
        val y =
            (radius * sin(Math.toRadians(angle.toDouble())) + centerLocation.y).toFloat()

        return PointF(x, y)
    }
}