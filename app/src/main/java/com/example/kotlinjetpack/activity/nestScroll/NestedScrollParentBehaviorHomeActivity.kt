package com.example.kotlinjetpack.activity.nestScroll

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.activity.commonTest.ResultCallbackActivity
import com.example.kotlinjetpack.activity.linkageScroll.CollapseExpandHeadActivity
import com.example.kotlinjetpack.constants.productCategoryJsonString
import com.example.kotlinjetpack.databinding.ActivityNestedScrollParentBehaviorHomeBinding
import com.feiyu.rv.IIntent
import com.feiyu.rv.SortBean
import com.google.gson.Gson


class NestedScrollParentBehaviorHomeActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityNestedScrollParentBehaviorHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedScrollParentBehaviorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }


    private fun initData() {
        binding.btnScrollParentChild.setOnClickListener(this)
        binding.btnCustomBehavior.setOnClickListener(this)
        binding.btnCustomNestedParent.setOnClickListener(this)
        binding.btnCustomNestedParent2.setOnClickListener(this)
        binding.btnNestScrollDemo.setOnClickListener(this)
        binding.btnScrollHead.setOnClickListener(this)
        binding.btnNestScrollBehavior.setOnClickListener(this)
        binding.btnLinkageRvBehavior.setOnClickListener(this)
//        binding.btnLinkageListDemo.setOnClickListener(this)
//        binding.btnLinkageList2.setOnClickListener(this)
//        binding.btnLinkageList3.setOnClickListener(this)
//        binding.btnLinkageList4.setOnClickListener(this)
//        binding.btnFoldAnim.setOnClickListener(this)
//        binding.btnGroupList.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        startActivity(
            Intent(
                this, when (view) {
                    binding.btnScrollParentChild -> NestedScrollViewActivity::class.java
                    binding.btnCustomBehavior -> BehaviorActivity::class.java
                    binding.btnCustomNestedParent -> NestedScrollParentActivity::class.java
                    binding.btnCustomNestedParent2 -> NestedScrollParent2Activity::class.java
                    binding.btnNestScrollDemo -> NestedScrollParent3Activity::class.java
                    binding.btnScrollHead -> CollapseExpandHeadActivity::class.java
                    binding.btnNestScrollBehavior -> NestedScrollBehaviorActivity::class.java
                    binding.btnLinkageRvBehavior -> RvBehaviorLinkageActivity::class.java
//                    binding.btnLinkageListDemo -> GangedRvActivity::class.java
//                    binding.btnLinkageList2 -> ScrollLinkageListActivity::class.java
//                    binding.btnLinkageList3 -> ScrollLinkageList2Activity::class.java
//                    binding.btnLinkageList4 -> ProductListActivity::class.java
//                    binding.btnFoldAnim -> ScrollFoldAnimActivity::class.java
//                    binding.btnGroupList -> NestListActivity::class.java
                    else -> ResultCallbackActivity::class.java
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