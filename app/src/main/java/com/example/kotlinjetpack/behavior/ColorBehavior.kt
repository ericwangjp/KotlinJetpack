package com.example.kotlinjetpack.behavior

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import com.example.kotlinjetpack.view.MoveView
import kotlin.random.Random

class ColorBehavior(val context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<AppCompatImageView>(context, attrs) {

    private val TAG = "ColorBehavior"

    /**
     * 判断跟随变化的 View
     */
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ): Boolean {
//        return super.layoutDependsOn(parent, child, dependency)
        return dependency is MoveView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ): Boolean {
        child.setBackgroundColor(Color.rgb(Random.nextInt(0,255),Random.nextInt(0,255),Random.nextInt(0,255)))
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onDependentViewRemoved(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ) {
        Log.e(TAG, "onDependentViewRemoved: child:${dependency.transitionName}" )
        super.onDependentViewRemoved(parent, child, dependency)
    }

    /**
     * 第一个生命周期方法
     */
    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        Log.e(TAG, "onAttachedToLayoutParams: ${params.anchorId}" )
        super.onAttachedToLayoutParams(params)
    }

    override fun getScrimColor(parent: CoordinatorLayout, child: AppCompatImageView): Int {
//        return super.getScrimColor(parent, child)
        return Color.RED
    }

    override fun getScrimOpacity(parent: CoordinatorLayout, child: AppCompatImageView): Float {
//        return super.getScrimOpacity(parent, child)
        return 0.5f
    }

}