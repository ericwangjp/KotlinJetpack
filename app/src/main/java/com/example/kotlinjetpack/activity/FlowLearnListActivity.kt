package com.example.kotlinjetpack.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.activity.commonTest.ResultCallbackActivity
import com.example.kotlinjetpack.activity.flowCoroutine.FlowLearnActivity
import com.example.kotlinjetpack.databinding.ActivityFlowLearnListBinding


class FlowLearnListActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityFlowLearnListBinding
    private val TAG = "FlowLearnListActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowLearnListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.apply {
            btnFlowLearn.setOnClickListener(this@FlowLearnListActivity)
        }
    }

    override fun onClick(view: View?) {
        startActivity(
            Intent(
                this, when (view) {
                    binding.btnFlowLearn -> FlowLearnActivity::class.java
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
