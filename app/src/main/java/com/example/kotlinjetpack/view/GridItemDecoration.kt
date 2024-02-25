package com.example.kotlinjetpack.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration : RecyclerView.ItemDecoration() {

    private val TAG = "GridItemDecoration"
    private val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#e6e6e6")
//            style = Paint.Style.FILL
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
    }

    /**
     * 针对每个 item 都会执行一次
     * 每个 item 的 outRect 可以设置为不同值
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
//        outRect.left = 10
        outRect.bottom = 5
        outRect.left = 100
//        outRect.right = 20
    }

    /**
     * 针对 ItemDecoration,只执行一次
     * 需要遍历每个 item 进行绘制
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        Log.e(TAG, "onDraw: ")
        var layoutManager = parent.layoutManager


        for (i in 0 until parent.childCount) {
            val childView = parent.getChildAt(i)
            layoutManager?.let {
                val leftDecorationWidth = it.getLeftDecorationWidth(childView)
                val bottomDecorationHeight = it.getBottomDecorationHeight(childView)
                var x =
                    i % (it as GridLayoutManager).spanCount * (childView.width + leftDecorationWidth) + leftDecorationWidth / 2f

                paint.style = Paint.Style.STROKE
                c.drawCircle(
                    x,
                    (childView.top + childView.height / 2).toFloat(),
                    25f,
                    paint
                )

                paint.style = Paint.Style.FILL
                c.drawRect(
                    Rect(
                        childView.left,
                        childView.bottom,
                        childView.right,
                        childView.bottom + bottomDecorationHeight
                    ), paint
                )
            }
        }
    }

    /**
     * 针对 ItemDecoration,只执行一次
     * 需要遍历每个 item 进行绘制
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }
}