package com.example.kotlinjetpack.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.OverScroller
import android.widget.ScrollView
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * desc: StickyNavScrollParent
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/15 14:39
 */
class StickyNavScrollParent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {
    private val TAG = "fqy-->"
    private var topHeight = 0
    private val mScroller: OverScroller by lazy {
        OverScroller(context)
    }

    // 第一个View
    private val firstView by lazy {
        children.first()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        firstView.post {
            topHeight = firstView.height
            Log.e(TAG, "onFinishInflate: topHeight: $topHeight")
        }
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val layoutParams = firstView.layoutParams
//        layoutParams.height = measuredHeight
//    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        Log.e(TAG, "onNestedScrollAccepted: ")
    }

    override fun onStartNestedScroll(
        child: View, target: View, nestedScrollAxes: Int, type: Int
    ): Boolean {
        return (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        Log.e(TAG, "onStopNestedScroll: ")
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        Log.e(TAG, "onNestedScroll1: ")
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        Log.e(TAG, "onNestedScroll2: ")
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.e(TAG, "onNestedPreScroll: Parent接收到滚动前事件，事件类型是手指滑动：${type == ViewCompat.TYPE_TOUCH}")
        Log.e(TAG, "onNestedPreScroll: 当前父view滚动距离scrollY：$scrollY")
        Log.e(TAG, "onNestedPreScroll: 当前子view滚动单次相对距离dy：$dy")
        Log.e(TAG, "onNestedPreScroll: 滚动到顶了:${!target.canScrollVertically(-1)}")
        Log.e(TAG, "onNestedPreScroll: 头部高度：$topHeight")
        // dy > 0表示子View向上滑动
        // (dy > 0 &&  scrollY < firstView.height) 子view 向上滑动  当前滑动的距离 < 第一个View的高 说明还有滑动空间
        // (dy < 0 && scrollY > 0) 子view 向下滑动 且父View还在屏幕外面, 另外内部View不能在垂直方向往下移动了
//        val hiddenTop = dy > 0 && scrollY < topHeight
//        val showTop = dy < 0 && scrollY > 0 && !target.canScrollVertically(-1)
//        if (hiddenTop || showTop) {
//            scrollBy(0, dy)
//            consumed[1] = dy
//        }

        if (dy < 0 && scrollY > 0 && !target.canScrollVertically(-1)) {
            scrollBy(0, -topHeight)
            consumed[1] = -topHeight // 关键代码!!parentView正在消费事件,并且通知 childView
            Log.e(TAG, "onNestedPreScroll: 展开")
//            LiveEventBus.get<String>("update").post("expand")
        } else if (dy > 0 && scrollY < topHeight) {
            scrollBy(0, topHeight)
            consumed[1] = topHeight // 关键代码!!parentView正在消费事件,并且通知 childView
            Log.e(TAG, "onNestedPreScroll: 收起")
//            LiveEventBus.get<String>("update").post("collapse")
        }


//        val recyclerView = target as RecyclerView
//        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//        val firstVisiblePos: Int = layoutManager.findFirstVisibleItemPosition()
//        if (firstVisiblePos <= 1) {
//            val topOffset = if (recyclerView.childCount == 0) 0 else recyclerView.getChildAt(0).top
//            Log.d(TAG, "距离顶部距离：$topOffset")
////            if (dy < 0 && topOffset >= 0) {
////                Log.e(TAG, "onNestedPreScroll: 展开")
////                LiveEventBus.get<String>("update").post("expand")
////            } else
//                if (dy > 0 && topOffset <= 0) {
//                    consumed[1] = dy // 关键代码!!parentView正在消费事件,并且通知 childView
//                Log.e(TAG, "onNestedPreScroll: 收起")
//                LiveEventBus.get<String>("update").post("collapse")
//            }
//        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
//        return super.onNestedPreFling(target, velocityX, velocityY)
        Log.e(TAG, "onNestedPreFling - scrollY: $scrollY")
        if (scrollY >= topHeight) return false
        fling(velocityY.toInt())
        return true
    }

    private fun fling(velocity: Int) {
        mScroller.fling(0, scrollY, 0, velocity, 0, 0, 0, topHeight)
        invalidate()
    }

//    override fun scrollTo(x: Int, y: Int) {
//        var ly = y
//        if (ly < 0) {
//            ly = 0
//        }
//        if (ly > topHeight) {
//            ly = topHeight
//        }
//        if (ly <= scrollY) {
//            super.scrollTo(x, ly)
//        }
//    }


}