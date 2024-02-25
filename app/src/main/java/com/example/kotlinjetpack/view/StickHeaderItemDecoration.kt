package com.example.kotlinjetpack.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.R
import kotlin.math.min

/**
 * desc: StickHeaderItemDecoration
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/9 10:23
 */
class StickHeaderItemDecoration() : RecyclerView.ItemDecoration() {

    //间隔高度
    private val mHeight = 100

    //矩形背景画笔
    private val mItemBgPaint: Paint by lazy {
        Paint()
    }

    //标签文本画笔
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRound: Rect = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        mItemBgPaint.apply {
            color = ContextCompat.getColor(parent.context, com.feiyu.rv.R.color.background_gray1)
        }
        textPaint.apply {
            color = ContextCompat.getColor(parent.context, R.color.black)
            textSize = 40f
        }
        val left = parent.paddingLeft.toFloat()
        val right = (parent.width - parent.paddingRight).toFloat()
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val bottom = childView.top.toFloat()
            val top = bottom - mHeight
            //绘制灰底矩形间隔
            c.drawRect(left, top, right, bottom, mItemBgPaint)
            //根据位置获取当前item的标签
            val tag = tagListener(parent.getChildAdapterPosition(childView))
            //绘制标签文本内容
            textPaint.getTextBounds(tag, 0, tag.length, mRound)
            c.drawText(
                tag,
                left + textPaint.textSize,
                bottom - mHeight / 2 + mRound.height() / 2,
                textPaint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //设置间隔高度
        outRect.top = mHeight
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val left = parent.paddingLeft.toFloat()
        val right = (parent.width - parent.paddingRight).toFloat()
        val manager = parent.layoutManager as LinearLayoutManager
        //第一个可见item位置
        val index = manager.findFirstVisibleItemPosition()
        if (index != -1) {
            //获取指定位置item的View信息
            val childView = parent.findViewHolderForLayoutPosition(index)?.itemView
            val top = parent.paddingTop.toFloat()
            val tag = tagListener(index)
            var bottom = parent.paddingTop + mHeight.toFloat()
            //悬浮置顶判断，其实也就是一直在绘制一个矩形加文本内容(上滑时取值bottom，下滑时取值childView.bottom.toFloat())
            bottom = min(childView?.bottom?.toFloat() ?: 0f, bottom)
            canvas.drawRect(0f, top, right, bottom, mItemBgPaint)
            textPaint.getTextBounds(tag, 0, tag.length, mRound)
            canvas.drawText(
                tag,
                left + textPaint.textSize,
                bottom - mHeight / 2 + mRound.height() / 2,
                textPaint
            )
        }
    }

    /**
     * 获取悬停标签
     */
    lateinit var tagListener: (Int) -> String

}