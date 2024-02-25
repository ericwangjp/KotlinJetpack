package com.example.kotlinjetpack.activity.nestScroll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.kotlinjetpack.databinding.ActivityBehaviorBinding

class BehaviorActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityBehaviorBinding
    private val TAG = "BehaviorActivity"
    private val views = arrayListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBehaviorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnAdd.setOnClickListener(this)
        binding.btnRemove.setOnClickListener(this)
        binding.btnEasy.setOnClickListener(this)
        binding.btnHard.setOnClickListener(this)
        binding.root.viewTreeObserver.addOnPreDrawListener {
            Log.e(TAG, "addOnPreDrawListener: 要开始绘制了")
            return@addOnPreDrawListener true
        }

        binding.root.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(p0: View?, p1: View?) {
                Log.e(TAG, "setOnHierarchyChangeListener: 添加了")
            }

            override fun onChildViewRemoved(p0: View?, p1: View?) {
                Log.e(TAG, "setOnHierarchyChangeListener: 移除了")
            }
        })
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnAdd -> {
                val textView = TextView(this)
                textView.text = "child view ${views.size}"
                textView.setPadding(20)
                views.add(textView)
                binding.root.addView(textView)
            }
            binding.btnRemove -> {
                Log.e(TAG, "views size: ${views.size}")
                views.lastOrNull()?.let {
                    binding.root.removeView(it)
                    views.remove(it)
                }

            }
            binding.btnEasy -> {

            }
            binding.btnHard -> {
                startActivity(Intent(this, NestBehaviorActivity::class.java))
            }
        }
    }

    override fun onStop() {
        super.onStop()
//        binding.root.viewTreeObserver.removeOnPreDrawListener(this)
    }
}