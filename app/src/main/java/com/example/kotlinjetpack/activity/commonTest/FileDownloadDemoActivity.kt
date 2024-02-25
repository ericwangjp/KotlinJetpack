package com.example.kotlinjetpack.activity.commonTest

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.databinding.ActivityFileDownloadDemoBinding

class FileDownloadDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFileDownloadDemoBinding

    //    private val downloadUrl = "http://fed.dev.hzmantu.com/mainto_app/app-official_product-release.apk"
    private val downloadUrl =
        "https://apke.mumayi.com/2022/06/28/130/1305582/sougoushurufa_V11.6.1_mumayi_df33d.apk"
    private var downloadId = 0L
    private var downloadedBytes = 0L
    private var totalBytes = 0L
    private var status = 0L
    private val downloadDirPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    ).absolutePath
    private val handlerThread by lazy {
        HandlerThread("handlerThread").apply {
            start()
        }
    }
    private val downloadManager: DownloadManager by lazy {
        getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    private val mHandler: Handler = object : Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                100 -> {
                    val progress = msg.arg1
                    Log.d("下载中，进度：", "-->: $progress")
                    runOnUiThread {
                        binding.tvDownloadProgress.text = "$progress%"
                        binding.progressDrawable.progress = progress
                    }
                }
                200 -> {
                    Log.d("下载完成：", "-->: ${Thread.currentThread().name}")
                }
            }
        }
    }
    private val downloadObserver: ContentObserver = object : ContentObserver(mHandler) {
        @SuppressLint("Range")
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            //此处可以通知handle去查询下载状态
            Log.e("ContentObserver", "onChange: $selfChange")
            Log.e("ContentObserver当前线程", "--》${Thread.currentThread().name}")
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                Log.e(
                    "失败原因",
                    "Reason: " + cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                );
                when (status) {
                    DownloadManager.STATUS_PAUSED -> {
                        Log.e("tag", "STATUS_PAUSED +" + DownloadManager.STATUS_PAUSED)
                    }
                    DownloadManager.STATUS_PENDING -> {
                        Log.e("tag", "STATUS_PENDING +" + DownloadManager.STATUS_PENDING)

                    }
                    DownloadManager.STATUS_RUNNING -> {
                        var bytesAndStatus =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        var totalSize =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        var dl_progress = ((bytesAndStatus * 100L) / totalSize)
                        Log.e("tag", "STATUS_RUNNING  progress = $dl_progress")
                        mHandler.sendMessage(Message.obtain().apply {
                            what = 100
                            arg1 = dl_progress.toInt()
                        })
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        var name = ""
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            var fileUriIdx =
                                cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            var fileUri = cursor.getString(fileUriIdx);
                            if (fileUri != null) {
                                name = Uri.parse(fileUri).path!!
                            }
                        } else {
                            name =
                                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME))
                        }
                        //下载完成
                        cursor.close()
                        Log.e(
                            "STATUS_SUCCESSFUL ", "id= $downloadId , 文件名：$name"
                        )
                        mHandler.sendEmptyMessage(200)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        Log.e("tag", "STATUS_FAILED " + DownloadManager.STATUS_FAILED)
                        cursor.close()
                    }

                }
            }
        }
    }

    private var downloadCompleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.e("下载成功", "onReceive:${completeDownloadId} ")
            val action = intent.action
            Log.e("action 为", "onReceive: ${DownloadManager.ACTION_DOWNLOAD_COMPLETE == action}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileDownloadDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    override fun onResume() {
        super.onResume()
        //注册ContentObserver
        contentResolver.registerContentObserver(
            Uri.parse("content://downloads/my_downloads"), true, downloadObserver
        )

        /** 注册下载完成接收广播  */
        registerReceiver(
            downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onPause() {
        super.onPause()
        contentResolver.unregisterContentObserver(downloadObserver)
        unregisterReceiver(downloadCompleteReceiver)
    }

    private fun initData() {
        // 开始下载
        binding.btnStartDownload.setOnClickListener {

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "无法操作SD卡", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // TODO: SD卡读写权限检查
            //  android.permission.WRITE_EXTERNAL_STORAGE

            // downloadUrl为下载地址
            val request = DownloadManager.Request(Uri.parse(downloadUrl))
            // 设置文件下载目录和文件名
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, "test.apk"
            )
            //设置下载中通知栏提示的标题
            request.setTitle("下载更新中")
            //设置下载中通知栏提示的介绍
            request.setDescription("测试下载中...")

            /*
            表示下载进行中和下载完成的通知栏是否显示，默认只显示下载中通知，
            VISIBILITY_HIDDEN表示不显示任何通知栏提示，
            这个需要在AndroidMainfest中添加权限android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
            */
            //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            /*
            表示下载允许的网络类型，默认在任何网络下都允许下载，
            有NETWORK_MOBILE、NETWORK_WIFI、NETWORK_BLUETOOTH三种及其组合可供选择；
            如果只允许wifi下载，而当前网络为3g，则下载会等待
            */
            //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

            //移动网络情况下是否允许漫游
            //request.setAllowedOverRoaming(true);

            //表示允许MediaScanner扫描到这个文件，默认不允许
            //request.allowScanningByMediaScanner();

            /*
            设置下载文件的mineType,
            因为下载管理Ui中点击某个已下载完成文件及下载完成点击通知栏提示都会根据mimeType去打开文件，
            所以我们可以利用这个属性。比如上面设置了mimeType为application/package.name，
            我们可以同时设置某个Activity的intent-filter为application/package.name，用于响应点击的打开文件
            */
            //request.setMimeType("application/package.name");

            //添加请求下载的网络链接的http头，比如User-Agent，gzip压缩等
            //request.addRequestHeader(String header, String value);

            /*
             * 调用downloadManager的enqueue接口进行下载，
             * 返回唯一的downloadId用于下载状态查询和下载完成监听
             */
            downloadId = downloadManager.enqueue(request)

            // 查询下载信息
            //setFilterById根据下载id进行过滤
//            val query = DownloadManager.Query().setFilterById(downloadId)
//            var cursor: Cursor? = null
//            try {
//                cursor = downloadManager.query(query)
//                if (cursor != null && cursor.moveToFirst()) {
//                    downloadedBytes =
//                        cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                    totalBytes =
//                        cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                    status = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//                }
//            } finally {
//                cursor?.close()
//            }

            // 进度回调，方式一；注册ContentObserver，监听下载状态，每次变化都会触发onChange
            // 方法二：通过ScheduledExecutorService轮询

        }
        // 进度条示例
        var a = 0
        val handler: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)

                Log.e("handleMessage: ", "a= ${a}")
                binding.progressbar.progress = a
                if (a < 100) {
                    a += 2
                    sendEmptyMessageDelayed(100, 200)
                }
            }
        }

        binding.btnStart.setOnClickListener {
            handler.sendEmptyMessageDelayed(100, 0)
        }



        binding.btnStartProgressAnim.setOnClickListener {
            binding.customProgress.setValue("80", 100f)
        }
    }
}