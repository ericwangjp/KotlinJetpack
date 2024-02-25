package com.example.kotlinjetpack.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.activity.commonTest.CameraDemoActivity
import com.example.kotlinjetpack.activity.commonTest.DependencyViewActivity
import com.example.kotlinjetpack.activity.commonTest.DianZanActivity
import com.example.kotlinjetpack.activity.commonTest.FileDownloadDemoActivity
import com.example.kotlinjetpack.activity.commonTest.GoogleScanCodeActivity
import com.example.kotlinjetpack.activity.commonTest.LocationDemoActivity
import com.example.kotlinjetpack.activity.commonTest.NotificationDemoActivity
import com.example.kotlinjetpack.activity.commonTest.ResultCallbackActivity
import com.example.kotlinjetpack.activity.commonTest.ShapeImageActivity
import com.example.kotlinjetpack.activity.commonTest.TagTextViewActivity
import com.example.kotlinjetpack.activity.commonTest.TextAnimActivity
import com.example.kotlinjetpack.activity.customview.CustomSpiderViewActivity
import com.example.kotlinjetpack.activity.linkageScroll.LinkageScrollHomeActivity
import com.example.kotlinjetpack.activity.linkageScroll.NestListActivity
import com.example.kotlinjetpack.activity.linkageScroll.ScrollFoldAnimActivity
import com.example.kotlinjetpack.constants.productCategoryJsonString
import com.example.kotlinjetpack.databinding.ActivityCommonTestListOneBinding
import com.feiyu.rv.IIntent
import com.feiyu.rv.SortBean
import com.google.gson.Gson


class CommonTestListOneActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityCommonTestListOneBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonTestListOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }


    private fun initData() {
        binding.btnFileDownload.setOnClickListener(this)
        binding.btnSysLocation.setOnClickListener(this)
        binding.btnTextAnim.setOnClickListener(this)
        binding.btnCustomSpiderView.setOnClickListener(this)
        binding.btnNotification.setOnClickListener(this)
        binding.btnDependencyView.setOnClickListener(this)
        binding.btnShapeImageView.setOnClickListener(this)
        binding.btnLinkageHome.setOnClickListener(this)
        binding.btnFoldAnim.setOnClickListener(this)
        binding.btnGroupList.setOnClickListener(this)
        binding.btnTag.setOnClickListener(this)
        binding.btnLike.setOnClickListener(this)
        binding.btnCameraDemo.setOnClickListener(this)
        binding.btnGoogleScanner.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        startActivity(
            Intent(
                this, when (view) {
                    binding.btnFileDownload -> FileDownloadDemoActivity::class.java
                    binding.btnSysLocation -> LocationDemoActivity::class.java
                    binding.btnTextAnim -> TextAnimActivity::class.java
                    binding.btnCustomSpiderView -> CustomSpiderViewActivity::class.java
                    binding.btnNotification -> NotificationDemoActivity::class.java
                    binding.btnDependencyView -> DependencyViewActivity::class.java
                    binding.btnShapeImageView -> ShapeImageActivity::class.java
                    binding.btnLinkageHome -> LinkageScrollHomeActivity::class.java
                    binding.btnFoldAnim -> ScrollFoldAnimActivity::class.java
                    binding.btnGroupList -> NestListActivity::class.java
                    binding.btnTag -> TagTextViewActivity::class.java
                    binding.btnLike -> DianZanActivity::class.java
                    binding.btnCameraDemo -> CameraDemoActivity::class.java
                    binding.btnGoogleScanner -> GoogleScanCodeActivity::class.java
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