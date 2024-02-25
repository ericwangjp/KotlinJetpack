package com.example.kotlinjetpack.activity.linkageScroll

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.kotlinjetpack.adapter.ProductCategoryChildAdapter
import com.example.kotlinjetpack.adapter.ProductCategoryContentAdapter
import com.example.kotlinjetpack.adapter.ProductCategoryParentAdapter
import com.example.kotlinjetpack.constants.productCategoryJsonString
import com.example.kotlinjetpack.databinding.ActivityScrollLinkageListBinding
import com.example.kotlinjetpack.interfaces.CheckListener
import com.example.kotlinjetpack.interfaces.OnItemClickListener
import com.example.kotlinjetpack.model.CategoryContentListModel
import com.example.kotlinjetpack.model.CategoryTwoArray
import com.example.kotlinjetpack.model.RightModel
import com.example.kotlinjetpack.utils.dp2px
import com.example.kotlinjetpack.view.ProductItemHeaderDecoration
import com.google.gson.Gson
import com.luck.picture.lib.utils.ToastUtils

class ScrollLinkageListActivity : AppCompatActivity(), CheckListener {
    private lateinit var binding: ActivityScrollLinkageListBinding
    private lateinit var mProductCategoryListAdapter: ProductCategoryParentAdapter
    private lateinit var mProductCategoryContentAdapter: ProductCategoryContentAdapter
    private val leftListLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val rightListLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val categoryContentList: ArrayList<RightModel> = arrayListOf()
    private var move: Boolean = false
    private val onContentScrollListener: ContentListScrollListener by lazy {
        ContentListScrollListener()
    }
    private var targetPosition: Int = 0
    private var lastChildPosition: Int = 0
    private lateinit var dataList: CategoryContentListModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollLinkageListBinding.inflate(layoutInflater)
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
                    targetPosition = position
                    setChecked(position, true)
                }

                override fun onItemLongClick(view: View?, position: Int) {

                }
            }) { leftPos, rightPos, offset ->
//                左侧内层列表点击滚动
                rightListLayoutManager.scrollToPositionWithOffset(rightPos, offset)
                //            将左边点击的位置作为当前的tag
                ProductItemHeaderDecoration.setCurrentParentPosition(leftPos, true)
                // TODO: 可优化左侧计算子item再次滚动居中
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

        mProductCategoryContentAdapter =
            ProductCategoryContentAdapter(categoryContentList, object : OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    if (categoryContentList[position].isTitle) {
                        ToastUtils.showToast(this@ScrollLinkageListActivity, "点击了标题 $position")
                    } else {
                        ToastUtils.showToast(this@ScrollLinkageListActivity, "点击了列表 $position")
                    }
                }

                override fun onItemLongClick(view: View?, position: Int) {

                }
            })
        val productItemHeaderDecoration =
            ProductItemHeaderDecoration(this, categoryContentList).apply {
                setCheckListener(this@ScrollLinkageListActivity)
//                setChildCheckListener(object : CategoryChildCheckListener {
//                    override fun check(position: Int) {
//                        Log.e("fqy-->", "选中位置: $position")
//                    }
//                })
            }
        binding.rvContentList.apply {
            layoutManager = rightListLayoutManager
            addItemDecoration(productItemHeaderDecoration)
            adapter = mProductCategoryContentAdapter
        }

    }

    private fun setChecked(position: Int, isLeft: Boolean) {
        Log.e("点击位置: ", "点击位置==>$position")
        if (isLeft) {
            mProductCategoryListAdapter.setSelection(position)
            var count = 0
            for (i in 0 until position) {
                count += dataList.categoryOneArray[i].categoryTwoArray.size
            }
            count += position
            binding.rvContentList.stopScroll()
            rightListLayoutManager.scrollToPositionWithOffset(count, 0)
//            将左边点击的位置作为当前的tag
            ProductItemHeaderDecoration.setCurrentParentPosition(position, true)
        } else {
            mProductCategoryListAdapter.setSelection(position)
//            如果是滑动右边联动左边，则按照右边传过来的位置作为tag
//            productItemHeaderDecoration.setCurrentTag(position.toString())
        }
        leftSmoothScrollToPosition(position)
    }

    // 将当前选中的item居中
    private fun leftSmoothScrollToPosition(position: Int) {
        // 将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        // todo 待优化快速滚动可能导致 NullPointerException crash binding.rvCategoryList.g…rstVisibleItemPosition()) must not be null
        val firstVisiblePosition = leftListLayoutManager.findFirstVisibleItemPosition()
        binding.rvCategoryList.postDelayed({
            val centerView: View? =
                binding.rvCategoryList.getChildAt(position - firstVisiblePosition)
            if (centerView != null) {
                val y: Int = centerView.top - binding.rvCategoryList.height / 2
                binding.rvCategoryList.smoothScrollBy(0, y)
            }
        }, 100)

    }


    override fun onDestroy() {
        super.onDestroy()
        binding.rvContentList.removeOnScrollListener(onContentScrollListener)
    }


    private inner class ContentListScrollListener : OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.e("onScrollStateChanged: ", "右侧 onScrollStateChanged ==>$newState")
