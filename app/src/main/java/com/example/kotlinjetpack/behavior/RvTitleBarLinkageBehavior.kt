package com.example.kotlinjetpack.behavior

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * desc: RvTitleBarLinkageBehavior
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/8/11 16:56
 */
class RvTitleBarLinkageBehavior(context: Context, attributeSet: AttributeSet?) :
    CoordinatorLayout.Behavior<LinearLayout>(context, attributeSet),Animator.AnimatorListener {

    var listener: ((percent: Float, title: String) -> Unit)? = null
    private var animaState = false

    companion object {
        const val MAX_PEEK = 1300f
        const val ALPHA_SPEED = 3f * 100
        const val ANIM_DURATION = 300L
        const val SCALE_PERCENT = 0.15f
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: LinearLayout,
        dependency: View
    ): Boolean {
//        return super.layoutDependsOn(parent, child, dependency)
        return dependency is RecyclerView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: LinearLayout,
        dependency: View
    ): Boolean {
//        return super.onDependentViewChanged(parent, child, dependency)
        if (child.translationY >= 0) {
            // header固定状态下
            // diff得出的就是rv顶部与header下边界的相差距离，也就是还差多少可以进入下一阶段
            // ALPHA_SPEED是一个阈值距离，就是多少距离开始进入渐变状态
            val diff = dependency.translationY - child.height
            if (diff < ALPHA_SPEED && diff >= 0) {
                // 这里转化为百分比
                child.alpha = (ALPHA_SPEED - diff) / ALPHA_SPEED
            } else if (diff >= ALPHA_SPEED) {
                child.alpha = 0f
            } else {
                child.alpha = 1f
            }
        }
        return true

    }

    /**
     * @child: 滚动组件自身
     * @target: 协调的目标view
     * @dx: x轴的滑动，向右为x轴u正方向
     * @dy: y轴的滑动，向下为y轴正方向
     * @consumed: 消费数组，[x,y]记录了x\y轴的滑动消费情况，如果不消费，Rv在后续环节还会自身滑动，因为没有消费完
     */
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        Log.e("fqy-onNestedPreScroll", "dx: $dx")
        Log.e("fqy-onNestedPreScroll", "dy: $dy")
        if (animaState) {
            // 动画正在执行中，所有滑动全部消费掉
            consumed[1] += dy
            return
        }
        if (type != ViewCompat.TYPE_TOUCH) {
            if (child.translationY >= 0) {
                // 如果顶部header还在，那就屏蔽fling
                consumed[1] += dy
                return
            }
        }

        if (dy > 0) {
            // 上滑
            Log.e("fqy-onNestedPreScroll", "target.translationY: ${target.translationY}")
            Log.e("fqy-onNestedPreScroll", "child.translationY: ${child.translationY}")
            Log.e("fqy-onNestedPreScroll", "child.height: ${child.height}")
            val y = target.translationY
            if (child.translationY >= 0 && y > child.height) {
                if (dy < y - child.height) {
                    target.translationY = y - dy
                    consumed[1] += dy
                } else {
                    target.translationY = child.height.toFloat()
                    consumed[1] += dy
                }
            } else {
                if (y > 0) {
                    if (y - dy >= 0) {
                        target.translationY = y - dy
                        child.translationY -= dy
                        consumed[1] += dy
                    } else {
                        child.translationY -= y
                        target.translationY = 0f
                        consumed[1] += y.toInt()
                    }
                    val percent = -child.translationY / child.height
                    child.scaleX = 1 - percent * SCALE_PERCENT
                    listener?.invoke(percent, "配送中")

                } else {

                }
            }
        } else {
            // 下滑
            (target as RecyclerView).let {
                val offsetY = it.computeVerticalScrollOffset()
                if (offsetY > 0) {
                    // 说明原来已经滑动过了，因为前面的推动都是translationY变化，影响不到它自身

                    // 这里写了两个判断，但是没作处理，是因为…做处理的话就会太丝滑了，在fling状态下就会忽闪忽闪的
                    // 所以我们的思路是，过度消费，也就全全由rv自己先去滑，因为它最多也就滑到header消失时刻的状态
                    if (offsetY + dy < 0) {
                        // 滑动的多了
                    } else {
                        // target自己可以处理
                    }
                } else {
                    if (target.translationY >= MAX_PEEK) {
                        // 已经到底了，不允许继续下拉了，你可以尝试不加这个，看看效果Hh
                        return
                    }
                    if (target.translationY - dy > MAX_PEEK) {
                        // 拉过头就没了，这个同上，都是对PEEK_HEIGHT的兜底
                        // 对了，对于这个PEEK需要设置多少，你可以通过rv的height-需要露出的height得出
                        target.translationY = MAX_PEEK
                        return
                    }

                    // header的translationY标志着它的情况
                    if (child.translationY < 0) {
                        // 需要把header一块滑下来
                        if (child.translationY < dy) { // 因为带有方向，所以这两个都是负数，你需要理解成距离会更加合适
                            // 滑动距离不足以滑完header，那就一起动
                            child.translationY -= dy
                            target.translationY -= dy
                            consumed[1] += dy
                        } else {
                            // 如果够滑完的话，header就需要固定住了，把剩余的translationY滑掉
                            // 这里也是过度消费的思路，因为滑动距离过剩了，但我们希望先拉到固定贴合的状态先
                            // 而不是直接就下去了，太丝滑会不太好
                            // 不信邪的可以试试hhh
                            target.translationY -= child.translationY
                            child.translationY = 0f
                            consumed[1] += dy
                        }
                        // ……这是一起推的阶段，还需要header进行一些scale和对外位移情况的暴露，先不关注
                        val percent = -child.translationY / child.height
                        child.scaleX = 1 - percent * SCALE_PERCENT
//                        child.scaleY = 1 - percent
                        listener?.invoke(percent, "配送中")

                    } else {
                        // header已经固定好了，那就自己滑好了
                        target.translationY -= dy
                        consumed[1] += dy
                    }
                }
            }

        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
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
        return axes.and(ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
        target: View,
        type: Int
    ) {
//        super.onStopNestedScroll(coordinatorLayout, child, target, type)
        if (type == ViewCompat.TYPE_TOUCH) {
            // 仅处理touch，区别与not_touch，如fling
            super.onStopNestedScroll(coordinatorLayout, child, target, type)
            val childY = child.height.toFloat()
            val y = target.translationY
            if (y < MAX_PEEK && y > childY) {
                // 处于在中间状态中，即第一阶段状态

                // 这里判别阈值设置了一半，也可以根据需要自行调整
                val mid = (MAX_PEEK + childY) / 2f
                if (y > mid) {
                    // 回缩
                    peekViewAnim(target, y, MAX_PEEK)
                } else {
                    // 展开
                    peekViewAnim(target, y, childY)
                }
            }
        }

    }

    private fun peekViewAnim(view: View, start: Float, end: Float) {
        if (animaState) {
            return
        }
        animaState = true
        val anim = ObjectAnimator.ofFloat(view, "translationY", start, end)
        anim.duration = ANIM_DURATION
        anim.addListener(this)
        anim.start()
    }

    override fun onAnimationStart(p0: Animator) {

    }

    override fun onAnimationEnd(p0: Animator) {
        animaState = false
    }

    override fun onAnimationCancel(p0: Animator) {

    }

    override fun onAnimationRepeat(p0: Animator) {

    }

}