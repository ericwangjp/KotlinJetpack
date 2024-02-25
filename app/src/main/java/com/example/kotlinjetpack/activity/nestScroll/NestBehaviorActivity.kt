package com.example.kotlinjetpack.activity.nestScroll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.adapter.BehaviorPagerAdapter
import com.example.kotlinjetpack.databinding.ActivityNestBehaviorBinding

class NestBehaviorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNestBehaviorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestBehaviorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.viewPager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = BehaviorPagerAdapter(this@NestBehaviorActivity)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> binding.rbtnHome.isChecked = true
                        1 -> binding.rbtnCommunity.isChecked = true
                        2 -> binding.rbtnMine.isChecked = true
                    }
                }
            })
        }

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbtn_home -> binding.viewPager.setCurrentItem(0, true)
                R.id.rbtn_community -> binding.viewPager.setCurrentItem(1, true)
                R.id.rbtn_mine -> binding.viewPager.setCurrentItem(2, true)
                else -> binding.viewPager.setCurrentItem(0, false)
            }

        }
    }
}