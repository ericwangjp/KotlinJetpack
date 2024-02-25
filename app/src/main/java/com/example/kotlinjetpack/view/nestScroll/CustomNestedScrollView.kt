package com.example.kotlinjetpack.view.nestScroll

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.core.view.children

/**
 * desc: CustomNestedScrollView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/19 20:32
 */
class CustomNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {
    private var topScrollOffset = 0
    // 第一个View
    private val firstView by lazy {
        children.first()
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {

    }

    override fun onStopNestedScroll(target: View, type: Int) {

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

    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {

    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy < 0 && scrollY > 0 && !target.canScrollVertically(-1)) {
            scrollBy(0, -topScrollOffset)
            consumed[1] = -topScrollOffset
            Log.e("fqy", "onNestedPreScroll: 展开")
//            LiveEventBus.get<String>("update").post("expand")
        } else if (dy > 0 && scrollY < topScrollOffset) {
            scrollBy(0, topScrollOffset)
            consumed[1] = topScrollOffset
            Log.e("fqy", "onNestedPreScroll: 收起")
//            LiveEventBus.get<String>("update").post("collapse")
        }
    }

}