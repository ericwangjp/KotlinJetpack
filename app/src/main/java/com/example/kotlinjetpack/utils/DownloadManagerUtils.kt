package com.example.kotlinjetpack.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.kotlinjetpack.App
import java.io.File
import java.util.*

/**
 * desc: DownloadManagerUtils
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/7/2 6:01 下午
 */
object DownloadManagerUtils {

    private val downloadManager: DownloadManager by lazy {
        App.application?.applicationContext?.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    }
    private var id = 0L
    private var url = ""
    private var mHandler = Handler(Looper.getMainLooper())
    private val timer: Timer by lazy {
        Timer()
    }


    private fun notifyListener(state: Int, progress: Int) {
        Log.e("当前线程：", "--》: ${Thread.currentThread().name}")
        Log.e("当前下载状态：$state", "当前下载进度: $progress")
//        mHandler.post {
//            var iterator = listenerList.iterator()
//            while (iterator.hasNext()) {
//                var listener = iterator.next()
//                listener.call(url, state, progress)
//            }
//        }

    }

    fun downLoad(title: String, description: String, downLoadHtmlUrl: String) {
        this.url = downLoadHtmlUrl
        var uri = Uri.parse(downLoadHtmlUrl)
        val request = DownloadManager.Request(uri)
        //设置漫游条件下是否可以下载
        request.setAllowedOverRoaming(false)
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        // 设置文件下载目录和文件名
        // request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "test.apk")
        //设置通知标题
        request.setTitle(title)
        //设置通知标题message
        request.setDescription(description)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()
        val index = downLoadHtmlUrl.lastIndexOf("/")
        val apkName: String = downLoadHtmlUrl.substring(index + 1, downLoadHtmlUrl.length)
        //设置文件存放路径
        val directory = getDiskCacheDir(App.application?.applicationContext!!)
        Log.e("文件下载地址：", "==>: $directory", )
        val file = File(directory, "/download/$apkName")
        request.setDestinationUri(Uri.fromFile(file))
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        this.id = downloadManager.enqueue(request)
        Log.e("下载id", "==》: $id", )
        startTimer()
    }


    private fun startTimer() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                checkStatus(id)
            }
        }, 1000, 1000)
    }

    private fun getDiskCacheDir(context: Context): String? {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            context.externalCacheDir!!.path
        } else {
            context.cacheDir.path
        }

    }

    @SuppressLint("Range")
    private fun checkStatus(id: Long) {
        val query = DownloadManager.Query()

        //通过下载的id查找
        query.setFilterById(id)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            Log.e("失败原因", "Reason: " + cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)));
            when (status) {
                DownloadManager.STATUS_PAUSED -> {
                    Log.e("tag", "STATUS_PAUSED +" + DownloadManager.STATUS_PAUSED)
                }
                DownloadManager.STATUS_PENDING -> {
                    Log.e("tag", "STATUS_PENDING +" + DownloadManager.STATUS_PENDING)
//                    checkStatus(id)

//                    notifyListener(id, DownloadManager.STATUS_PENDING, 0)

                }
                DownloadManager.STATUS_RUNNING -> {
                    var bytesAndStatus =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    var totalSize =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    var dl_progress = ((bytesAndStatus * 100L) / totalSize)
                    notifyListener(DownloadManager.STATUS_RUNNING, dl_progress.toInt())
                    Log.e("tag", "STATUS_RUNNING  bytesAndStatus = $dl_progress")
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    notifyListener(DownloadManager.STATUS_SUCCESSFUL, 100)
                    var name = ""
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        var fileUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        var fileUri = cursor.getString(fileUriIdx);
                        if (fileUri != null) {
                            name = Uri.parse(fileUri).getPath()!!
                        }
                    } else {
                        name =
                            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME))
                    }
                    timer.cancel()
                    //下载完成
                    cursor.close()
                    Log.e(
                        "tag",
                        "STATUS_SUCCESSFUL +" + DownloadManager.STATUS_SUCCESSFUL + ",id=" + id
                    )
                    installApk(File(name))
                }
                DownloadManager.STATUS_FAILED -> {
                    Log.e("tag", "STATUS_FAILED " + DownloadManager.STATUS_FAILED)
                    cursor.close()
                }

            }
        }
    }

    private fun installApk(file: File) {
        Log.e("下载完成，开始安装", "installApk: ${file.absolutePath}", )

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true) //表明不是未知来源
//            val uri = FileProvider.getUriForFile(
//                App.context!!,
//                App.context!!.packageName + ".fileProvider",
//                file
//            )
//            intent.setDataAndType(uri, "application/vnd.android.package-archive")
//            App.context!!.startActivity(intent)
//
//        } else {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
//            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true) //表明不是未知来源
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            App.context!!.startActivity(intent)
//        }
    }

}
