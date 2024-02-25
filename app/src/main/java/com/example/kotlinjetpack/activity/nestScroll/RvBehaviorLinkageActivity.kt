package com.example.kotlinjetpack.activity.nestScroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.kotlinjetpack.adapter.BehaviorRCVAdapter
import com.example.kotlinjetpack.behavior.RvTitleBarLinkageBehavior
import com.example.kotlinjetpack.databinding.ActivityRvBehaviorLinkageBinding


class RvBehaviorLinkageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRvBehaviorLinkageBinding
    private val TAG = "RvBehaviorLinkageActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRvBehaviorLinkageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        val listData: ArrayList<String> = arrayListOf()
        for (i in 1..15) {
            listData.add("第$i")
        }
        binding.rvList.apply {
//            layoutManager = LinearLayoutManager(this@RvBehaviorLinkageActivity)
            adapter = BehaviorRCVAdapter(listData)
            translationY = RvTitleBarLinkageBehavior.MAX_PEEK
        }

        val behavior = RvTitleBarLinkageBehavior(this, null).apply {
            // 这里就是TitleBar和header的互动
            val threshold = 0.7f
            listener = { percent, title ->
                // 这个监听header的移动程度，通过percent表示，上推过程中percent逐渐变大到1，下滑最小到固定时为0

                // 这里就是TitleBar中何时显示文字了，这里的阈值判断是header移动到70%
                if (percent >= threshold && binding.tvTitleBar.text.isEmpty()) {
                    binding.tvTitleBar.text = title
                } else if (percent < threshold && binding.tvTitleBar.text.isNotEmpty()) {
                    binding.tvTitleBar.text = ""
                }
                // 这是透明度协调
                binding.tvTitleBar.alpha = percent

            }
        }
        (binding.llayoutStatusMsg.layoutParams as CoordinatorLayout.LayoutParams).behavior =
            behavior
    }


}
