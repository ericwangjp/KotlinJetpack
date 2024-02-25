package com.example.kotlinjetpack.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * desc: SmoothScrollLinearLayoutManager
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/2 15:10
 */
class SmoothScrollLinearLayoutManager(
    private val context: Context, orientation: Int, reverseLayout: Boolean
) : LinearLayoutManager(context, orientation, reverseLayout) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int
    ) {
//        super.smoothScrollToPosition(recyclerView, state, position)
        val smoothScroller: LinearSmoothScroller = SmoothScroller(context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }


//    滚动到置顶
    inner class SmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
//            return super.calculateSpeedPerPixel(displayMetrics)
            return 100f / displayMetrics.densityDpi
        }
        override fun getHorizontalSnapPreference(): Int {
//            return super.getHorizontalSnapPreference()
            return SNAP_TO_START
        }

        override fun getVerticalSnapPreference(): Int {
//            return super.getVerticalSnapPreference()
            return SNAP_TO_START
        }
    }
}