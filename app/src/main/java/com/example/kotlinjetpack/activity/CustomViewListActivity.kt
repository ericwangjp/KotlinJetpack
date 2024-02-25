package com.example.kotlinjetpack.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.activity.commonTest.ResultCallbackActivity
import com.example.kotlinjetpack.activity.customview.CustomViewOneActivity
import com.example.kotlinjetpack.databinding.ActivityCustomViewListBinding


class CustomViewListActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityCustomViewListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomViewListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.apply {
            btnCustomViewOne.setOnClickListener(this@CustomViewListActivity)
        }
    }

    override fun onClick(view: View?) {
        startActivity(
            Intent(
                this, when (view) {
                    binding.btnCustomViewOne -> CustomViewOneActivity::class.java
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