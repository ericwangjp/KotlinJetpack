package com.example.kotlinjetpack.activity.commonTest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.databinding.ActivityDependencyViewBinding

class DependencyViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDependencyViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDependencyViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {

    }
}