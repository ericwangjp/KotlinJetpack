package com.example.kotlinjetpack.activity

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.adapter.MyFragmentPagerAdapter
import com.example.kotlinjetpack.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * material design tabLayout 详细使用
 * 参考链接：https://blog.csdn.net/yechaoa/article/details/122270969
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private val titles = arrayOf("Android", "Ios", "Kotlin", "Flutter", "Web")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        val newTab1 = binding.tabLayout1.newTab()
        newTab1.text = "hone"
        val newTab2 = binding.tabLayout1.newTab()
        newTab2.text = "bill"
        val newTab3 = binding.tabLayout1.newTab()
        newTab3.text = "search"
        val newTab4 = binding.tabLayout1.newTab()
        newTab4.text = "square"
        val newTab5 = binding.tabLayout1.newTab()
        newTab5.text = "financial"
        val newTab6 = binding.tabLayout1.newTab()
        newTab6.text = "my"
        binding.tabLayout1.addTab(newTab1)
        binding.tabLayout1.addTab(newTab2)
        binding.tabLayout1.addTab(newTab3)
        binding.tabLayout1.addTab(newTab4)
        binding.tabLayout1.addTab(newTab5)
        binding.tabLayout1.addTab(newTab6)
        binding.tabLayout1.getTabAt(0)?.setIcon(R.mipmap.ic_launcher)
        hideToolTipText(binding.tabLayout1.getTabAt(0))
        setDivider(binding.tabLayout1)
        setRedIcon(binding.tabLayout1)

//        绑定viewPager
//        binding.viewPager.adapter = MyFragmentPagerAdapter(supportFragmentManager)
//        binding.tabLayout1.setupWithViewPager(binding.viewPager)
        //        绑定viewPager2
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.adapter = MyFragmentPagerAdapter(this)
        TabLayoutMediator(
            binding.tabLayout1,
            binding.viewPager,
            true,
            true,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.text = titles[position]
            }).attach()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.d(TAG, "onPageScrollStateChanged: $state")
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                Log.d(TAG, "onPageScrolled: $position")
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "onPageSelected: $position")
            }
        })


//        BottomNavigationView 添加红点
        binding.bottomNavView.getOrCreateBadge(R.id.bottom_nav_mine).apply {
            backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.red)
            badgeTextColor = ContextCompat.getColor(this@MainActivity, R.color.white)
            number = 9999
            maxCharacterCount = 3
        }
    }

    /**
     * 设置红点
     */
    private fun setRedIcon(tabLayout: TabLayout) {
        tabLayout.apply {
            // 数字
            getTabAt(0)?.let { tab ->
                tab.orCreateBadge.apply {
                    backgroundColor = Color.RED
                    maxCharacterCount = 3
                    number = 99999
                    badgeTextColor = Color.WHITE
                }
            }

            // 红点
            getTabAt(1)?.let { tab ->
                tab.orCreateBadge.backgroundColor = Color.YELLOW
            }
        }

    }

    /**
     * 设置 分割线
     */
    private fun setDivider(tabLayout: TabLayout) {
        for (index in 0 until tabLayout.tabCount) {
            val linearLayout = tabLayout.getChildAt(index) as? LinearLayout
            linearLayout?.let {
                it.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                it.dividerDrawable = ContextCompat.getDrawable(this, R.drawable.shape_tab_divider)
                it.dividerPadding = 30
            }
        }

    }

    /**
     * 隐藏长按显示文本
     */
    private fun hideToolTipText(tab: TabLayout.Tab?) {
        // 取消长按事件
        tab?.let {
            it.view.isLongClickable = false
            // api 26 以上 设置空text
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                it.view.tooltipText = ""
            }
        }
    }

}