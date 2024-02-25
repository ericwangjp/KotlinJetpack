package com.example.kotlinjetpack.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.children

class ParentNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {
    // RelativeLayout
    private val TAG = "ParentNestedScrollView"
    private val parentHelper by lazy {
        NestedScrollingParentHelper(this)
    }
    private val mScroller by lazy {
//        Scroller(context)
        OverScroller(context)
    }

    // 第一个View
    private val firstView by lazy {
        children.first()
    }

    //在setContentView之后、onMeasure之前调用的方法
    override fun onFinishInflate() {
        super.onFinishInflate()
    }

//    private var mChildHeight = 0
//
//    @SuppressLint("LongLogTag")
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        var tempHeightMeasureSpec = heightMeasureSpec
//        mChildHeight = 0
//        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
//        val heightSize = MeasureSpec.getSize(tempHeightMeasureSpec)
//
//        children.forEach {
//            tempHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.UNSPECIFIED)
//
//            // 测量子view
//            measureChild(it, widthMeasureSpec, tempHeightMeasureSpec)
//            mChildHeight += it.measuredHeight
//        }
//        setMeasuredDimension(widthSize, heightSize)
//    }

    /*
     * 子view调用 startNestedScroll()时候执行
     * 返回true，代表当前ViewGroup能接受内部View的滑动参数（这个内部View不一定是直接子View），一般情况下建议直接返回true，当然你可以根据nestedScrollAxes：判断垂直或水平方向才返回true
     */
    override fun onStartNestedScroll(
        child: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean =
        (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
//        true


    /*
     * 如果onStartNestedScroll()返回true的话,就会紧接着调用该方法
     *  常用来做一些初始化工作
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    /*
     * 当子view调用 stopNestedScroll() 时候调用
     */
    override fun onStopNestedScroll(target: View, type: Int) {
        parentHelper.onStopNestedScroll(target, type)
    }

    /**
     * 处理child未消耗完的滚动距离
     */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        Log.e(TAG, "onNestedScroll: ${consumed[0]}-${consumed[1]}")
    }

    /**
     * 处理child未消耗完的滚动距离
     */
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        Log.e(TAG, "onNestedScroll: $dyUnconsumed")
    }


    /**
     * 会传入内部View移动的dx与dy，当前ViewGroup可以消耗掉一定的dx与dy，然后通过最后一个参数consumed传回给子View
     * 当子view调用 dispatchNestedPreScroll() 时候调用
     * tips:在childNestedScrollView.onTouchEvent#ACTION_MOVE:中
     * ViewCompat.canScrollVertically(view, int)
     * 负数: 顶部是否可以滚动(官方描述: 能否往上滚动, 不太准确吧~)
     * 正数: 底部是否可以滚动
     */
    override fun onNestedPreScroll(
        target: View, dx: Int, dy: Int,
        consumed: IntArray, type: Int
    ) {
        Log.e(TAG, "onNestedPreScroll: Parent接收到滚动前事件，事件类型是手指滑动：${type == ViewCompat.TYPE_TOUCH}")
        Log.e(TAG, "onNestedPreScroll: 当前父view滚动距离scrollY：$scrollY")
        Log.e(TAG, "onNestedPreScroll: 当前子view滚动单次相对距离dy：$dy")
        // dy > 0表示子View向上滑动
        // (dy > 0 &&  scrollY < firstView.height) 子view 向上滑动  当前滑动的距离 < 第一个View的高 说明还有滑动空间
        // (dy < 0 && scrollY > 0) 子view 向下滑动 且父View还在屏幕外面, 另外内部View不能在垂直方向往下移动了
        if ((dy > 0 && scrollY < firstView.height) || (dy < 0 && scrollY > 0 && !target.canScrollVertically(
                -1
            ))
        ) {
            // 父容器消费了多少通知子view
            consumed[1] = dy // 关键代码!!parentView正在消费事件,并且通知 childView
            scrollBy(0, dy)
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.e(TAG, "onNestedPreFling: velocityY->$velocityY")
        Log.e(TAG, "onNestedPreFling: scrollY->$scrollY")
        Log.e(TAG, "onNestedPreFling: firstView.height->${firstView.height}")
        if (velocityY > 0 && scrollY < firstView.height) {
            // 向上滑动, 且当前父View还可见
            fling(velocityY.toInt(), firstView.height)
            return true
        } else if (velocityY < 0 && scrollY > 0) {
            // 向下滑动, 且当前View部分在屏幕外
            fling(velocityY.toInt(), 0)
            return true
        }
//        return super.onNestedPreFling(target, velocityX, velocityY)
        return false
    }


    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
//        return super.onNestedFling(target, velocityX, velocityY, consumed)
        Log.e(TAG, "onNestedFling: ")
        return false
    }

    override fun scrollTo(x: Int, y: Int) {
        var tempY = y
//        if (tempY < 0) tempY = 0
//        super.scrollTo(x, tempY)
        if (tempY < 0) {
            // 不允许向下滑动
            tempY = 0
        }
        if (tempY > firstView.height) {
            // 防止向上滑动距离大于最大滑动距离
            tempY = firstView.height
        }
        if (tempY != scrollY) {
            super.scrollTo(x, tempY)
        }
    }


    private fun fling(velocityY: Int, maxY: Int) {
        mScroller.fling(0, scrollY, 0, velocityY, 0, 0, 0, maxY)
        invalidate()
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            invalidate()
        }
//        super.computeScroll()
    }

}

