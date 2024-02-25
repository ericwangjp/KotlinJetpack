package com.example.kotlinjetpack.activity.linkageScroll

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.kotlinjetpack.adapter.ProductCategoryChildAdapter
import com.example.kotlinjetpack.adapter.ProductCategoryContentAdapter
import com.example.kotlinjetpack.adapter.ProductCategoryParentAdapter
import com.example.kotlinjetpack.constants.productCategoryJsonString
import com.example.kotlinjetpack.databinding.ActivityScrollLinkageList2Binding
import com.example.kotlinjetpack.interfaces.OnItemClickListener
import com.example.kotlinjetpack.model.CategoryContentListModel
import com.example.kotlinjetpack.model.CategoryTwoArray
import com.example.kotlinjetpack.model.RightModel
import com.example.kotlinjetpack.utils.RecyclerViewSmoothScrollHelper
import com.example.kotlinjetpack.utils.dp2px
import com.google.gson.Gson
import com.luck.picture.lib.utils.ToastUtils

class ScrollLinkageList2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityScrollLinkageList2Binding
    private lateinit var mProductCategoryListAdapter: ProductCategoryParentAdapter
    private lateinit var mProductCategoryContentAdapter: ProductCategoryContentAdapter
    private val leftListLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val rightListLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val categoryContentList: ArrayList<RightModel> = arrayListOf()
    private val onContentScrollListener: ContentListScrollListener by lazy {
        ContentListScrollListener()
    }
    private var lastChildPosition: Int = 0
    private var lastParentPosition: Int = 0
    private lateinit var dataList: CategoryContentListModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollLinkageList2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
    }

    private fun initView() {
        binding.rvContentList.addOnScrollListener(onContentScrollListener)
    }

    private fun initData() {
//        数据源
        dataList = Gson().fromJson(productCategoryJsonString, CategoryContentListModel::class.java)

//        左侧列表
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        mProductCategoryListAdapter =
            ProductCategoryParentAdapter(dataList.categoryOneArray, object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    setChecked(position)
                }

                override fun onItemLongClick(view: View?, position: Int) {

                }
            }) { leftPos, rightPos, offset ->
//                左侧内层列表点击滚动
                rightListLayoutManager.scrollToPositionWithOffset(rightPos, offset)
                // TODO: 可优化左侧计算子item再次滚动居中
//                leftSmoothScrollToPosition(leftPos)
            }
        binding.rvCategoryList.apply {
            layoutManager = leftListLayoutManager
            addItemDecoration(dividerItemDecoration)
            adapter = mProductCategoryListAdapter
        }

