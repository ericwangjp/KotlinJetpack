package com.example.kotlinjetpack.view

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemCategoryContentTitleBinding
import com.example.kotlinjetpack.interfaces.CheckListener
import com.example.kotlinjetpack.model.RightModel


/**
 * desc: ProductItemHeaderDecoration
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/6 18:01
 */
class ProductItemHeaderDecoration(val context: Context, private var dataList: List<RightModel>) :
    RecyclerView.ItemDecoration() {
    private var mTitleHeight = 0f
    private var mInflater: LayoutInflater? = null
    private var mCheckListener: CheckListener? = null


    init {
        mTitleHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 40f, context.resources.displayMetrics
        )
        mInflater = LayoutInflater.from(context)
    }

    fun setCheckListener(checkListener: CheckListener?) {
        mCheckListener = checkListener
    }

    fun setData(mDatas: List<RightModel>) {
        this.dataList = mDatas
    }

    companion object {
        // 标记当前左侧选中的 position，因为有可能选中的 item，右侧不能置顶，所以强制替换掉当前的tag
        var mCurrentParentPos = 0
        var mIsClick = false
        fun setCurrentParentPosition(currentPos: Int, isClick: Boolean = false) {
            mCurrentParentPos = currentPos
            mIsClick = isClick
        }
    }


    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val firstVisiblePos: Int =
            (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d("pos--->", firstVisiblePos.toString())
        var parentPos = dataList[firstVisiblePos].parentPos
        val child: View? = parent.findViewHolderForLayoutPosition(firstVisiblePos)?.itemView
        var isTranslate = false //canvas是否平移的标志
        if (dataList[firstVisiblePos].parentPos != dataList[firstVisiblePos + 1].parentPos) {
            parentPos = dataList[firstVisiblePos].parentPos
            val childHeight: Int = (child?.height ?: 0) + (child?.top ?: 0)
            Log.d("fqy-->", "childHeight: $childHeight")
            // body 平移
            if (childHeight < mTitleHeight) {
                canvas.save()
                isTranslate = true
                canvas.translate(0f, childHeight - mTitleHeight)
            }
        }
        drawHeader(parent, firstVisiblePos, canvas)
        if (isTranslate) {
            canvas.restore()
        }
        Log.d("fqy", "tag---> $parentPos<=>$mCurrentParentPos")
        if (parentPos != mCurrentParentPos) {
            if (!mIsClick) {
                mCurrentParentPos = parentPos
            }
            if (mCheckListener != null) {
                mCheckListener?.check(
                    if (mIsClick) mCurrentParentPos else parentPos, false
                )
                mIsClick = false
            } else {
                Log.e("tag--->", "mCheckListener is Null")
            }
        }

        // 子列表
//        val firstCompleteVisiblePos: Int =
//            (parent.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//        Log.e("fqy-->", "第一个完全可见: $firstCompleteVisiblePos")
//        val firstCompleteTop = parent.getChildAt(firstCompleteVisiblePos).top
//        Log.e("fqy-->", "第一个完全可见顶部: $firstCompleteTop")
//        val firstTop = parent.getChildAt(firstVisiblePos).top
//        Log.e("fqy-->", "第一个可见顶部: $firstTop")
//        val curParentPos = dataList[firstVisiblePos].parentPos
//        val upTotalPos = dataList.filter { it.parentPos < curParentPos }.size
//        Log.e("fqy-->", "item 计算: $curParentPos - $upTotalPos")
//        val selPos = firstVisiblePos - upTotalPos
//        if (firstTop <= dp2px(40f)) {
//            Log.e("fqy-->", "选中: $selPos")
//        }

//        var selPos = if (curParentPos == 0) {
//            firstVisiblePos
//        } else {
//            firstVisiblePos - upTotalPos
//        }
//        if (firstTop == dp2px(40f)) {
//            mCategoryChildCheckListener?.check(selPos)
//        }
    }

    /**
     * @param parent
     * @param pos
     */
    private fun drawHeader(parent: RecyclerView, pos: Int, canvas: Canvas) {
        val titleBinding = ItemCategoryContentTitleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        titleBinding.tvTitle.text = dataList[pos].titleName
        //绘制title开始
        var lp: ViewGroup.LayoutParams? = titleBinding.root.layoutParams
        if (lp == null) {
            lp = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            titleBinding.root.layoutParams = lp
        }
        val widthMeasureSpec: Int = when (lp.width) {
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                //如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec
                View.MeasureSpec.makeMeasureSpec(
                    parent.width - parent.paddingLeft - parent.paddingRight,
                    View.MeasureSpec.EXACTLY
                )
            }
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                //如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec
                View.MeasureSpec.makeMeasureSpec(
                    parent.width - parent.paddingLeft - parent.paddingRight,
                    View.MeasureSpec.AT_MOST
                )
            }
            else -> {
                //否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec
                View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY)
            }
        }
        //高度同理
        val heightMeasureSpec: Int = when (lp.height) {
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                View.MeasureSpec.makeMeasureSpec(
                    parent.height - parent.paddingTop - parent.paddingBottom,
                    View.MeasureSpec.EXACTLY
                )
            }
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                View.MeasureSpec.makeMeasureSpec(
                    parent.height - parent.paddingTop - parent.paddingBottom,
                    View.MeasureSpec.AT_MOST
                )
            }
            else -> {
                View.MeasureSpec.makeMeasureSpec(mTitleHeight.toInt(), View.MeasureSpec.EXACTLY)
            }
        }
        //依次调用 measure,layout,draw方法，将头部显示在屏幕上
        titleBinding.root.apply {
            measure(widthMeasureSpec, heightMeasureSpec)
            layout(
                parent.paddingLeft,
                parent.paddingTop,
                parent.paddingLeft + measuredWidth,
                parent.paddingTop + measuredHeight
            )
            draw(canvas) //Canvas默认在视图顶部，无需平移，直接绘制
        }
        //绘制title结束
    }
}