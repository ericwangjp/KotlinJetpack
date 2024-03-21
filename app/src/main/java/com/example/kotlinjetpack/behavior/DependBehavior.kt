package com.example.kotlinjetpack.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.kotlinjetpack.view.exercise.DependentView

/**
 * desc: DependBehavior
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/3/11 17:45
 */
class DependBehavior constructor(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attributeSet) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is DependentView
    }


    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        //获取dependency的位置
        child.x = dependency.x
        child.y = dependency.bottom.toFloat()

        return true
    }
}