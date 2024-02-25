package com.example.kotlinjetpack.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import com.example.kotlinjetpack.interfaces.OnPullZoomListener
import kotlin.math.abs

/**
 * desc: CustomScaleScrollView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/21 11:37
 */
class CustomScaleScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
    private var mPullToZoomEnable = true
    private var mHideHeader = false
    private var mIsBeingDragged = false
    private var isZooming = false
    private var mLastMotionY = 0f
    private var mLastMotionX = 0f
    private var mInitialMotionY = 0f
    private var mInitialMotionX = 0f
    private var mTouchSlop = 30
    private final val FRICTION = 10
    private var mOnPullZoomListener: OnPullZoomListener? = null

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
//        return super.onInterceptTouchEvent(ev)
        if (!isPullToZoomEnabled() || isHideHeader()) {
            return false
        }

        val action: Int = event.action

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false
            return false
        }

        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true
        }
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (isReadyForPullStart()) {
                    val y: Float = event.y
                    val x: Float = event.x
                    val absDiff: Float

                    // We need to use the correct values, based on scroll
                    // direction
                    val diff: Float = y - mLastMotionY
                    val oppositeDiff: Float = x - mLastMotionX
                    absDiff = abs(diff)
                    if (absDiff > mTouchSlop && absDiff > abs(oppositeDiff)) {
                        if (diff >= 1f && isReadyForPullStart()) {
                            mLastMotionY = y
                            mLastMotionX = x
                            mIsBeingDragged = true
                        }
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullStart()) {
                    mInitialMotionY = event.y
                    mLastMotionY = mInitialMotionY
                    mInitialMotionX = event.x
                    mLastMotionX = mInitialMotionX
                    mIsBeingDragged = false
                }
            }
        }

        return mIsBeingDragged
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return super.onTouchEvent(ev)
        if (!isPullToZoomEnabled() || isHideHeader()) {
            return false
        }

        if (event.action == MotionEvent.ACTION_DOWN && event.edgeFlags != 0) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (mIsBeingDragged) {
                    mLastMotionY = event.y
                    mLastMotionX = event.x
                    pullEvent()
                    isZooming = true
                    return true
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (isReadyForPullStart()) {
                    mInitialMotionY = event.getY()
                    mLastMotionY = mInitialMotionY
                    mInitialMotionX = event.getX()
                    mLastMotionX = mInitialMotionX
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false
                    // If we're already refreshing, just scroll back to the top
                    if (isZooming) {
                        smoothScrollToTop()
                        mOnPullZoomListener?.onPullZoomEnd()
                        isZooming = false
                        return true
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun smoothScrollToTop() {

    }

    private fun isReadyForPullStart(): Boolean {
        return false
    }

    private fun isHideHeader(): Boolean = mHideHeader

    private fun isPullToZoomEnabled(): Boolean = mPullToZoomEnable

    private fun pullEvent() {
        val newScrollValue: Int
        val initialMotionValue: Float = mInitialMotionY
        val lastMotionValue: Float = mLastMotionY
        newScrollValue =
            Math.round((initialMotionValue - lastMotionValue).coerceAtMost(0f) / FRICTION).toInt()
        pullHeaderToZoom(newScrollValue)
        mOnPullZoomListener?.onPullZooming(newScrollValue)
    }

    private fun pullHeaderToZoom(newScrollValue: Int) {
//        Log.d("pullHeaderToZoom --> ", "newScrollValue = $newScrollValue")
//        Log.d("pullHeaderToZoom --> ", "mHeaderHeight = $mHeaderHeight")
//        if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
//            mScalingRunnable.abortAnimation()
//        }
//        val localLayoutParams: ViewGroup.LayoutParams = mHeaderContainer.getLayoutParams()
//        localLayoutParams.height = abs(newScrollValue) + mHeaderHeight
//        mHeaderContainer.setLayoutParams(localLayoutParams)
//        if (isCustomHeaderHeight) {
//            val zoomLayoutParams: ViewGroup.LayoutParams = mZoomView.getLayoutParams()
//            zoomLayoutParams.height = abs(newScrollValue) + mHeaderHeight
//            mZoomView.setLayoutParams(zoomLayoutParams)
//        }
    }

    fun setOnPullZoomListener(onPullZoomListener: OnPullZoomListener) {
        mOnPullZoomListener = onPullZoomListener
    }
}