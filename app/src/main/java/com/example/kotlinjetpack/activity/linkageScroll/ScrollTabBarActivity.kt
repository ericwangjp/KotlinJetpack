package com.example.kotlinjetpack.activity.linkageScroll

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinjetpack.adapter.RcvIndicatorAdapter
import com.example.kotlinjetpack.adapter.TabPagerAdapter
import com.example.kotlinjetpack.databinding.ActivityScrollTabBarBinding
import com.example.kotlinjetpack.databinding.LayoutTabIndicatorBinding
import com.example.kotlinjetpack.utils.SmoothScrollLinearLayoutManager
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView


class ScrollTabBarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollTabBarBinding

    private val TAG = "ScrollTabBarActivity"
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var defaultSelPos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollTabBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }

    private fun initData() {
        val mTitleDataList =
            listOf("新闻", "电影", "娱乐", "美食", "科技", "热搜", "本地生活", "团购", "外卖", "明星", "音乐", "美图")
        binding.viewPager.apply {
            adapter = TabPagerAdapter(mTitleDataList)
            offscreenPageLimit = 0
        }
        val commonNavigator = CommonNavigator(this).apply {
//            isAdjustMode = true
            isSmoothScroll = true
            adapter = object : CommonNavigatorAdapter() {
                override fun getCount(): Int {
                    return mTitleDataList.size
                }

                override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                    val tabLayoutBinding = LayoutTabIndicatorBinding.inflate(layoutInflater)
                    tabLayoutBinding.tvTitle.text = mTitleDataList[index]
                    val commonPagerTitleView = CommonPagerTitleView(context).apply {
                        setContentView(tabLayoutBinding.root)
                        onPagerTitleChangeListener =
                            object : CommonPagerTitleView.OnPagerTitleChangeListener {
                                override fun onSelected(index: Int, totalCount: Int) {
                                    tabLayoutBinding.tvTitle.setTextColor(Color.BLUE)
                                }

                                override fun onDeselected(index: Int, totalCount: Int) {
                                    tabLayoutBinding.tvTitle.setTextColor(Color.BLACK)
                                }

                                override fun onLeave(
                                    index: Int,
                                    totalCount: Int,
                                    leavePercent: Float,
                                    leftToRight: Boolean
                                ) {
                                    // TODO: 可以进行图片缩放处理
                                }

                                override fun onEnter(
                                    index: Int,
                                    totalCount: Int,
                                    enterPercent: Float,
                                    leftToRight: Boolean
                                ) {
                                    // TODO: 可以进行图片缩放处理
                                }
                            }
                        setOnClickListener {
                            Log.e(TAG, "当前选中位置: $index")
                            binding.viewPager.setCurrentItem(index, true)
                        }
                    }
                    return commonPagerTitleView


//                    val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
//                    colorTransitionPagerTitleView.normalColor = Color.GRAY
//                    colorTransitionPagerTitleView.selectedColor = Color.BLACK
//                    colorTransitionPagerTitleView.text = mTitleDataList[index]
//                    colorTransitionPagerTitleView.setOnClickListener {
////                        mViewPager.setCurrentItem(
////                            index
////                        )
//                    }
//                    return colorTransitionPagerTitleView
                }

                override fun getIndicator(context: Context?): IPagerIndicator? {
//                    val indicator = LinePagerIndicator(context)
//                    indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
//                    return indicator
                    return null
                }
            }
        }
        binding.magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(binding.magicIndicator, binding.viewPager)

//        recyclerview 实现
        linearLayoutManager =
            SmoothScrollLinearLayoutManager(
                this@ScrollTabBarActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.rvList.apply {
            layoutManager = linearLayoutManager
            adapter = RcvIndicatorAdapter(mTitleDataList).apply {
                setOnItemClickListener(object : RcvIndicatorAdapter.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        setSelection(position)
                        val firstVisiblePos = linearLayoutManager.findFirstVisibleItemPosition()
                        val firstCompleteVisiblePos =
                            linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                        val lastVisiblePos = linearLayoutManager.findLastVisibleItemPosition()
                        val lastCompleteVisiblePos =
                            linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        Log.e(TAG, "第一个可见item: $firstVisiblePos")
                        Log.e(TAG, "第一个全部可见item: $firstCompleteVisiblePos")
                        Log.e(TAG, "最后一个可见item: $lastVisiblePos")
                        Log.e(TAG, "最后一个全部可见item: $lastCompleteVisiblePos")
                        if (position > defaultSelPos) {
                            if (position + 4 >= mTitleDataList.size) {
                                binding.rvList.smoothScrollToPosition(mTitleDataList.size - 1)
                            } else {
                                binding.rvList.smoothScrollToPosition(position + 4)
                            }
                        } else if (position < defaultSelPos) {
                            if (position - 4 <= 0) {
                                binding.rvList.smoothScrollToPosition(0)
                            } else {
                                binding.rvList.smoothScrollToPosition(position - 4)
                            }
                        }
                        defaultSelPos = position
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
            }
        }

    }

//    private fun initData() {
//        val listData: ArrayList<String> = arrayListOf()
//        for (i in 1..10) {
//            listData.add("第$i")
//        }
//        binding.rvChild.apply {
//            layoutManager = LinearLayoutManager(this@CommonOneActivity)
//            adapter = BehaviorRCVAdapter(listData)
//        }
//    }
}