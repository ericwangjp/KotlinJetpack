package com.example.kotlinjetpack.utils.pic

import android.content.Context
import com.example.kotlinjetpack.utils.ImageFileUtils

/**
 * desc: 图片下载结果监听
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/7 7:51 下午
 */
abstract class IDownloadResult : IResult<String?> {
    private var mFilePath: String

    constructor(filePath: String) {
        mFilePath = filePath
    }

    constructor(context: Context) {
        mFilePath = ImageFileUtils.getImageDownloadPath(context)
    }

    fun getFilePath(): String {
        return mFilePath
    }

    fun onProgress(progress: Int) {}

    abstract override fun onResult(result: String?)
}