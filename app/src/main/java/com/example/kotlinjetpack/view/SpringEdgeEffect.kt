package com.example.kotlinjetpack.view

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.EdgeEffect
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory

/**
 * desc: SpringEdgeEffect
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/7/3 10:08
 * 构建一个自定义的 EdgeEffectFactory 并设置给RecyclerView 即可实现滚动弹性效果
 * recyclerView?.edgeEffectFactory = SpringEdgeEffect()
 */
class SpringEdgeEffect : EdgeEffectFactory() {
    companion object {
//        当调用onAbsorb方法时，该常量用于确定平移速度的大小，可根据需要调节
        private const val FLING_TRANSLATION_MAGNITUDE = 0.2f
    }

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
//        return super.createEdgeEffect(view, direction)

        return object : EdgeEffect(recyclerView.context) {

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationYDelta =
                    sign * recyclerView.width * deltaDistance * 0.8f
                Log.d("handlePull", "deltaDistance: $translationYDelta")
                recyclerView.forEach {
                    if (it.isVisible) {
                        // 设置每个RecyclerView的子item的translationY属性
                        recyclerView.getChildViewHolder(it).itemView.translationY += translationYDelta
                    }
                }
            }

            override fun onRelease() {
                super.onRelease()
                Log.d("onRelease", "onRelease")
                recyclerView.forEach {
                    //复位
                    val animator = ValueAnimator.ofFloat(
                        recyclerView.getChildViewHolder(it).itemView.translationY,
                        0f
                    ).apply {
                        duration = 500
                        interpolator = DecelerateInterpolator(2.0f)
                        addUpdateListener { valueAnimator ->
                            recyclerView.getChildViewHolder(it).itemView.translationY =
                                valueAnimator.animatedValue as Float
                        }
                    }
                    animator.start()
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                Log.d("onAbsorb", "onAbsorb")
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                recyclerView.forEach {
                    if (it.isVisible) {
                        // 在这个可以做动画
                        val currentTranslationY = it.translationY
                        val animator =
                            ValueAnimator.ofFloat(currentTranslationY, translationVelocity).apply {
                                duration = 500
                                interpolator = DecelerateInterpolator()
                                addUpdateListener { animation ->
                                    it.translationY = animation.animatedValue as Float
                                }
                            }
                        animator.start()
                    }
                }
            }

            override fun draw(canvas: Canvas?): Boolean {
                // 设置大小之后，就不会绘制 overscroll 阴影效果
                setSize(0, 0)
                return super.draw(canvas)
            }
        }
    }
}