//        右侧列表
        for (i in dataList.categoryOneArray.indices) {
            val head = RightModel(
                dataList.categoryOneArray[i].name,
                dataList.categoryOneArray[i].name,
                i,
                true,
                dataList.categoryOneArray[i].imgsrc
            )
            categoryContentList.add(head)
            val categoryTwoArray: List<CategoryTwoArray> =
                dataList.categoryOneArray[i].categoryTwoArray
            for (j in categoryTwoArray.indices) {
                val body = RightModel(
                    categoryTwoArray[j].name,
                    dataList.categoryOneArray[i].name,
                    i,
                    false,
                    categoryTwoArray[j].imgsrc
                )
                categoryContentList.add(body)
            }
        }

        binding.layoutDetailHeader.tvTitle.text = categoryContentList[0].titleName
        mProductCategoryContentAdapter =
            ProductCategoryContentAdapter(categoryContentList, object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    if (categoryContentList[position].isTitle) {
                        ToastUtils.showToast(this@ScrollLinkageList2Activity, "点击了标题 $position")
                    } else {
                        ToastUtils.showToast(this@ScrollLinkageList2Activity, "点击了列表 $position")
                    }
                }

                override fun onItemLongClick(view: View?, position: Int) {

                }
            })
        binding.rvContentList.apply {
            layoutManager = rightListLayoutManager
            adapter = mProductCategoryContentAdapter
        }

    }

    private fun setChecked(position: Int) {
        Log.e("点击位置: ", "外层列表点击位置==>$position")
        mProductCategoryListAdapter.setSelection(position)
        var count = 0
        for (i in 0 until position) {
            count += dataList.categoryOneArray[i].categoryTwoArray.size
        }
        count += position
        binding.rvContentList.stopScroll()
        rightListLayoutManager.scrollToPositionWithOffset(count, -dp2px(40f))
        leftSmoothScrollToPosition(position)
    }

    // 将当前选中的item居中
    private fun leftSmoothScrollToPosition(position: Int) {
        Log.e("fqy-->", "目标滚动位置: $position")
        // 将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
//        val firstVisiblePosition = leftListLayoutManager.findFirstVisibleItemPosition()
//        val dyPos = position - firstVisiblePosition
//        if (dyPos in 0 until binding.rvCategoryList.childCount) {
//            val targetView: View? =
//                binding.rvCategoryList.getChildAt(dyPos)
//            if (targetView != null) {
//                val y: Int = targetView.top - binding.rvCategoryList.height / 2
//                binding.rvCategoryList.smoothScrollBy(0, y)
//            }
//        }

//        将选中item 滚动到第3位
        val targetPos = position - 2
        if (targetPos < 0) return
        RecyclerViewSmoothScrollHelper.smoothScrollToPosition(
            binding.rvCategoryList, LinearSmoothScroller.SNAP_TO_START, targetPos
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.rvContentList.removeOnScrollListener(onContentScrollListener)
    }


    private inner class ContentListScrollListener : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.e("onScrollStateChanged: ", "右侧列表 onScrollStateChanged ==>$newState")
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            Log.e(" onScrolled: ", "单次滚动距离==》$dy")
            Log.e("fqy-->", "滚动方向: ${if (dy > 0) "向上" else "向下"}")
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePos: Int = layoutManager.findFirstVisibleItemPosition()
            if (firstVisiblePos > 0) {
                Log.e("fqy-->", "第一个可见item: $firstVisiblePos")
                binding.layoutDetailHeader.tvTitle.text =
                    categoryContentList[firstVisiblePos - 1].titleName
                val curParentPos = categoryContentList[firstVisiblePos - 1].parentPos
                if (lastParentPosition != curParentPos) {
                    mProductCategoryListAdapter.setSelection(curParentPos)
                    lastParentPosition = curParentPos
                    leftSmoothScrollToPosition(curParentPos)
                }


                val upTotalPos = categoryContentList.filter { it.parentPos < curParentPos }.size
//                var leftChildSelPos = if (dy > 0) {
//                    firstCompleteVisiblePos - upTotalPos
//                } else {
//                    firstVisiblePos - upTotalPos
//                }
                var leftChildSelPos = firstVisiblePos - upTotalPos - 1
                Log.e("fqy-->", "选中位置1: $leftChildSelPos")
//                if (leftChildSelPos > 0) {
//                    leftChildSelPos += 1
//                }
                Log.e("fqy-->", "选中位置2: $leftChildSelPos")
                val holder = binding.rvCategoryList.findViewHolderForLayoutPosition(curParentPos)
                if (holder != null) {
                    val childAdapter =
                        (holder as ProductCategoryParentAdapter.LinkageListParentHolder).itemViewBinding.rvCategoryChildList.adapter
                    if (childAdapter != null) {
                        (childAdapter as ProductCategoryChildAdapter).setSelection(
                            leftChildSelPos
                        )
                    }
                }


//                Log.e("fqy-->", "前几组item数: $upTotalPos")
//                Log.e("fqy-->", "左侧应选中 POS: $leftChildSelPos")
//                // 获取指定位置item的View信息
//                val childView =
//                    recyclerView.findViewHolderForLayoutPosition(firstVisiblePos)?.itemView
//                val top = recyclerView.paddingTop + (childView?.top ?: 0)
//                var bottom = recyclerView.paddingTop + (childView?.bottom ?: 0)
//                // 置顶判断，上滑时取值bottom，下滑时取值childView.bottom
//                Log.e("fqy-->", "onScrolled top: $top")
//                Log.e("fqy-->", "onScrolled bottom: $bottom")
//
//
//                if (dy < 0) {
//                    leftChildSelPos -= 1
//                }
//                if ((bottom in dp2px(35f)..dp2px(45f) || ((bottom in 0..dp2px(10f) && dy < 0))) && (lastChildPosition != leftChildSelPos)) {
//                    val holder =
//                        binding.rvCategoryList.findViewHolderForLayoutPosition(curParentPos)
//                    if (holder != null) {
//                        val childAdapter =
//                            (holder as ProductCategoryParentAdapter.LinkageListParentHolder).itemViewBinding.rvCategoryChildList.adapter
//                        if (childAdapter != null) {
//                            (childAdapter as ProductCategoryChildAdapter).setSelection(
//                                leftChildSelPos
//                            )
//                        }
//                    }
//                    lastChildPosition = leftChildSelPos
//                }
            }
        }

    }
}