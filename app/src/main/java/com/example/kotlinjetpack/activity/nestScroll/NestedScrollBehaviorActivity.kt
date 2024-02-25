package com.example.kotlinjetpack.activity.nestScroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.databinding.ActivityNestedScrollBehaviorBinding


class NestedScrollBehaviorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNestedScrollBehaviorBinding
    private val TAG = "NestedScrollBehaviorActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedScrollBehaviorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {

    }


}
