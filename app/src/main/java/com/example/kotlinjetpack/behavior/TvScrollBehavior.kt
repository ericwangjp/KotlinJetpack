package com.example.kotlinjetpack.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import java.lang.Math.abs

/**
 * desc: RvScrollBehavior
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/3/12 13:56
 */
class TvScrollBehavior constructor(
    private val mContext: Context,
    val attributeSet: AttributeSet
) : CoordinatorLayout.Behavior<TextView>(mContext, attributeSet) {
    //相对于y轴滑动的距离
    private var mScrollY = 0

    //总共滑动的距离
    private var totalScroll = 0


    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: TextView,
        layoutDirection: Int
    ): Boolean {
        Log.e("TAG", "onLayoutChild----")
        //实时测量
        parent.onLayoutChild(child, layoutDirection)
        return true
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: TextView,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        //目的为了dispatch成功
        return true
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
        //边界处理
        var cosumedy = dy
        Log.e("TAG", "onNestedPreScroll $totalScroll dy $dy")
        var scroll = totalScroll + dy
        if (kotlin.math.abs(scroll) > getMaxScroll(child)) {
            cosumedy = getMaxScroll(child) - kotlin.math.abs(totalScroll)
        } else if (scroll < 0) {
            cosumedy = 0
        }
        //在这里进行事件消费，我们只需要关心竖向滑动
        ViewCompat.offsetTopAndBottom(child, -cosumedy)
        //重新赋值
        totalScroll += cosumedy
        consumed[1] = cosumedy
    }

    private fun getMaxScroll(child: TextView): Int {
        return child.height
    }
}