package com.example.kotlinjetpack.view

import android.animation.*
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.kotlinjetpack.R
import java.util.*
import kotlin.math.pow


/**
 * desc: FlowLikeView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/30 7:28 下午
 */
class FlowLikeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr) {
    // 图片的集合
    private val mLikeDrawables by lazy {
        ArrayList<Drawable?>()
    }

    // 用于设置动画对象的位置参数
    private var mLayoutParams: LayoutParams? = null

    // 用于产生随机数,如生成随机图片
    private val mRandom: Random by lazy {
        Random()
    }

    // 控件的宽度
    private var mViewWidth = 0

    // 控件的高度
    private var mViewHeight = 0

    // 图片的宽度
    private var mPicWidth = 0

    // 图片的高度
    private var mPicHeight = 0

    // 在 XML 布局文件中添加的子View的总高度
    private var mChildViewHeight = 0

    private fun initParams() {
        mLikeDrawables.add(generateDrawable(R.drawable.ic_red_heart))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart1))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart2))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart3))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart4))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart5))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart6))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart7))
//        mLikeDrawables.add(generateDrawable(R.drawable.heart8))

        // 获取图片的宽高, 由于图片大小一致,故直接获取第一张图片的宽高
        mPicWidth = mLikeDrawables[0]?.intrinsicWidth ?: 0
        mPicHeight = mLikeDrawables[0]?.intrinsicHeight ?: 0

        // 初始化布局参数
        mLayoutParams = LayoutParams(mPicWidth, mPicHeight)
        mLayoutParams!!.addRule(CENTER_HORIZONTAL)
        mLayoutParams!!.addRule(ALIGN_PARENT_BOTTOM)
    }

    private fun generateDrawable(resID: Int): Drawable? {
        return ContextCompat.getDrawable(context, resID)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mChildViewHeight <= 0) {
            var i = 0
            val size = childCount
            while (i < size) {
                val childView = getChildAt(i)
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)
                mChildViewHeight += childView.measuredHeight
                i++
            }

            // 设置底部间距
            mLayoutParams!!.bottomMargin = mChildViewHeight
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = width
        mViewHeight = height
    }

    /**
     * 动态添加 FlowView
     */
    fun addLikeView() {
        val likeView = ImageView(context)
        likeView.setImageDrawable(mLikeDrawables[mRandom.nextInt(mLikeDrawables.size)])
        likeView.layoutParams = mLayoutParams
        addView(likeView)
        startAnimation(likeView)
    }

    private fun startAnimation(target: View) {
        // 设置进入动画
        val enterAnimator = generateEnterAnimation(target)
        // 设置路径动画
        val curveAnimator = generateCurveAnimation(target)

        // 设置动画集合, 先执行进入动画,最后再执行运动曲线动画
        val finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.setTarget(target)
        finalAnimatorSet.playSequentially(enterAnimator, curveAnimator)
        finalAnimatorSet.addListener(AnimationEndListener(target))
        finalAnimatorSet.start()
    }

    /**
     * 生成进入动画
     *
     * @return 动画集合
     */
    private fun generateEnterAnimation(target: View): AnimatorSet {
        val alpha = ObjectAnimator.ofFloat(target, "alpha", 0.2f, 1f)
        val scaleX = ObjectAnimator.ofFloat(target, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(target, "scaleY", 0.5f, 1f)
        val enterAnimation = AnimatorSet()
        enterAnimation.playTogether(alpha, scaleX, scaleY)
        enterAnimation.duration = 150
        enterAnimation.setTarget(target)
        return enterAnimation
    }

    /**
     * 生成曲线运动动画
     *
     * @return 动画集合
     */
    private fun generateCurveAnimation(target: View): ValueAnimator {
        val evaluator = CurveEvaluator(generateCTRLPointF(1), generateCTRLPointF(2))
        val valueAnimator = ValueAnimator.ofObject(
            evaluator,
            PointF(
                ((mViewWidth - mPicWidth) / 2).toFloat(),
                (mViewHeight - mChildViewHeight - mPicHeight).toFloat()
            ),
            PointF(
                (mViewWidth / 2 + (if (mRandom.nextBoolean()) 1 else -1) * mRandom.nextInt(
                    100
                )).toFloat(), 0f
            )
        )
        valueAnimator.duration = 3000
        valueAnimator.addUpdateListener(CurveUpdateLister(target))
        valueAnimator.setTarget(target)
        return valueAnimator
    }

    /**
     * 生成贝塞儿曲线的控制点
     *
     * @param value 设置控制点 y 轴上取值区域
     * @return 控制点的 x y 坐标
     */
    private fun generateCTRLPointF(value: Int): PointF {
        val pointF = PointF()
        pointF.x = (mViewWidth / 2 - mRandom.nextInt(100)).toFloat()
        pointF.y = mRandom.nextInt(mViewHeight / value).toFloat()
        return pointF
    }

    /**
     * 自定义估值算法, 计算对象当前运动的具体位置 Point
     * 由于这里使用的是三阶的贝塞儿曲线, 所以我们要定义两个控制点
     */
    private inner class CurveEvaluator(
        private val ctrlPointF1: PointF, private val ctrlPointF2: PointF
    ) :
        TypeEvaluator<PointF> {
        override fun evaluate(fraction: Float, startValue: PointF, endValue: PointF): PointF {

            // 这里运用了三阶贝塞儿曲线的公式
            val leftTime = 1.0f - fraction
            val resultPointF = PointF()

            // 三阶贝塞儿曲线
            resultPointF.x = leftTime.toDouble().pow(3.0)
                .toFloat() * startValue.x + 3 * leftTime.toDouble().pow(2.0)
                .toFloat() * fraction * ctrlPointF1.x + 3 * leftTime * fraction.toDouble().pow(2.0)
                .toFloat() * ctrlPointF2.x + fraction.toDouble().pow(3.0).toFloat() * endValue.x
            resultPointF.y = leftTime.toDouble().pow(3.0)
                .toFloat() * startValue.y + 3 * leftTime.toDouble().pow(2.0)
                .toFloat() * fraction * ctrlPointF1.y + 3 * leftTime * fraction * fraction * ctrlPointF2.y + fraction.toDouble()
                .pow(3.0).toFloat() * endValue.y

            // 二阶贝塞儿曲线
//            resultPointF.x = (float) Math.pow(leftTime, 2) * startValue.x + 2 * fraction * leftTime * ctrlPointF1.x
//                    + ((float) Math.pow(fraction, 2)) * endValue.x;
//            resultPointF.y = (float) Math.pow(leftTime, 2) * startValue.y + 2 * fraction * leftTime * ctrlPointF1.y
//                    + ((float) Math.pow(fraction, 2)) * endValue.y;
            return resultPointF
        }
    }

    /**
     * 动画曲线路径更新监听器, 用于动态更新动画作用对象的位置
     */
    private inner class CurveUpdateLister(private val target: View) :
        AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            // 获取当前动画运行的状态值, 使得动画作用对象沿着曲线(涉及贝塞儿曲线)运动
            val pointF = animation.animatedValue as PointF
            ViewCompat.setX(target, pointF.x)
            ViewCompat.setY(target, pointF.y)
            // 改变对象的透明度
            ViewCompat.setAlpha(target, 1 - animation.animatedFraction)
        }
    }

    /**
     * 动画结束监听器,用于释放无用的资源
     */
    private inner class AnimationEndListener(private val target: View) :
        AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            removeView(target)
        }
    }

    init {
        initParams()
    }
}