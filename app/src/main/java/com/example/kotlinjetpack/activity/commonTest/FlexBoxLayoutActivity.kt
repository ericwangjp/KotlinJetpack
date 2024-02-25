package com.example.kotlinjetpack.activity.commonTest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.adapter.CommonItemAdapter
import com.example.kotlinjetpack.databinding.ActivityFlexBoxLayoutBinding
import com.google.android.flexbox.*
import kotlin.random.Random

class FlexBoxLayoutActivity : AppCompatActivity() {
    lateinit var binding: ActivityFlexBoxLayoutBinding
    private var dataLists = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlexBoxLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        for (i in 0..30) {
            dataLists.add("-> ${i * Random.nextLong(10, 10000)}")
        }
        val flexboxLayoutManager =
            FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP).apply {
                alignItems = AlignItems.CENTER
                justifyContent = JustifyContent.FLEX_START
            }
        binding.rvList.apply {
            layoutManager = flexboxLayoutManager
//            addItemDecoration(DividerItemDecoration(this@FlexBoxLayoutActivity,DividerItemDecoration.VERTICAL))
            adapter = CommonItemAdapter(dataLists)
        }

    }
}