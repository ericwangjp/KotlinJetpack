package com.example.kotlinjetpack.utils

import android.content.Context
import android.text.TextUtils
import java.io.File
import java.text.DecimalFormat
import java.util.*


/**
 * desc: ImageFileUtils
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/7 7:52 下午
 */
object ImageFileUtils {

    /**
     * 本地与我们应用程序相关文件存放的根目录
     */
    private const val ROOT_DIR_PATH = "/fresco-helper"

    /**
     * 下载图片文件存放的目录
     */
    private const val IMAGE_DOWNLOAD_IMAGES_PATH = "/Download/Images/"

    /**
     * 获取下载图片文件存放的目录
     *
     * @param context Context
     * @return SDCard卡或者手机内存的根路径
     */
    fun getImageDownloadDir(context: Context): String {
        return getRootDir(context) + IMAGE_DOWNLOAD_IMAGES_PATH
    }

    /**
     * 获取外部存储路径
     *
     * @param context Context
     * @return SDCard卡或者手机内存的根路径
     */
    fun getRootDir(context: Context): String {
        return context.applicationContext.getFilesDir().getPath().toString() + ROOT_DIR_PATH
    }

    /**
     * 随机生成一个文件，用于存放下载的图片
     *
     * @param context Context
     * @return
     */
    fun getImageDownloadPath(context: Context): String {
        val imageRootDir = getImageDownloadDir(context)
        val dir = File(imageRootDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val fileName: String = UUID.randomUUID().toString() + ".jpg"
        return dir.absolutePath + File.separator.toString() + fileName
    }

    /**
     * 获取下载文件在本地存放的路径
     *
     * @param context
     * @param url
     * @return
     */
    fun getImageDownloadPath(context: Context, url: String): String {
        if (url.startsWith("/")) {
            return url
        }
        val fileName = getFileName(url)
        val imageRootDir = getImageDownloadDir(context)
        val dir = File(imageRootDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir.absolutePath + File.separator.toString() + fileName
    }

    /**
     * 根据url获取文件名
     *
     * @param url
     * @return
     */
    fun getFileName(url: String): String {
        //        MLog.i("fileName = " + fileName);
        return url.substring(url.lastIndexOf(File.separator) + 1)
    }

    /**
     * 检查本地是否存在某个文件
     *
     * @param filePath
     * @return
     */
    fun exists(filePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath)) {
            return false
        }
        val file = File(filePath)
        return file.exists() && file.isFile()
    }

    fun getFileSize(fileS: Long): String? {
        val df = DecimalFormat("#0.0")
        val fileSizeString: String = if (fileS < 1073741824) {
            df.format(fileS.toDouble() / 1048576).toString() + "MB"
        } else {
            df.format(fileS.toDouble() / 1073741824).toString() + "GB"
        }
        return fileSizeString
    }
}