//            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                move = false
//                val n: Int = mRightIndex - rightListLayoutManager.findFirstVisibleItemPosition()
//                Log.d("n---->", n.toString())
//                if (0 <= n && n < binding.rvContentList.childCount) {
//                    val top: Int = binding.rvContentList.getChildAt(n).top
//                    Log.d("top--->", top.toString())
//                    binding.rvContentList.smoothScrollBy(0, top)
//                }
//            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            Log.e(" onScrolled: ", "单次滚动距离==》$dy")
            Log.e("fqy-->", "滚动方向: ${if (dy > 0) "向上" else "向下"}")
            val firstVisiblePos: Int = rightListLayoutManager.findFirstVisibleItemPosition()
            Log.e("fqy-->", "第一个可见item: $firstVisiblePos")
            val curParentPos = categoryContentList[firstVisiblePos].parentPos
            val upTotalPos = categoryContentList.filter { it.parentPos < curParentPos }.size
            val selPos = firstVisiblePos - upTotalPos
            Log.e("fqy-->", "计算位置: $selPos")
            val top: Int = rightListLayoutManager.findViewByPosition(firstVisiblePos)?.top ?: 0
            Log.e("fqy-->", "aaa距离顶部距离: $top - $firstVisiblePos - $upTotalPos")
            if (dy > 0 && (lastChildPosition != selPos) && (top <= -dp2px(40f))) {
                Log.e("fqy-->", "aaa选中位置-1: $selPos")
                val holder = binding.rvCategoryList.findViewHolderForLayoutPosition(curParentPos)
                if (holder != null) {
                    val childAdapter =
                        (holder as ProductCategoryParentAdapter.LinkageListParentHolder).itemViewBinding.rvCategoryChildList.adapter
                    if (childAdapter != null) {
                        (childAdapter as ProductCategoryChildAdapter).setSelection(selPos)
                    }

                }
                lastChildPosition = selPos
            } else if (dy < 0 && (lastChildPosition != selPos) && (top >= -dp2px(40f))) {
                Log.e("fqy-->", "aaa选中位置-2: $selPos")
                val holder = binding.rvCategoryList.findViewHolderForLayoutPosition(curParentPos)
                if (holder != null) {
                    val childAdapter =
                        (holder as ProductCategoryParentAdapter.LinkageListParentHolder).itemViewBinding.rvCategoryChildList.adapter
                    if (childAdapter != null) {
                        (childAdapter as ProductCategoryChildAdapter).setSelection(selPos)
                    }

                }
                lastChildPosition = selPos
            }
        }
    }

    override fun check(position: Int, isLeft: Boolean) {
        Log.e("点击位置: ", "点击位置==>$position")
        setChecked(position, isLeft)
    }
}