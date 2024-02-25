package com.example.kotlinjetpack.behavior

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * desc: RvScrollBehavior
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/3/12 14:15
 */
class RvScrollBehavior constructor(
    val context: Context,
    val attributeSet: AttributeSet
) : CoordinatorLayout.Behavior<RecyclerView>(context, attributeSet) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {
        return dependency is TextView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: RecyclerView,
        dependency: View
    ): Boolean {

        Log.e("TAG", "onDependentViewChanged ${dependency.bottom} ${child.top}")
        ViewCompat.offsetTopAndBottom(child, (dependency.bottom - child.top))
//        if (!child.canScrollVertically(-1)) {
//            ViewCompat.offsetTopAndBottom(child, (dependency.bottom - child.top))
//        }
        return true
    }
}