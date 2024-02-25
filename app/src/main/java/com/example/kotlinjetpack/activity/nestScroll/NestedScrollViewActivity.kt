package com.example.kotlinjetpack.activity.nestScroll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinjetpack.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import java.lang.RuntimeException

/**
 * head 嵌套滚动
 */
class NestedScrollViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nested_scroll_view)
        initData()
    }

    private fun initData() {
    }
}