package com.example.kotlinjetpack.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.activity.commonTest.DragViewActivity
import com.example.kotlinjetpack.activity.commonTest.FlexBoxLayoutActivity
import com.example.kotlinjetpack.activity.commonTest.MotionLayoutActivity
import com.example.kotlinjetpack.activity.commonTest.PicSelectActivity
import com.example.kotlinjetpack.activity.commonTest.ResultCallbackActivity
import com.example.kotlinjetpack.activity.commonTest.WaterfallFlowActivity
import com.example.kotlinjetpack.activity.commonTest.WebViewActivity
import com.example.kotlinjetpack.activity.linkageScroll.ScrollTabBarActivity
import com.example.kotlinjetpack.databinding.ActivityCommonTestListTwoBinding


class CommonTestListTwoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCommonTestListTwoBinding
    private val TAG = "CommonOneActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonTestListTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }

    private fun initData() {
        binding.apply {
            btnScrollTabBar.setOnClickListener(this@CommonTestListTwoActivity)
            btnWebView.setOnClickListener(this@CommonTestListTwoActivity)
            btnDragView.setOnClickListener(this@CommonTestListTwoActivity)
            btnFlexBoxLayout.setOnClickListener(this@CommonTestListTwoActivity)
            btnWaterfallFlow.setOnClickListener(this@CommonTestListTwoActivity)
            btnTakePic.setOnClickListener(this@CommonTestListTwoActivity)
            btnMotionLayout.setOnClickListener(this@CommonTestListTwoActivity)
            btnActivityCallBack.setOnClickListener(this@CommonTestListTwoActivity)
        }
    }

    override fun onClick(view: View?) {
        startActivity(
            Intent(
                this, when (view) {
                    binding.btnScrollTabBar -> ScrollTabBarActivity::class.java
                    binding.btnWebView -> WebViewActivity::class.java
                    binding.btnDragView -> DragViewActivity::class.java
                    binding.btnFlexBoxLayout -> FlexBoxLayoutActivity::class.java
                    binding.btnWaterfallFlow -> WaterfallFlowActivity::class.java
                    binding.btnTakePic -> PicSelectActivity::class.java
                    binding.btnMotionLayout -> MotionLayoutActivity::class.java
                    binding.btnActivityCallBack -> ResultCallbackActivity::class.java
                    else -> ResultCallbackActivity::class.java
                }
            ).apply {
//                val dataList = Gson().fromJson(productCategoryJsonString, SortBean::class.java)
//                val bundle = Bundle()
//                bundle.putSerializable(IIntent.DATA_TAG, dataList)
//                putExtras(bundle)
            }
        )
    }

}