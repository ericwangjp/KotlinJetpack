package com.example.kotlinjetpack.activity.commonTest

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.kotlinjetpack.adapter.WaterfallFlowAdapter
import com.example.kotlinjetpack.databinding.ActivityWaterfallFlowBinding
import com.example.kotlinjetpack.model.WaterfallFlowItem


class WaterfallFlowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWaterfallFlowBinding
    private val photoList = mutableListOf(
        "https://scpic.chinaz.net/files/pic/pic9/202109/apic35558.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202109/apic35353.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202105/apic32712.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202010/apic28503.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202007/apic26293.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202006/apic26080.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202005/apic25328.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202004/zzpic24401.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202003/zzpic23777.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202205/apic41123.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/202205/apic41017.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201912/zzpic22261.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201912/zzpic21613.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201912/zzpic21631.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201910/zzpic20731.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201911/zzpic20869.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201909/zzpic20143.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201908/zzpic19621.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201908/zzpic19796.jpg",
        "https://scpic.chinaz.net/files/pic/pic9/201907/zzpic19262.jpg",
    )
    private val mAdapter by lazy {
        WaterfallFlowAdapter(
            arrayListOf(),
            GestureDetector(this@WaterfallFlowActivity,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        Log.e("onDoubleTap: ", "->双击点赞")
                        return super.onDoubleTap(e)
                    }
                })
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterfallFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        val dataList = arrayListOf<WaterfallFlowItem>()
        for (i in photoList.indices) {
            dataList.add(
                WaterfallFlowItem(photoList[i], "我是说明 ${i + 1}")
            )
        }
        binding.rvList.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = mAdapter
            mAdapter.refreshData(dataList)
        }

        binding.btnChangeData.setOnClickListener {
            mAdapter.changeData(11,"https://img2.woyaogexing.com/2022/06/22/c3960cdd558fab73!400x400.jpg")
            mAdapter.getDataList().forEach {
                Log.e("集合数据: ","==》${it.resId}" )
            }
            val layoutManager = binding.rvList.layoutManager
            Log.e("isVisible: ","==>${layoutManager?.getChildAt(11)?.isVisible}" )
            if (layoutManager is StaggeredGridLayoutManager) {
                val firstVisible = layoutManager.findFirstVisibleItemPositions(null)
                val lastVisible = layoutManager.findLastVisibleItemPositions(null)
                val visiblePos = firstVisible.plus(lastVisible).distinct()
                val max = visiblePos.maxOrNull() ?: -1
                val min = visiblePos.minOrNull() ?: -1
                Log.e("当前可见最大值: ", "==》$max")
                Log.e("当前可见最小值: ", "==> $min")
//                if (pos >= min || pos <= max) {
//                    listAdapter.notifyItemChanged(pos)
//                }
            }
        }

    }
}