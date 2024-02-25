package com.example.kotlinjetpack.activity.commonTest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.kotlinjetpack.activity.cameraxmlkit.CameraXScanActivity
import com.example.kotlinjetpack.activity.scancode.CameraXLivePreviewActivity
import com.example.kotlinjetpack.databinding.ActivityGoogleScanCodeBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import java.nio.ByteBuffer

class GoogleScanCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoogleScanCodeBinding
    private val TAG = "GoogleScanCodeActivity"
    private val gmsBarcodeScannerOptions by lazy {
        GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .enableAutoZoom().build()
    }
    private lateinit var gmsBarcodeScanner: GmsBarcodeScanner
    private lateinit var barcodeScanner: BarcodeScanner

    //    todo 动态获取支持的最大 ratio
//    Camera1: camera.getZoomRatios(camera.getMaxZoom())
//    Camera2: characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)
//    CameraX: camera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio()
    private val maxSupportedZoomRatio = 5f
//    private val zoomCallback: ZoomSuggestionOptions.ZoomCallback =
//        ZoomSuggestionOptions.ZoomCallback { zoomRatio ->
//            if (camera.isClosed()) {
//                return@ZoomCallback false
//            }
//            camera.getCameraControl().setZoomRatio(zoomRatio)
//            return@ZoomCallback true
//        }
//    private val barcodeScannerOptions by lazy {
//        BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
//            .setZoomSuggestionOptions(
//                ZoomSuggestionOptions.Builder(zoomCallback)
//                    .setMaxSupportedZoomRatio(maxSupportedZoomRatio).build()
//            ).enableAllPotentialBarcodes().build()
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleScanCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }

    private fun initData() {
        gmsBarcodeScanner = GmsBarcodeScanning.getClient(this, gmsBarcodeScannerOptions)
//        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)
        binding.apply {
            //
            /**
             * Google 扫码器
             * 无需应用请求摄像头权限
             * 需要 Android API 级别 21 或更高级别。确保应用的 build 文件使用的 minSdkVersion 值不小于 21
             * 不支持界面定制
             * 在国内不适用，依赖 Google Play 服务，启动前必须先下载才能使用
             */
            btnStartGoogleScan.setOnClickListener {
                gmsBarcodeScanner.startScan().addOnSuccessListener { barcode ->
                    Log.e(TAG, "扫码成功: ${barcode.rawValue}")
                    tvScanResult.text = barcode.rawValue
                }.addOnCanceledListener {
                    Toast.makeText(this@GoogleScanCodeActivity, "扫码取消", Toast.LENGTH_SHORT)
                        .show()
                }.addOnFailureListener { e ->
                    Log.e(TAG, "扫码失败: ${e.message}")
                    tvScanResult.apply {
                        text = e.toString()
                        setTextColor(Color.RED)
                    }
                }
            }

            /**
             * 支持界面自定义
             * 实施步骤	    模型通过 Google Play 服务动态下载	    模型在构建时静态关联到您的应用。
             * 应用大小	    大小增加约 200 KB。	                大小增加约 2.4 MB。
             * 初始化时间	在首次使用之前，可能需要等待模型下载。	模型立即可用。
             */
            btnStartMlkitScan.setOnClickListener {
//                val inputImage:InputImage = getImageFromBitmap()
//                barcodeScanner.process(inputImage).addOnSuccessListener { barcodes ->
//                    Log.e(TAG, "扫码成功: $barcodes")
//                    for (barcode in barcodes) {
//                        val bounds = barcode.boundingBox
//                        val corners = barcode.cornerPoints
//                        val rawValue = barcode.rawValue
//                        val valueType = barcode.valueType
//                        tvScanResult.text = "${tvScanResult.text}\n${rawValue}"
//                        // See API reference for complete list of supported types
//                        when (valueType) {
//                            Barcode.TYPE_WIFI -> {
//                                val ssid = barcode.wifi!!.ssid
//                                val password = barcode.wifi!!.password
//                                val type = barcode.wifi!!.encryptionType
//                            }
//                            Barcode.TYPE_URL -> {
//                                val title = barcode.url!!.title
//                                val url = barcode.url!!.url
//                            }
//                        }
//                    }
//                }.addOnCanceledListener {
//                    Toast.makeText(this@GoogleScanCodeActivity, "扫码取消", Toast.LENGTH_SHORT)
//                        .show()
//                }.addOnFailureListener { e ->
//                    Log.e(TAG, "扫码失败: ${e.message}")
//                    tvScanResult.apply {
//                        text = e.toString()
//                        setTextColor(Color.RED)
//                    }
//                }

                startActivity(
                    Intent(
                        this@GoogleScanCodeActivity,
                        CameraXLivePreviewActivity::class.java
                    )
                )
            }

            btnStartMlkitComponentScan.setOnClickListener {
                startActivity(Intent(this@GoogleScanCodeActivity, CameraXScanActivity::class.java))
            }
        }
    }

    //    使用 CameraX 来获取和分析图片
    private class CustomImageAnalyzer : ImageAnalysis.Analyzer {
        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                // Pass image to an ML Kit Vision API

            }
        }

    }

    /**
     * 未使用 CameraX 时，计算图片旋转角度
     */
    private val imageOrientations = SparseIntArray().apply {
        append(Surface.ROTATION_0, 0)
        append(Surface.ROTATION_90, 90)
        append(Surface.ROTATION_180, 180)
        append(Surface.ROTATION_270, 270)
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(
        cameraId: String, activity: Activity, isFrontFacing: Boolean
    ): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = imageOrientations.get(deviceRotation)

        // Get the device's sensor orientation.
        val cameraManager = activity.getSystemService(CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager.getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!

        rotationCompensation = if (isFrontFacing) {
            (sensorOrientation + rotationCompensation) % 360
        } else { // back-facing
            (sensorOrientation - rotationCompensation + 360) % 360
        }
        return rotationCompensation
    }

    private fun getImageFromURI(context: Context, imageUri: Uri) = try {
        InputImage.fromFilePath(context, imageUri)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }


    private fun getImageFromByteBuffer(byteBuffer: ByteBuffer, rotationDegrees: Int) =
        InputImage.fromByteBuffer(
            byteBuffer,/* image width */
            480,/* image height */
            360,
            rotationDegrees,
            InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        )


    private fun getImageFromByteArray(byteArray: ByteArray, rotationDegrees: Int) =
        InputImage.fromByteArray(
            byteArray,/* image width */
            480,/* image height */
            360,
            rotationDegrees,
            InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        )

    private fun getImageFromBitmap(bitmap: Bitmap) = InputImage.fromBitmap(bitmap, 0)


}