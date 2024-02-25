package com.example.kotlinjetpack.activity.commonTest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.adapter.UploadPicAdapter
import com.example.kotlinjetpack.databinding.ActivityDragViewBinding
import com.example.kotlinjetpack.model.PicInfo
import com.example.kotlinjetpack.utils.DragHelper
import com.example.kotlinjetpack.view.GridItemDecoration

class DragViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDragViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        val datas = arrayListOf<PicInfo>()
        for (i in 0 until 6) {
            datas.add(PicInfo(R.drawable.icon_pic_default, "--> ${i + 1}"))
        }
        binding.rvList.apply {
            layoutManager = GridLayoutManager(this@DragViewActivity, 3)
//            layoutManager = LinearLayoutManager(this@DragViewActivity)
            adapter = UploadPicAdapter(datas)
            ItemTouchHelper(DragHelper(object : DragHelper.OnDragListener {
                override fun onStartDrag() {
                    binding.tvDelete.visibility = View.VISIBLE
                }

                override fun onStopDrag() {
                    binding.tvDelete.visibility = View.GONE
                    Log.e("onStopDrag: ", "当前集合数据：${datas.forEach { println(it.desc) }}")
                }

                override fun onDragAreaChange(isInDeleteArea: Boolean) {
                    Log.e("onDragAreaChange: ", "是否在删除区域：$isInDeleteArea")
                }

            }, binding.tvDelete, adapter!! as UploadPicAdapter)).attachToRecyclerView(this)
//            val dividerItemDecoration = DividerItemDecoration(this@DragViewActivity,DividerItemDecoration.VERTICAL)
//            dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.deivider_vertical_gradient))
//            addItemDecoration(dividerItemDecoration)
            addItemDecoration(GridItemDecoration())
//            addItemDecoration(ProcessItemDecoration())
        }
    }
}