package com.example.kotlinjetpack.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout


/**
 * desc: 普通点赞效果, 点击控件后出现一个View上浮
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/30 5:22 下午
 * @param mClickView 被点击的View
 * @param mAnimView  点赞后, 向上浮动的View
 * @param mListener  被点击的View,点击后的回调事件.
 */
class ViewLikeUtils(
    val mClickView: View,
    val mAnimView: View,
    val mListener: ViewLikeClickListener
) {

    init {
        initListener()
    }

    interface ViewLikeClickListener {
        /**
         * @param view          被点赞的按钮
         * @param toggle        开关
         * @param viewLikeUtils 工具类本身
         */
        fun onClick(view: View?, toggle: Boolean, viewLikeUtils: ViewLikeUtils?)
    }

    private var toggle = false // 点击开关标识
    private var mX // 距离屏幕左侧距离
            = 0
    private var mY // 距离屏幕顶端距离, 越往下数值越大
            = 0

    /**
     * 设置View的监听
     */
    private fun initListener() {
        mClickView.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
                location // 获取被点击View的坐标
                toggle = !toggle
                mListener?.onClick(mClickView, toggle, this@ViewLikeUtils)
                // mView.performClick();
            }
            // 正常的OnClickListener将无法调用
            true
        }
    }

    /**
     * 获取View在屏幕中的坐标
     */
    private val location: Unit
        private get() {
            val mLocation = IntArray(2)
            mClickView.getLocationOnScreen(mLocation)
            mX = mLocation[0]
            mY = mLocation[1]
        }

    /**
     * 开始动画
     */
    private fun startAnim(valueAnimator: ValueAnimator) {
        valueAnimator.addUpdateListener { valueAnimator ->
            mAnimView.alpha = 1 - valueAnimator.animatedFraction
            val params = mAnimView.layoutParams as FrameLayout.LayoutParams
            params.topMargin =
                ((mY - mAnimView.measuredHeight - 100 * valueAnimator.animatedFraction).toInt())
            mAnimView.layoutParams = params
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                removeChildView(mAnimView)
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        valueAnimator.start()
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
            // 测量浮动View的大小
            animview.measure(0, 0)
            params.topMargin = (mY - animview.measuredHeight) as Int
            params.leftMargin =
                mX + mClickView.measuredWidth / 2 - animview.measuredHeight / 2
            animview.layoutParams = params
        }
    }

    /**
     * 开始动画
     */
    fun startLikeAnim(valueAnimator: ValueAnimator) {
        removeChildView(mAnimView)
        addAnimView(mAnimView)
        startAnim(valueAnimator)
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

}