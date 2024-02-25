package com.example.kotlinjetpack.activity.commonTest

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kotlinjetpack.databinding.ActivityWebViewBinding


class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    private val TAG = "WebViewActivity"
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // 处理结果数据
                val fileResult = arrayOf<Uri>()
                data?.data?.let {
                    fileResult.plus(it)
                }
                mFilePathCallback?.onReceiveValue(fileResult)
                mFilePathCallback = null
            } else {
                mFilePathCallback?.onReceiveValue(arrayOf())
                mFilePathCallback = null
            }
        }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 权限被授予
                Log.e(TAG, "=》 权限被授予: ")
            } else {
                Log.e(TAG, "=》 权限被拒绝: ")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initData()
    }

    private fun initView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                allowFileAccess = true
                useWideViewPort = true
                domStorageEnabled = true
                databaseEnabled = false
                loadWithOverviewMode = true
                blockNetworkImage = false
                loadsImagesAutomatically = true
                // 防止系统字体大小影响web
                textZoom = 100
                // 解决跨域请求问题
                allowFileAccessFromFileURLs = true
//                userAgentString = userAgentString + "/himo-app-android"
            }

            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    Log.e(TAG, "onJsAlert: $message")
                    return super.onJsAlert(view, url, message, result)
                }

                override fun onJsConfirm(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    Log.e(TAG, "onJsConfirm: $message")
                    return super.onJsConfirm(view, url, message, result)
                }

                override fun onJsPrompt(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    defaultValue: String?,
                    result: JsPromptResult?
                ): Boolean {
                    Log.e(TAG, "onJsPrompt: $message")
                    return super.onJsPrompt(view, url, message, defaultValue, result)
                }

                override fun onPermissionRequest(request: PermissionRequest) {
                    super.onPermissionRequest(request)
                    Log.e(TAG, "onPermissionRequest: 请求权限：${request}")
                    if (ContextCompat.checkSelfPermission(
                            this@WebViewActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // 权限未被授予，需要向用户请求权限
//                        ActivityCompat.requestPermissions(
//                            this@WebViewActivity,
//                            arrayOf(Manifest.permission.CAMERA),
//                            CAMERA_PERMISSION_REQUEST_CODE
//                        )
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        // 权限已经被授予，可以执行相应操作
                        Log.e(TAG, "onPermissionRequest: 已经授权：${request}")
                    }

                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    Log.e(TAG, "onProgressChanged: 进度：$newProgress")
                }

                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    Log.e(TAG, "onShowFileChooser: 文件选择：$fileChooserParams")
                    mFilePathCallback = filePathCallback
                    val fileChooseIntent = fileChooserParams.createIntent()
                    launcher.launch(fileChooseIntent)
                    return true
//                    return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.e(TAG, "onPageStarted: 开始加载页面")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.e(TAG, "onPageFinished: 页面加载完成")
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e(TAG, "onReceivedError: 加载出错:${error.description}")
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    super.onReceivedSslError(view, handler, error)
                    Log.e(TAG, "onReceivedSslError: 加载证书出错")
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    Log.e(TAG, "shouldOverrideUrlLoading: ${request.url}")
                    request.url.scheme?.apply {
                        return if (!startsWith("http") || !startsWith("wss") || !startsWith("file")) {
                            try {
                                Log.e(TAG, "shouldOverrideUrlLoading: url==>$url")
                                val intent = Intent(Intent.ACTION_VIEW, request.url)
                                ContextCompat.startActivity(this@WebViewActivity, intent, null)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                                Log.e(TAG, "shouldOverrideUrlLoading: ${e.message}")
                            }
                            true
                        } else {
                            // view.loadUrl(request.url.toString())
                            super.shouldOverrideUrlLoading(view, request)
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }

            loadUrl("file:///android_asset/h5_sample.html")
//            loadUrl("https://m.haimati.cn/common-h5/index.html#/wxUrlScheme?appName=himo_app&path=%2Fpages%2Findex%2Findex")
//            loadUrl("https://postpay-2g5hm2oxbbb721a4-1258211818.tcloudbaseapp.com/jump-mp.html")
        }
    }

    private fun initData() {

    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}