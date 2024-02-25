package com.example.kotlinjetpack.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import androidx.core.view.children

class ChildNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), NestedScrollingChild3 {

    private val TAG = "ChildNestedScrollView"
    private val childHelper by lazy {
        NestedScrollingChildHelper(this).apply { isNestedScrollingEnabled = true }
    }

    // 滚动消耗
    private val mScrollConsumed = IntArray(2)

    // 偏移量
    private val mScrollOffset = IntArray(2)

    private var lastTouchY = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        val touchY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchY = touchY
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
            }
            MotionEvent.ACTION_MOVE -> {
                var tempY = lastTouchY - touchY
                // 分发事件给parent 询问parent是否执行
                // true 表示父view消费了事件
                if (dispatchNestedPreScroll(
                        0,
                        tempY,
                        mScrollConsumed,
                        mScrollOffset,
                        ViewCompat.TYPE_TOUCH
                    )
                ) { // 父亲消费
                    tempY -= mScrollConsumed[1]
                    if (tempY == 0) return true
                } else {
                    // 自己消费
                    scrollBy(0, tempY)
                }
                lastTouchY = touchY
                // true 支持嵌套滚动
                if (dispatchNestedScroll(
                        0,
                        tempY,
                        0,
                        scrollY - measuredHeight,
                        mScrollOffset,
                        ViewCompat.TYPE_TOUCH
                    )
                ) {
                    Log.i("szj分发事件", "dispatchNestedScroll\t lastTouchY:${lastTouchY}")
                }

            }
            // 抬起/取消
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                stopNestedScroll(ViewCompat.TYPE_TOUCH)
            }
        }
        return true
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean = let {
        Log.i(TAG, "child startNestedScroll axes:$axes type:$type ")
        childHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll(type: Int) {
        Log.i(TAG, "child stopNestedScroll $type")
        childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return super.hasNestedScrollingParent()
    }

    // NestedScrollingChild3
    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int,
        consumed: IntArray
    ) {
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    // NestedScrollingChild2
    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean = let {
        childHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean = let {
        childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    /*
     * 作者:android 超级兵
     * 创建时间: 4/9/22 3:47 PM
     * TODO  最终xml会调用到这里..添加
     */
    override fun addView(child: View, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
    }

    @SuppressLint("LongLogTag")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var tempHeightMeasureSpec = heightMeasureSpec

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        // 遍历所有的view 用来测量高度
        children.forEach {
            tempHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(tempHeightMeasureSpec),
                MeasureSpec.UNSPECIFIED
            )

            // 测量子view
            measureChild(it, widthMeasureSpec, tempHeightMeasureSpec)
        }
        setMeasuredDimension(widthSize, children.first().measuredHeight)
    }

    override fun scrollTo(x: Int, y: Int) {
        var tempY = y
        if (tempY < 0) tempY = 0
        super.scrollTo(x, tempY)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY)
//        return super.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
//        return super.dispatchNestedFling(velocityX, velocityY, consumed)
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }
}
