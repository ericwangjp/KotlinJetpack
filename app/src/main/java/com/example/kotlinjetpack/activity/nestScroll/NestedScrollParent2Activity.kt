package com.example.kotlinjetpack.activity.nestScroll

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.adapter.BehaviorRCVAdapter
import com.example.kotlinjetpack.databinding.ActivityNestedScrollParent2Binding
import com.example.kotlinjetpack.utils.ViewWrapper
import com.example.kotlinjetpack.utils.dp2px
import com.jeremyliao.liveeventbus.LiveEventBus

class NestedScrollParent2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityNestedScrollParent2Binding

    //    private lateinit var binding: LayoutBehaviorOneBinding
    private var dataLists = arrayListOf<String>()
    private var isExpand = true
    private val TAG = "fqy折叠布局"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedScrollParent2Binding.inflate(layoutInflater)
//        binding = LayoutBehaviorOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        LiveEventBus.get<String>("update").observe(this) { s ->
            if (s.equals("expand")) {
                if (!isExpand) {
                    expandAnim()
                    isExpand = true
                }

            } else {
                if (isExpand) {
                    collapseAnim()
                    isExpand = false
                }

            }
        }
        for (i in 0..30) {
            dataLists.add("---> $i <--")
        }
        binding.rvList.let {
            it.layoutManager = LinearLayoutManager(this)
            it.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            it.adapter = BehaviorRCVAdapter(dataLists)
            it.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    Log.e(TAG, "onScrollStateChanged: $newState")
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.e(TAG, "onScrolled: $dy")
//                    if (!recyclerView.canScrollVertically(1)) {
//                    } else if (!recyclerView.canScrollVertically(-1)) {
//                        if (dy < 0) {
//                            if (!isExpand) {
//                                Log.e(TAG, "onScrolled: 展开")
//                                expandAnim()
//                                isExpand = true
//                            }
//                        }
//
//                    }
//                    val layoutManager = binding.rvList.layoutManager as LinearLayoutManager
//                    val first = layoutManager.findFirstCompletelyVisibleItemPosition()
//                    if (first > 0 && dy > 0) {
//                        if (isExpand) {
//                            Log.e(TAG, "onScrolled: 收起")
//                            collapseAnim()
//                            isExpand = false
//                        }
//                    }
                }
            })
        }

//        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            Log.e(TAG, "scrollY: $scrollY")
//            Log.e(TAG, "oldScrollY: $oldScrollY")
//            if (!binding.rvList.canScrollVertically(-1)) {
//                Log.e(TAG, "recyclerView 滚动到顶部 ")
//            } else if (!binding.rvList.canScrollVertically(1)) {
//                Log.e(TAG, "recyclerView 滚动到底部 ")
//            }
//            if (scrollY > oldScrollY) {
//                Log.e(TAG, "向上滚动")
//            } else {
//                Log.e(TAG, "向下滚动")
//            }
//            val layoutManager = binding.rvList.layoutManager as LinearLayoutManager
//            val first = layoutManager.findFirstCompletelyVisibleItemPosition()
//            Log.e(TAG, "第一个完全可见：$first")
//        })
    }

    private fun expandAnim() {
        val viewWrapper = ViewWrapper(binding.rlayoutAnim)
        val scaleAnimator: ObjectAnimator =
            ObjectAnimator.ofInt(viewWrapper, "Height", 0, dp2px(200f))
        val alphaAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(binding.rlayoutAnim, "alpha", 0.0f, 1.0f)
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleAnimator).with(alphaAnimator)
        animatorSet.duration = 100
        animatorSet.start()
    }


    private fun collapseAnim() {
        val viewWrapper = ViewWrapper(binding.rlayoutAnim)
        val scaleAnimator: ObjectAnimator =
            ObjectAnimator.ofInt(viewWrapper, "Height", dp2px(200f), 0)
        val alphaAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(binding.rlayoutAnim, "alpha", 1.0F, 0.0F)
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleAnimator).with(alphaAnimator)
        animatorSet.duration = 100
        animatorSet.start()
    }
}