package com.example.kotlinjetpack.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * desc: RecyclerViewSmoothScrollHelper 目前仅支持 LinearLayoutManager
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/7 10:56
 */
object RecyclerViewSmoothScrollHelper {

    fun smoothScrollToPosition(recyclerView: RecyclerView, snapMode: Int, position: Int) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val mScroller: LinearSmoothScroller = when (snapMode) {
                LinearSmoothScroller.SNAP_TO_START -> {
                    SmoothToTopScroller(recyclerView.context)
                }
                LinearSmoothScroller.SNAP_TO_END -> {
                    SmoothToBottomScroller(recyclerView.context)
                }
                else -> {
                    LinearSmoothScroller(recyclerView.context)
                }
            }
            mScroller.targetPosition = position
            layoutManager.startSmoothScroll(mScroller)
        }
    }

    class SmoothToTopScroller internal constructor(context: Context) :
        LinearSmoothScroller(context) {
        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return super.calculateSpeedPerPixel(displayMetrics)
//            默认 25，这里加快速度
//            return 15f / displayMetrics.densityDpi
        }
    }

    class SmoothToBottomScroller internal constructor(context: Context) :
        LinearSmoothScroller(context) {
        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_END
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_END
        }
    }
}