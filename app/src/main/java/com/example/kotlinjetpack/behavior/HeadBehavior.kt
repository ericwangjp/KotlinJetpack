package com.example.kotlinjetpack.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class HeadBehavior(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<TextView>(context, attrs) {
    //    最后一次滑动距离
    private var lastScrollY = 0

    companion object {
        private const val TAG = "HeadBehavior"
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
//        return super.onStartNestedScroll(
//            coordinatorLayout,
//            child,
//            directTargetChild,
//            target,
//            axes,
//            type
//        )
        return target is RecyclerView
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: TextView,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        // 最大滑动
        val maxScroll = -child.height

        // 最小滑动距离
        val minScroll = 0

        // 当前滑动距离 = 最后一次滑动距离 - 当前滑动距离
        var currentOffset = lastScrollY - dy

        // 当前消费的距离 Y
        currentOffset = when {
            currentOffset < maxScroll -> {
                maxScroll
            }
            currentOffset > minScroll -> {
                minScroll
            }
            else -> {
                currentOffset
            }
        }

        ViewCompat.offsetTopAndBottom(child, currentOffset - child.top)

        // 当前消费距离 - 最后一次消费距离 - 当前消费距离
        val consumedY: Int = lastScrollY - currentOffset

        // 记录当前滑动距离
        lastScrollY = currentOffset

        // 将当前记录的距离 传递给rv
        consumed[1] = consumedY
    }
}