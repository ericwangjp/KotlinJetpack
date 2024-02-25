package com.example.kotlinjetpack.activity.customview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.databinding.ActivityCustomViewOneBinding


class CustomViewOneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomViewOneBinding
    private val TAG = "CustomViewOneActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomViewOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {

    }


}
