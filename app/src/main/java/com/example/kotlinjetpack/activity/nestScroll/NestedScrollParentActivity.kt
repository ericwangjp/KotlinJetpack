package com.example.kotlinjetpack.activity.nestScroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinjetpack.adapter.BehaviorRCVAdapter
import com.example.kotlinjetpack.databinding.ActivityNestedScrollParentBinding

class NestedScrollParentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNestedScrollParentBinding
    private var dataLists = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedScrollParentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        for (i in 0..30) {
            dataLists.add("---> $i <--")
        }
        binding.rvList.let {
            it.layoutManager = LinearLayoutManager(this)
            it.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            it.adapter = BehaviorRCVAdapter(dataLists)
        }
    }
}