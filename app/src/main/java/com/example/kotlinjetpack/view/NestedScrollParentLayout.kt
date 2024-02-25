package com.example.kotlinjetpack.view

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.marginLeft
import com.example.kotlinjetpack.utils.ScrollUtil
import com.example.kotlinjetpack.utils.WidgetUtil
import kotlin.math.absoluteValue
import kotlin.math.pow

open class NestedScrollParentLayout : ViewGroup, NestedScrollingParent3 {

    private var mVelocityTracker = VelocityTracker.obtain()
    private var mScroller = Scroller(context)

    private var mParentHelper: NestedScrollingParentHelper? = null

    //    判断是滑动的最小距离,阈值，小于阈值默认为手指抖动，ViewConfiguration 中配置
    private var mTouchSlop: Int = 0

    //    用来初始化fling的最小速度，单位是每秒多少像素
    private var mMinimumVelocity: Float = 0f

    //    用来初始化fling的最大速度，单位是每秒多少像素
    private var mMaximumVelocity: Float = 0f
    private var mCurrentVelocity: Float = 0f

    //    阻尼滑动参数
    private val mMaxDragRate = 2.5f
    private val mMaxDragHeight = 250
    private val mScreenHeightPixels = context.resources.displayMetrics.heightPixels

    private var mHandler: Handler? = null
    private var mNestedInProgress = false

    //    是否允许过度滑动
    private var mIsAllowOverScroll = true

    //    在子view滑动前，此view需要滑动的距离
    private var mPreConsumedNeeded = 0

    //    当前竖直方向上 translationY 的距离
    private var mSpinner = 0f

    private var mReboundAnimator: ValueAnimator? = null

    // TODO:  待定义类型
    private var mReboundInterpolator = null

    //    实现fling时，先过度滑动再回弹的效果
    private var mAnimationRunnable: Runnable? = null

    //    控制fling时等待 contentView 回到 translation = 0 的位置
    private var mVerticalPermit = false

    private val TAG = "NestedScrollParent-M"

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setWillNotDraw(false)
        mHandler = Handler(Looper.getMainLooper())
        mParentHelper = NestedScrollingParentHelper(this)
        ViewConfiguration.get(context).let {
            mTouchSlop = it.scaledTouchSlop
            mMinimumVelocity = it.scaledMinimumFlingVelocity.toFloat()
            mMaximumVelocity = it.scaledMaximumFlingVelocity.toFloat()
        }
    }

    /**
     * 在布局加载结束时，找到可以滚动的 View 作为内容布局，并赋值给 mRefreshContent 属性
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var minimumWidth = 0
        var minimumHeight = 0
        val thisView = this
        for (i in 0 until super.getChildCount()) {
            val childView = super.getChildAt(i)
            if (childView == null || childView.visibility == GONE) continue
            val lp = childView.layoutParams
            val mlp = lp as? MarginLayoutParams
            val leftMargin = mlp?.leftMargin ?: 0
            val rightMargin = mlp?.rightMargin ?: 0
            val topMargin = mlp?.topMargin ?: 0
            val bottomMargin = mlp?.bottomMargin ?: 0
            val widthSpec = getChildMeasureSpec(
                widthMeasureSpec,
                thisView.paddingLeft + thisView.paddingRight + leftMargin + rightMargin,
                lp.width
            )
            val heightSpec = getChildMeasureSpec(
                heightMeasureSpec,
                thisView.paddingTop + thisView.paddingBottom + topMargin + bottomMargin,
                lp.height
            )
            childView.measure(widthSpec, heightSpec)
            minimumWidth += childView.measuredWidth
            minimumHeight += childView.measuredHeight
        }
        super.setMeasuredDimension(
            resolveSize(
                minimumWidth.coerceAtLeast(super.getSuggestedMinimumWidth()),
                widthMeasureSpec
            ),
            resolveSize(
                minimumHeight.coerceAtLeast(super.getSuggestedMinimumHeight()),
                heightMeasureSpec
            )
        )
    }


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val thisView = this
        var bottom = 0
        var top = 0
        for (i in 0 until super.getChildCount()) {
            val childView = super.getChildAt(i)
            if (childView == null || childView.visibility == GONE) continue
            val lp = childView.layoutParams
            val mlp = lp as? MarginLayoutParams
            val leftMargin = mlp?.leftMargin ?: 0
            val topMargin = mlp?.topMargin ?: 0

            val left = leftMargin + thisView.paddingLeft
            val right = left + childView.measuredWidth
            top += topMargin + thisView.paddingTop + bottom
            bottom += top + childView.measuredHeight

            childView.layout(left, top, right, bottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
//        return super.generateLayoutParams(attrs)
        Log.e(TAG, "generateLayoutParams: ")
        return MarginLayoutParams(context, attrs)
    }

    /**
     * 嵌套滑动开始时调用
     * 方法返回 true 时，表示此Parent能够接收此次嵌套滑动事件
     * 返回 false，不接收此次嵌套滑动事件，后续方法都不会调用
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        Log.e(TAG, "onStartNestedScroll: 类型：$type")
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    /**
     * 当 onStartNestedScroll() 方法返回 true 后，此方法会立刻调用
     * 可在此方法做每次嵌套滑动的初始化工作
     */
    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        Log.e(TAG, "onNestedScrollAccepted: type-> $type")
        mParentHelper?.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        Log.e(TAG, "onStopNestedScroll: type-> $type")
        mParentHelper?.onStopNestedScroll(target, type)
    }

    /**
     * Parent 正在执行嵌套滑动时，会调用此方法，在这里实现嵌套滑动的逻辑
     * 与下面方法的区别，此方法多了个 consumed 参数，用于存放嵌套滑动执行完后，
     * 被此 parent 消耗的滑动距离
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
        Log.e(TAG, "onNestedScroll: type-> $type")
        if (type == ViewCompat.TYPE_TOUCH) {
            // TODO:
        } else {
            consumed[1] += dyUnconsumed
        }
    }

    //    Parent 正在执行嵌套滑动时，会调用此方法，在这里实现嵌套滑动的逻辑
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        Log.e(TAG, "onNestedScroll: type-> $type")
        if (type == ViewCompat.TYPE_TOUCH) {
            // TODO:
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.e(TAG, "onNestedPreScroll: type-> $type")
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
//        return super.onNestedPreFling(target, velocityX, velocityY)
        return startFlingIfNeed(-velocityY)
    }

    private fun startFlingIfNeed(flingVelocity: Float): Boolean {
        val velocity = if (flingVelocity == 0f) mCurrentVelocity else flingVelocity
        if (velocity.absoluteValue > mMinimumVelocity) {
            if (velocity < 0 && mIsAllowOverScroll && mSpinner == 0f
                || velocity > 0 && mIsAllowOverScroll && mSpinner == 0f
            ) {
                mScroller.fling(0, 0, 0, (-velocity).toInt(), 0, 0, -Int.MAX_VALUE, Int.MAX_VALUE)
                mScroller.computeScrollOffset()
                val thisView: View = this
                thisView.invalidate()
            }
        }

        return false
    }

    override fun computeScroll() {
//        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
//            mScroller.startScroll()
//            invalidate()
        }
    }


}