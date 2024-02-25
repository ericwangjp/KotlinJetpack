package com.example.kotlinjetpack.activity.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlinjetpack.databinding.ActivityCustomSpiderViewBinding
import com.example.kotlinjetpack.databinding.ActivityMotionLayoutBinding

class CustomSpiderViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomSpiderViewBinding
    private val listData by lazy {
        arrayListOf("数据1", "数据2", "数据3", "数据4", "数据5", "数据6", "数据7", "数据8")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomSpiderViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {

    }
}