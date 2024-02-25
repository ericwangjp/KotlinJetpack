package com.example.kotlinjetpack.utils

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.adapter.UploadPicAdapter
import java.util.*
import kotlin.math.log


class DragHelper(
    private val onDragListener: OnDragListener?,
    private val deleteView: View,
    private val uploadPicAdapter: UploadPicAdapter
) :
    ItemTouchHelper.Callback() {

    companion object {
        private const val TAG = "DragHelper"
    }

    private var deletePos = -1
    private val itemScaleRate = 1.2f

    //    private var releaseHand = false
    private var mIsInside = false
    private var tempViewHolder: RecyclerView.ViewHolder? = null

    /**
     * 设置item是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        Log.e(TAG, "getMovementFlags: -->开始处理拖动和滑动事件")
        var dragFlag: Int = 0
        var swipeFlag: Int = 0
//        val manager = recyclerView.layoutManager
//        if (manager is GridLayoutManager) {
        if (viewHolder is UploadPicAdapter.PicViewHolder) {
            dragFlag =
                ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }
//        else {
//            dragFlag = ItemTouchHelper.DOWN or ItemTouchHelper.UP
//            swipeFlag = ItemTouchHelper.END or ItemTouchHelper.START
//        }

        return makeMovementFlags(dragFlag, swipeFlag)
    }

    /**
     * 当用户从item原来的位置拖动可以拖动的item到新位置的过程中调用
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        Log.e(TAG, "onMove: -->")
        if (viewHolder.itemViewType != target.itemViewType) {
            return false
        }
//        处理 + 不可拖动，不可替换，和下面二选一均可
//        if (fromPosition == dataList?.size ?: 0 || toPosition == dataList?.size ?: 0) {
//            return false
//        }
        if (target is UploadPicAdapter.AddViewHolder) {
            return false
        }

        val dataList = uploadPicAdapter.getDataList()
        if (dataList == null || dataList.size < 2) {
            return false
        }

        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        deletePos = toPosition
        Log.e(TAG, "onMove: 开始位置：$fromPosition")
        Log.e(TAG, "onMove: 结束位置：$toPosition")
//        Collections.swap(dataList, fromPosition, toPosition)
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(
                    dataList,
                    i,
                    i + 1
                )
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(
                    dataList,
                    i,
                    i - 1
                )
            }
        }
        uploadPicAdapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    /**
     * 滑动到消失后的调用
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.e(TAG, "onSwiped:--> $direction")
        val position = viewHolder.adapterPosition
        if ((direction == ItemTouchHelper.END) or (direction == ItemTouchHelper.START)) {
//            adapter.getDataList().remove(position)
//            adapter.notifyItemRemoved(position)
        }
    }

    /**
     * RecyclerView调用onDraw时调用，如果想自定义item对用户互动的响应,可以重写该方法
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        Log.e(TAG, "onChildDraw: child绘制")
        // 判断拖动item是否和删除区域有重叠
        val deleteViewWidth = deleteView.width
        val deleteViewHeight = deleteView.height
        val deleteViewOut = IntArray(2)
        deleteView.getLocationInWindow(deleteViewOut)
        val deleteViewX = deleteViewOut[0]
        val deleteViewY = deleteViewOut[1]
        Log.e(TAG, "deleteView: X：$deleteViewX，Y：$deleteViewY")

        val itemWidth = viewHolder.itemView.width
        val itemHeight = viewHolder.itemView.height
        val itemViewOut = IntArray(2)
        viewHolder.itemView.getLocationInWindow(itemViewOut)
        val itemViewX = itemViewOut[0]
        val itemViewY = itemViewOut[1]
        Log.e(TAG, "itemView: X：$itemViewX，Y：$itemViewY")
        Log.e(TAG, "onChildDraw: dx:$dX，dy：$dY")

        val scaleItemWidth = itemWidth * itemScaleRate
        val scaleItemHeight = itemHeight * itemScaleRate

        val centerX = itemViewX + scaleItemWidth
        val centerY = itemViewY + scaleItemHeight

        val isInside = (centerY > deleteViewY
                && deleteViewY < deleteViewY + deleteViewHeight
                && centerX > deleteViewX
                && deleteViewX < deleteViewX + deleteViewWidth)
        Log.e(TAG, "是否在删除区域: $isInside")
        if (isInside != mIsInside) {
            onDragListener?.onDragAreaChange(isInside)
        }
        this.mIsInside = isInside
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        Log.e(TAG, "onChildDrawOver: child上层绘制")
    }

    /**
     * 设置是否可以长按拖拽，默认为true
     */
    override fun isLongPressDragEnabled(): Boolean {
//        return super.isLongPressDragEnabled()
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    /**
     * 设置手指离开后ViewHolder的动画时间，在用户手指离开后调用
     */
    override fun getAnimationDuration(
        recyclerView: RecyclerView,
        animationType: Int,
        animateDx: Float,
        animateDy: Float
    ): Long {
        Log.e(TAG, "getAnimationDuration: 获取动画执行时间：")
        if (mIsInside) {
            return 0
        }
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
    }

    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        Log.e(TAG, "onSelectedChanged: 选中view，状态：$actionState")
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            deletePos = viewHolder?.adapterPosition ?: -1
            tempViewHolder = viewHolder
            viewHolder?.itemView?.let { view ->
                ValueAnimator.ofFloat(1f, itemScaleRate).apply {
                    duration = 200
                    addUpdateListener {
                        var value = it.animatedValue as Float
                        view.scaleX = value
                        view.scaleY = value
                    }
                }.start()
            }
            onDragListener?.onStartDrag()
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (mIsInside && deletePos >= 0) {
                Log.e(TAG, "onChildDraw: 用户在删除区松开手")
                tempViewHolder?.let {
                    it.itemView.visibility = View.INVISIBLE
                    uploadPicAdapter.removeDragData(deletePos)
                }
                mIsInside = false
            }
            deletePos = -1
            tempViewHolder = null
        }
    }

    /**
     * 当用户与item的交互结束并且item也完成了动画时调用
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        Log.e(TAG, "clearView: 动画执行结束")
        viewHolder.itemView.let { view ->
            ValueAnimator.ofFloat(itemScaleRate, 1f).apply {
                duration = 200
                addUpdateListener {
                    var value = it.animatedValue as Float
                    view.scaleX = value
                    view.scaleY = value
                }
            }.start()
        }
        onDragListener?.onStopDrag()
    }

    interface OnDragListener {
        fun onStartDrag()
        fun onStopDrag()
        fun onDragAreaChange(isInDeleteArea: Boolean)
    }

}