package com.example.kotlinjetpack.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

/**
 * desc: DivergeView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/30 8:55 下午
 */
class DivergeView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null as AttributeSet?,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr), Runnable {
    private val mRandom: Random = Random()
    private val mDivergeInfos by lazy {
        ArrayList<DivergeInfo>(30)
    }
    private var mQueen = Collections.synchronizedList(ArrayList<Int>(30))
    private var mPtStart: PointF? = null
    private var mPtEnd: PointF? = null
    private val mDeadPool by lazy {
        ArrayList<DivergeInfo>()
    }
    private var mPaint: Paint? = null
    private var mDivergeViewProvider: DivergeViewProvider? = null
    private var mLastAddTime: Long = 0
    private var mThread: Thread? = null
    private var mRunning: Boolean = true
    private var mIsDrawing: Boolean = false

    override fun run() {
        while (mRunning) {
            if (mDivergeViewProvider != null && mQueen != null && !mIsDrawing && mDivergeInfos != null) {
                dealQueen()
                if (mDivergeInfos.size != 0) {
                    dealDiverge()
                    mIsDrawing = true
                    this.postInvalidate()
                }
            }
        }
        release()
    }

    private fun dealDiverge() {
        var i = 0
        while (i < mDivergeInfos.size) {
            val divergeInfo = mDivergeInfos[i]
            val timeLeft = 1.0f - divergeInfo.mDuration
            divergeInfo.mDuration += 0.01f
            val time1 = timeLeft * timeLeft
            val time2 = 2.0f * timeLeft * divergeInfo.mDuration
            val time3 = divergeInfo.mDuration * divergeInfo.mDuration
            val x =
                time1 * mPtStart!!.x + time2 * divergeInfo.mBreakPoint.x + time3 * divergeInfo.mEndPoint.x
            divergeInfo.mX = x
            val y =
                time1 * mPtStart!!.y + time2 * divergeInfo.mBreakPoint.y + time3 * divergeInfo.mEndPoint.y
            divergeInfo.mY = y
            if (divergeInfo.mY <= divergeInfo.mEndPoint.y) {
                mDivergeInfos.removeAt(i)
                mDeadPool.add(divergeInfo)
                --i
            }
            ++i
        }
    }

    private fun dealQueen() {
        val now = System.currentTimeMillis()
        if (mQueen.size > 0 && now - mLastAddTime > 200L) {
            mLastAddTime = System.currentTimeMillis()
            var divergeInfo: DivergeInfo? = null
            if (mDeadPool.size > 0) {
                divergeInfo = mDeadPool[0]
                mDeadPool.removeAt(0)
            }
            if (divergeInfo == null) {
                divergeInfo = createDivergeNode(mQueen[0])
            }
            divergeInfo.reset()
            divergeInfo.mType = mQueen[0]
            mDivergeInfos.add(divergeInfo)
            mQueen.removeAt(0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    fun setDivergeViewProvider(divergeViewProvider: DivergeViewProvider?) {
        mDivergeViewProvider = divergeViewProvider
    }

    fun getStartPoint(): PointF? {
        return mPtStart
    }

    fun isRunning(): Boolean {
        return mRunning
    }

    fun startDiverges(index: Int) {
        mQueen.add(index)
        if (mThread == null) {
            mThread = Thread(this)
            mThread!!.start()
        }
    }

    fun stop() {
        mDivergeInfos.clear()
        if (mQueen != null) {
            mQueen.clear()
        }
        mDeadPool.clear()
    }

    fun release() {
        stop()
        mPtEnd = null
        mPtStart = null
        mDivergeInfos.clear()
        mQueen.clear()
        mDeadPool.clear()
    }

    fun setStartPoint(point: PointF?) {
        mPtStart = point
    }

    fun setEndPoint(point: PointF?) {
        mPtEnd = point
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mRunning = false
    }

    override fun onDraw(canvas: Canvas) {
        if (mRunning && mDivergeViewProvider != null && mDivergeInfos != null) {
            val var2: Iterator<*> = mDivergeInfos!!.iterator()
            while (var2.hasNext()) {
                val divergeInfo = var2.next() as DivergeInfo
                mPaint!!.alpha = (255.0f * divergeInfo.mY / mPtStart!!.y).toInt()
                canvas.drawBitmap(
                    mDivergeViewProvider!!.getBitmap(divergeInfo.mType)!!,
                    divergeInfo.mX,
                    divergeInfo.mY,
                    mPaint
                )
            }
        }
        mIsDrawing = false
    }

    private fun getBreakPointF(scale1: Int, scale2: Int): PointF {
        val pointF = PointF()
        pointF.x =
            (mRandom.nextInt((this.measuredWidth - this.paddingRight + this.paddingLeft) / scale1) + this.measuredWidth / scale2).toFloat()
        pointF.y =
            (mRandom.nextInt((this.measuredHeight - this.paddingBottom + this.paddingTop) / scale1) + this.measuredHeight / scale2).toFloat()
        return pointF
    }

    protected fun createDivergeNode(type: Any?): DivergeInfo {
        var endPoint = mPtEnd
        if (endPoint == null) {
            endPoint = PointF(mRandom.nextInt(this.measuredWidth).toFloat(), 0.0f)
        }
        if (mPtStart == null) {
            mPtStart = PointF(
                (this.measuredWidth / 2).toFloat(),
                (this.measuredHeight - 100).toFloat()
            )
        }
        return DivergeInfo(
            mPtStart!!.x, mPtStart!!.y,
            getBreakPointF(2, 3), endPoint, type
        )
    }

    inner class DivergeInfo(
        var mX: Float,
        var mY: Float,
        breakPoint: PointF,
        var mEndPoint: PointF,
        type: Any?
    ) {
        var mDuration = 0.0f
        var mBreakPoint: PointF
        var mType: Any?
        var mStartX: Float
        var mStartY: Float
        fun reset() {
            this.mDuration = 0.0f
            mX = mStartX
            mY = mStartY
        }

        init {
            mStartX = mX
            mStartY = mY
            mBreakPoint = breakPoint
            mType = type
        }
    }

    interface DivergeViewProvider {
        fun getBitmap(var1: Any?): Bitmap?
    }

    companion object {
        const val mDuration = 0.01f
        const val mDefaultHeight = 100
        protected const val mQueenDuration = 200L
    }

    init {
        mPaint = Paint(1)
    }
}
