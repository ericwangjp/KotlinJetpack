package com.example.kotlinjetpack.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException

/**
 * 绘制的流程中，先调用 ItemDecoration 的 onDraw() 方法，
 * 然后再调用 item 的 onDraw() 方法，
 * 最后再调用 ItemDecoration 的 onDrawOver() 方法
 */
class ProcessItemDecoration : RecyclerView.ItemDecoration() {

    private val progressPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            style = Paint.Style.FILL
            strokeWidth = 4f
        }
    }
    private val linePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
    }
    private var curPos = 7
    private val radius = 20f

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.apply {
            top = 40
            left = 100
            right = 40
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        val layoutManager = parent.layoutManager
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val leftDecorationWidth = layoutManager?.getLeftDecorationWidth(childView) ?: 0
            val topDecorationHeight = layoutManager?.getTopDecorationHeight(childView) ?: 0
            val bottomDecorationHeight = layoutManager?.getBottomDecorationHeight(childView) ?: 0
            val childLayoutPosition = parent.getChildLayoutPosition(childView)
//            圆上半部分的竖线
            val topStartY = childView.top - topDecorationHeight
            val topStopY = childView.top + childView.height / 2f - radius
//            圆下半部分的竖线
            val bottomStartY = childView.top + childView.height / 2f + radius
            val bottomStopY = childView.bottom

            // 位置超过 curPosition 时，竖线颜色设置为浅色
            if (childLayoutPosition > curPos) {
                linePaint.color = Color.GRAY
                progressPaint.color = Color.GRAY
                progressPaint.style = Paint.Style.STROKE
            } else {
                linePaint.color = Color.GREEN
                progressPaint.color = Color.GREEN
                progressPaint.style = Paint.Style.FILL
            }

            if (childLayoutPosition == curPos) {
                progressPaint.style = Paint.Style.STROKE
                c.drawCircle(
                    leftDecorationWidth / 2f,
                    childView.top + childView.height / 2f,
                    4f,
                    progressPaint
                )
            }

            c.drawCircle(
                leftDecorationWidth / 2f,
                childView.top + childView.height / 2f,
                radius,
                progressPaint
            )

            // 绘制竖线 , 第 0 位置上只需绘制 下半部分
            if (childLayoutPosition == 0) {
                // 当前 item position = curPosition 时，绘制下半部分竖线时，颜色设置为浅色
                if (childLayoutPosition == curPos) {
                    linePaint.color = Color.GRAY
                }
                c.drawLine(
                    leftDecorationWidth / 2f,
                    bottomStartY,
                    leftDecorationWidth / 2f,
                    bottomStopY.toFloat(),
                    linePaint
                )
                // 最后位置上，只需绘制上半部分
            } else if (childLayoutPosition == (parent.adapter?.itemCount ?: 0) - 1) {
                c.drawLine(
                    leftDecorationWidth / 2f,
                    topStartY.toFloat(),
                    leftDecorationWidth / 2f,
                    topStopY,
                    linePaint
                )
            } else {
                c.drawLine(
                    leftDecorationWidth / 2f,
                    topStartY.toFloat(),
                    leftDecorationWidth / 2f,
                    topStopY,
                    linePaint
                )

                // 当前 item position = curPosition 时，绘制下半部分竖线时，颜色设置为浅色
                if (childLayoutPosition == curPos) {
                    linePaint.color = Color.GRAY
                }
                c.drawLine(
                    leftDecorationWidth / 2f,
                    bottomStartY,
                    leftDecorationWidth / 2f,
                    bottomStopY.toFloat(),
                    linePaint
                )
            }

        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val layoutManager = parent.layoutManager
        Log.e("onDrawOver: ", "childCount：${parent.childCount}")
        Log.e("onDrawOver: ", "itemCount：${layoutManager?.itemCount}")
        Log.e("onDrawOver: ", "adapter itemCount：${parent.adapter?.itemCount}")
//        for (i in 0 until parent.childCount) {
//            val childView = parent.getChildAt(i)
//            val leftDecorationWidth = layoutManager?.getLeftDecorationWidth(childView) ?: 0f
//            c.drawCircle(
//                leftDecorationWidth.toFloat() + 20,
//                childView.top + childView.height / 2f,
//                20f,
//                progressPaint
//            )
//        }
    }

    fun setDoingPos(recyclerView: RecyclerView, position: Int) {
        if (recyclerView.adapter == null) {
            throw IllegalArgumentException("RecyclerView Adapter can't be null")
        }
        if (position < 0) {
            throw IllegalArgumentException("position can't be less than 0")
        }

        if (position > (recyclerView.adapter?.itemCount ?: 0) - 1) {
            throw IllegalArgumentException("position can't be greater than item count")
        }
        this.curPos = position
    }
}