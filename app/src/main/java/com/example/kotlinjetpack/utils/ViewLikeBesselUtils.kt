package com.example.kotlinjetpack.utils

import android.animation.*
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.PointF
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.FrameLayout
import java.util.*
import kotlin.collections.ArrayList


/**
 * desc: ViewLikeBesselUtils
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/30 5:52 下午
 */
class ViewLikeBesselUtils(
    val mClickView: View,
    val mAnimViews: ArrayList<View>,
    val mListener: ViewLikeClickListener
) {
    interface ViewLikeClickListener {
        /**
         * @param view                被点赞的按钮
         * @param toggle              开关
         * @param viewLikeBesselUtils 工具类本身
         */
        fun onClick(view: View?, toggle: Boolean, viewLikeBesselUtils: ViewLikeBesselUtils?)
    }

    private var toggle = false // 点击开关标识
    private var mX // 距离屏幕左侧距离
            = 0
    private var mY // 距离屏幕顶端距离, 越往下数值越大
            = 0
    private val mRandom: Random = Random() // 随机数

    /**
     * 设置View的监听
     */
    private fun initListener() {
        mClickView.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
                getLocation() // 获取被点击View的坐标
                toggle = !toggle
                mListener?.onClick(mClickView, toggle, this@ViewLikeBesselUtils)
                // mView.performClick();
            }
            // 正常的OnClickListener将无法调用
            true
        }
    }

    /**
     * 获取View在屏幕中的坐标
     */
    private fun getLocation() {
        val mLocation = IntArray(2)
        mClickView.getLocationInWindow(mLocation)
        mX = mLocation[0]
        mY = mLocation[1]
    }

    /**
     * 开始动画
     *
     * @param mAnimView
     */
    private fun startAnim(mAnimView: View, mTime: Int) {
        val animatorSet = AnimatorSet()
        val interpolators: ArrayList<BaseInterpolator> = ArrayList()
        interpolators.add(AccelerateInterpolator())
        interpolators.add(DecelerateInterpolator())
        interpolators.add(AccelerateDecelerateInterpolator())
        interpolators.add(LinearInterpolator())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            animatorSet.interpolator = interpolators[mRandom.nextInt(4)]
        }
        // 合并动画
        animatorSet.playTogether(getAnimationSet(mAnimView), getBezierAnimatorSet(mAnimView))
        animatorSet.setTarget(mAnimView)
        animatorSet.duration = mTime.toLong()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                removeChildView(mAnimView)
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        animatorSet.start()
    }

    /**
     * 将上浮控件添加到屏幕中
     *
     * @param animview
     */
    private fun addAnimView(animview: View) {
        val activityFromView = getActivityFromView(mClickView)
        if (activityFromView != null) {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            val mRootView = activityFromView.window.decorView.rootView as FrameLayout
            mRootView.addView(animview, params)
        }
    }

    /**
     * 开始动画
     */
    fun startLikeAnim() {
        for (mAnimView in mAnimViews) {
            removeChildView(mAnimView)
            addAnimView(mAnimView)
            startAnim(mAnimView, mRandom.nextInt(1500))
        }
    }

    /**
     * 获取属性动画
     */
    private fun getAnimationSet(mView: View): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(mView, "scaleX", 0.4f, 1f)
        val scaleY = ObjectAnimator.ofFloat(mView, "scaleY", 0.4f, 1f)
        val alpha = ObjectAnimator.ofFloat(mView, "alpha", 1f, 0.2f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha)
        return animatorSet
    }

    /**
     * 获取贝塞尔动画
     */
    private fun getBezierAnimatorSet(mView: View): ValueAnimator {
        // 测量view
        mView.measure(0, 0)
        // 屏幕宽
        val width = getActivityFromView(mClickView)!!.windowManager.defaultDisplay.width
        val mPointF0X: Int = mX + mRandom.nextInt(mView.getMeasuredWidth())
        val mPointF0Y: Int = mY - mView.getMeasuredHeight() / 2
        // 起点
        val pointF0 = PointF(mPointF0X.toFloat(), mPointF0Y.toFloat())
        // 终点
        val pointF3 = PointF(mRandom.nextInt(width - 100).toFloat(), 0f)
        // 第二点
        val pointF1 = PointF(mRandom.nextInt(width - 100).toFloat(), (mY * 0.7).toFloat())
        // 第三点
        val pointF2 = PointF(mRandom.nextInt(width - 100).toFloat(), (mY * 0.3).toFloat())
        val be = BezierEvaluator(pointF1, pointF2)
        val bezierAnimator = ValueAnimator.ofObject(be, pointF0, pointF3)
        bezierAnimator.addUpdateListener { valueAnimator ->
            val pointF = valueAnimator.animatedValue as PointF
            mView.setX(pointF.x)
            mView.setY(pointF.y)
        }
        return bezierAnimator
    }

    /**
     * 获取Activity
     *
     * @param view
     * @return
     */
    fun getActivityFromView(view: View?): Activity? {
        if (null != view) {
            var context: Context? = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = (context as ContextWrapper).baseContext
            }
        }
        return null
    }

    /**
     * 将子View从父容器中去除
     */
    private fun removeChildView(mChildView: View) {
        val parentViewGroup = mChildView.parent as? ViewGroup
        parentViewGroup?.removeView(mChildView)
    }

    init {
        initListener()
    }
}

class BezierEvaluator(
    /**
     * 这2个点是控制点
     */
    private val point1: PointF, private val point2: PointF
) :
    TypeEvaluator<PointF> {
    /**
     * @param t
     * @param point0 初始点
     * @param point3 终点
     * @return
     */
    override fun evaluate(t: Float, point0: PointF, point3: PointF): PointF {
        val point = PointF()
        point.x =
            point0.x * (1 - t) * (1 - t) * (1 - t) + 3 * point1.x * t * (1 - t) * (1 - t) + 3 * point2.x * t * t * (1 - t) * (1 - t) + point3.x * t * t * t
        point.y =
            point0.y * (1 - t) * (1 - t) * (1 - t) + 3 * point1.y * t * (1 - t) * (1 - t) + 3 * point2.y * t * t * (1 - t) * (1 - t) + point3.y * t * t * t
        return point
    }
}