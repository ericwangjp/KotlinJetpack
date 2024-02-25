package com.example.kotlinjetpack.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class ScrollRecyclerViewBehavior(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<ViewPager2>(context, attrs) {

        companion object {
            private const val TAG = "ScrollRVBehavior"
        }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: ViewPager2,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child,layoutDirection)
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ViewPager2,
        dependency: View
    ): Boolean {
//        return super.layoutDependsOn(parent, child, dependency)
        return dependency is AppCompatTextView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ViewPager2,
        dependency: View
    ): Boolean {
//        return super.onDependentViewChanged(parent, child, dependency)
        ViewCompat.offsetTopAndBottom(child, -(child.top - dependency.bottom))
        return false
    }
}