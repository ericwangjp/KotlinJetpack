package com.example.kotlinjetpack.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.kotlinjetpack.R
import java.text.DecimalFormat

/**
 * desc: CustomCircleProgress
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/2/11 17:35
 */
class CustomCircleProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mCircleWidth: Float = 15f
    private var mCircleColor: Int? = null
    private var mBgCircleWidth: Float = 15f
    private var mBgCircleColor: Int? = null
    private var mIsAnimate: Boolean = true
    private var mAnimDuration: Int = 1000
    private var mValueText: String? = null
    private var mMaxValue: Float = 100f
    private var mStartAngle: Float = 270f
    private var mSweepAngle: Float = 360f
    private var mValueTextSize: Float? = null
    private var mValueTextColor: Int? = null
    private var mHint: CharSequence? = null
    private var mHintSize: Float? = null
    private var mHintColor: Int? = null
    private var mGradientColors: IntArray? = intArrayOf(Color.RED, Color.GRAY, Color.BLUE)
    private var mGradientColor: Int? = null
    private var mIsGradient: Boolean = false
    private var mIsShowShadow: Boolean = false
    private var mShadowSize: Float = 8f
    private var mShadowColor: Int? = null
    private var mProgress: Int = 20

    //圆心位置
    private val centerPosition: Point by lazy {
        Point()
    }

    //半径
    private var raduis: Float = 0f

    //声明边界矩形
    private val mRectF: RectF by lazy {
        RectF()
    }

    //颜色渐变色
    private var mSweepGradient: SweepGradient? = null


    //是否开启抗锯齿(默认开启)
    private var antiAlias: Boolean = true

    //声明背景圆画笔
    private lateinit var mBgCirclePaint: Paint

    //声明进度圆的画笔
    private lateinit var mProgressPaint: Paint

    //绘制进度数值
    private lateinit var mValuePaint: TextPaint

    //绘制提示文本
    private lateinit var mHintPaint: TextPaint

    //属性动画
    private var mAnimator: ValueAnimator? = null

    //动画进度
    private var mAnimPercent: Float = 0f

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        initAttrs(context, attrs, defStyleAttr)
        initPaint()
    }


    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleProgress)
        mCircleWidth = typedArray.getDimension(R.styleable.CustomCircleProgress_circleWidth, 15f)
        mCircleColor =
            typedArray.getColor(R.styleable.CustomCircleProgress_circleColor, Color.YELLOW)
        mBgCircleWidth =
            typedArray.getDimension(R.styleable.CustomCircleProgress_bgCircleWidth, 15f)
        mBgCircleColor =
            typedArray.getColor(R.styleable.CustomCircleProgress_bgCircleColor, Color.GRAY)
        mIsAnimate = typedArray.getBoolean(R.styleable.CustomCircleProgress_isAnimate, true)
        mAnimDuration = typedArray.getInt(R.styleable.CustomCircleProgress_animDuration, 1000)
        mValueText = typedArray.getString(R.styleable.CustomCircleProgress_valueText)
        mMaxValue = typedArray.getFloat(R.styleable.CustomCircleProgress_maxValue, 100f)
        mStartAngle = typedArray.getFloat(R.styleable.CustomCircleProgress_startAngle, 27f)
        mSweepAngle = typedArray.getFloat(R.styleable.CustomCircleProgress_sweepAngle, 360f)
        mValueTextSize =
            typedArray.getDimension(R.styleable.CustomCircleProgress_valueTextSize, 15f)
        mValueTextColor =
            typedArray.getColor(R.styleable.CustomCircleProgress_valueTextColor, Color.BLACK)
        mHint = typedArray.getString(R.styleable.CustomCircleProgress_hint)
        mHintSize = typedArray.getDimension(R.styleable.CustomCircleProgress_hintSize, 15f)
        mHintColor = typedArray.getColor(R.styleable.CustomCircleProgress_hintColor, Color.GRAY)
        mGradientColor =
            typedArray.getResourceId(R.styleable.CustomCircleProgress_gradientColor, 0)
        if (mGradientColor != 0) {
            mGradientColors = resources.getIntArray(mGradientColor!!)
        }
        mIsGradient = typedArray.getBoolean(R.styleable.CustomCircleProgress_isGradient, false)
        mIsShowShadow = typedArray.getBoolean(R.styleable.CustomCircleProgress_isShowShadow, false)
        mShadowSize = typedArray.getFloat(R.styleable.CustomCircleProgress_shadowSize, 8f)
        mShadowColor = typedArray.getColor(R.styleable.CustomCircleProgress_shadowColor, Color.BLUE)
        mProgress = typedArray.getInt(R.styleable.CustomCircleProgress_progress, 10)
        Log.e("mProgress", "initAttrs: $mProgress")

        typedArray.recycle()
    }


    private fun initPaint() {
        //圆画笔（主圆的画笔设置）
        mProgressPaint = Paint().apply {
            isAntiAlias = antiAlias //是否开启抗锯齿
            style = Paint.Style.STROKE //画笔样式
            strokeWidth = mCircleWidth //画笔宽度
            strokeCap = Paint.Cap.ROUND  //笔刷样式（圆角的效果）
            color = mCircleColor!!//画笔颜色
        }

        //背景圆画笔（一般和主圆一样大或者小于主圆的宽度）
        mBgCirclePaint = Paint().apply {
            isAntiAlias = antiAlias
            style = Paint.Style.STROKE
            strokeWidth = mBgCircleWidth
            strokeCap = Paint.Cap.ROUND
            color = mBgCircleColor!!
        }

        //初始化主题文字的字体画笔
        mValuePaint = TextPaint().apply {
            isAntiAlias = antiAlias  //是否抗锯齿
            textSize = mValueTextSize!!  //字体大小
            color = mValueTextColor!!  //字体颜色
            textAlign = Paint.Align.CENTER //从中间向两边绘制，不需要再次计算文字
//            typeface = TypefaceUtil.getSFSemobold(context) //字体加粗
        }

        //初始化提示文本的字体画笔
        mHintPaint = TextPaint().apply {
            isAntiAlias = antiAlias
            textSize = mHintSize!!
            color = mHintColor!!
            textAlign = Paint.Align.CENTER
//            typeface = TypefaceUtil.getSFRegular(context) //自定义字体
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //圆心位置
        centerPosition.x = w / 2
        centerPosition.y = h / 2

        //半径
        val maxCirWidth = Math.max(mCircleWidth, mBgCircleWidth)
        val minWidth = Math.min(
            w - paddingLeft - paddingRight - 2 * maxCirWidth - mShadowSize * 2,
            h - paddingBottom - paddingTop - 2 * maxCirWidth - mShadowSize * 2
        )

        raduis = minWidth / 2

        //矩形坐标
        mRectF.left = centerPosition.x - raduis - maxCirWidth / 2
        mRectF.top = centerPosition.y - raduis - maxCirWidth / 2
        mRectF.right = centerPosition.x + raduis + maxCirWidth / 2
        mRectF.bottom = centerPosition.y + raduis + maxCirWidth / 2

        if (mIsGradient) {
            setupGradientCircle() //设置圆环画笔颜色渐变
        }
    }

    /**
     * 使用渐变色画圆
     */
    private fun setupGradientCircle() {
        mSweepGradient =
            SweepGradient(
                centerPosition.x.toFloat(),
                centerPosition.y.toFloat(),
                mGradientColors!!,
                null
            )
        mProgressPaint.shader = mSweepGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawText(canvas)
        drawCircle(canvas)
    }

    private fun drawText(canvas: Canvas) {
        Log.e("绘制--》", "drawText: $mValueText")
        if (!mValueText.isNullOrBlank()) {
            canvas.drawText(
                mValueText!!,
                centerPosition.x.toFloat(),
                centerPosition.y.toFloat(),
                mValuePaint
            )
        }

        if (!mHint.isNullOrEmpty()) {
            canvas.drawText(
                mHint.toString(),
                centerPosition.x.toFloat(),
                centerPosition.y - mHintPaint.ascent() + 15,   //设置Y轴间距
                mHintPaint
            )
        }

    }

    private fun drawCircle(canvas: Canvas) {
        canvas.save()
        if (mIsShowShadow) {
            mProgressPaint.setShadowLayer(mShadowSize, 0f, 0f, mShadowColor!!) //设置阴影
        }

        //画背景圆（画一整个圆环）
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mBgCirclePaint)

        //画圆（计算出进度，按进度来绘制）
        val percent = mProgress.toFloat() / mMaxValue
        Log.e("绘制百分比", "drawCircle: $mProgress-$mMaxValue $percent")
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle * percent, false, mProgressPaint)
        canvas.restore()

    }

    /**
     * 执行属性动画
     */
    private fun startAnim(start: Float, end: Float, animTime: Int) {
        mAnimator = ValueAnimator.ofFloat(start, end)
        mAnimator?.duration = animTime.toLong()
        mAnimator?.addUpdateListener {
            //得到当前的动画进度并赋值
            mAnimPercent = it.animatedValue as Float
            mProgress = (mAnimPercent * mMaxValue).toInt()
            //根据当前的动画得到当前的值
            mValueText = if (mIsAnimate) {
                roundByScale((mAnimPercent * mMaxValue).toDouble(), 2)
            } else {
                roundByScale(mValueText!!.toDouble(), 0)
            }

            //不停的重绘当前值-表现出动画的效果
            postInvalidate()
        }
        mAnimator?.start()
    }

    //计算百分比的小数点后面的位数显示
    private fun roundByScale(v: Double, scale: Int): String {
        if (scale < 0) {
            throw IllegalArgumentException("参数错误，必须设置大于0的数字")
        }
        if (scale == 0) {
            return DecimalFormat("0").format(v)
        }
        var formatStr = "0."

        for (i in 0 until scale) {
            formatStr += "0"
        }
        return DecimalFormat(formatStr).format(v);
    }

    /**
     * 设置当前需要展示的值
     */
    fun setValue(value: String, maxValue: Float): CustomCircleProgress {
        if (isNum(value) && value != mValueText) {
            Log.e("设置进度值:", "setValue: $value")
            mValueText = value
            mMaxValue = maxValue

            //当前的进度和最大的进度，去做动画的绘制
            val start = mAnimPercent
            val end = value.toFloat() / maxValue
            startAnim(start, end, mAnimDuration)

        } else {
            mValueText = value
        }
        return this
    }

    /**
     * 设置动画时长
     * */
    fun setAnimTime(animTime: Int): CustomCircleProgress {
        this.mAnimDuration = animTime
        invalidate()
        return this
    }


    /**
     * 是否渐变色
     * */
    fun setIsGradient(isGradient: Boolean): CustomCircleProgress {
        this.mIsGradient = isGradient
        invalidate()
        return this
    }

    /**
     * 设置渐变色
     * */
    fun setGradientColors(gradientColors: IntArray): CustomCircleProgress {
        mGradientColors = gradientColors
        setupGradientCircle()
        return this
    }

    /**
     * 是否显示阴影
     * */
    fun setShadowEnable(enable: Boolean): CustomCircleProgress {
        mIsShowShadow = enable
        invalidate()
        return this
    }

    /**
     * 设置小数位数
     * */
    fun setDigit(digit: Int): CustomCircleProgress {
        mProgress = digit
        invalidate()
        return this
    }

    //判断当前的值是否是数字类型
    private fun isNum(str: String): Boolean {
        try {
            val toDouble = str.toDouble()
        } catch (e: Exception) {
            return false
        }
        return true
    }

}