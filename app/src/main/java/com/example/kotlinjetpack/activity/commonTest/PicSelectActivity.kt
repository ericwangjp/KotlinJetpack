package com.example.kotlinjetpack.activity.commonTest

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlinjetpack.adapter.FolderListAdapter
import com.example.kotlinjetpack.adapter.PicListAdapter
import com.example.kotlinjetpack.databinding.ActivityPicSelectBinding
import com.example.kotlinjetpack.utils.pic.FrescoEngine
import com.example.kotlinjetpack.utils.pic.FrescoImageLoader
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.permissionx.guolindev.PermissionX


class PicSelectActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "PicSelectActivity==>"
    private lateinit var binding: ActivityPicSelectBinding
    private val folderListAdapter by lazy {
        FolderListAdapter(mutableListOf())
    }
    private val picListAdapter by lazy {
        PicListAdapter(mutableListOf())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPicSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnGetPic.setOnClickListener(this)
        binding.btnSystemPic.setOnClickListener(this)
        binding.btnTakePic.setOnClickListener(this)
        binding.btnGetFolderList.setOnClickListener(this)
        binding.btnGetAlbumList.setOnClickListener(this)
        binding.btnGetSelectAlbum.setOnClickListener(this)

        binding.rvAlbumList.apply {
            layoutManager = GridLayoutManager(this@PicSelectActivity, 3)
            adapter = picListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@PicSelectActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnGetPic -> {
                checkPermission {
                    PictureSelector.create(this)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(FrescoEngine.instance)
                        .forResult(object : OnResultCallbackListener<LocalMedia?> {
                            override fun onResult(result: ArrayList<LocalMedia?>?) {
                                result?.let {
                                    Log.e(TAG, "onResult: ${result.size}")
                                    FrescoImageLoader.loadFile(
                                        binding.sdvAlbumOne,
                                        result[0]?.path
                                    )
                                }
                            }

                            override fun onCancel() {

                            }
                        })
                }
            }
            binding.btnSystemPic -> {
                checkPermission {
                    PictureSelector.create(this)
                        .openSystemGallery(SelectMimeType.ofImage())
                        .forSystemResult(object : OnResultCallbackListener<LocalMedia?> {
                            override fun onResult(result: ArrayList<LocalMedia?>?) {
                                result?.let {
                                    FrescoImageLoader.loadFile(binding.sdvImgTwo, result[0]?.path)
                                }
                            }

                            override fun onCancel() {}
                        })
                }
            }
            binding.btnTakePic -> {
                checkPermission {
                    PictureSelector.create(this)
                        .openCamera(SelectMimeType.ofImage())
                        .forResult(object : OnResultCallbackListener<LocalMedia?> {
                            override fun onResult(result: ArrayList<LocalMedia?>?) {
                                result?.let {
                                    FrescoImageLoader.loadFile(
                                        binding.sdvImgThree,
                                        result[0]?.path
                                    )
                                }
                            }

                            override fun onCancel() {}
                        })
                }
            }
            binding.btnGetFolderList -> {
                checkPermission {
                    PictureSelector.create(this)
                        .dataSource(SelectMimeType.ofAll())
                        .obtainAlbumData { result ->
                            Log.e(TAG, "onClick:${result.size} ")
                            folderListAdapter.refreshData(result)
                        }
                }
            }
//            相册列表
            binding.btnGetAlbumList -> {
                checkPermission {
                    PictureSelector.create(this)
                        .dataSource(SelectMimeType.ofAll())
                        .obtainMediaData { result ->
                            Log.e(TAG, "onClick:${result.size} ")
                            picListAdapter.refreshData(result)
                        }
                }
            }

            binding.btnGetSelectAlbum -> {
                checkPermission {
                    val loader = PictureSelector.create(this)
                        .dataSource(SelectMimeType.ofAll()).buildMediaLoader()
                    loader.loadAllAlbum { result ->
                            Log.e(TAG, "onClick：${result.size} ")
                            Log.e(TAG, "onClick：${result[0].folderTotalNum} ")
                            Log.e(TAG, "onClick：${result[0].isHasMore} ")
                            Log.e(TAG, "onClick：${result[0].currentDataPage} ")
                            Log.e(TAG, "onClick：${result[0].bucketId} ")
                            Log.e(TAG, "onClick：${result[0].folderName} ")
                        }
                }
            }
        }
    }

    private fun checkPermission(block: () -> Unit) {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "没有权限无法选择照片",
                    "确定",
                    "取消"
                )
            }.onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "需要您手动在设置中打开对应的权限",
                    "确定",
                    "取消"
                )
            }.request { allGranted, grantedList, deniedList ->
                if (allGranted) {
//                Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                    block.invoke()
                } else {
                    Toast.makeText(
                        this,
                        "These permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
    }
}