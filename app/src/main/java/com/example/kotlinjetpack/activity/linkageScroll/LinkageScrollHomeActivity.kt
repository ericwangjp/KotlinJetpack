package com.example.kotlinjetpack.activity.linkageScroll

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.constants.productCategoryJsonString
import com.example.kotlinjetpack.databinding.ActivityLinkageScrollHomeBinding
import com.feiyu.rv.GangedRvActivity
import com.feiyu.rv.IIntent
import com.feiyu.rv.SortBean
import com.google.gson.Gson


class LinkageScrollHomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLinkageScrollHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinkageScrollHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnLinkageList.setOnClickListener(this)
        binding.btnLinkageListDemo.setOnClickListener(this)
        binding.btnLinkageList2.setOnClickListener(this)
        binding.btnLinkageList3.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        startActivity(
            Intent(
                this, when (view) {
                    binding.btnLinkageList -> LinkageListActivity::class.java
                    binding.btnLinkageListDemo -> GangedRvActivity::class.java
                    binding.btnLinkageList2 -> ScrollLinkageListActivity::class.java
                    binding.btnLinkageList3 -> ScrollLinkageList2Activity::class.java
                    else -> LinkageListActivity::class.java
                }
            ).apply {
                val dataList = Gson().fromJson(productCategoryJsonString, SortBean::class.java)
                val bundle = Bundle()
                bundle.putSerializable(IIntent.DATA_TAG, dataList)
                putExtras(bundle)
            }
        )
    }


}
