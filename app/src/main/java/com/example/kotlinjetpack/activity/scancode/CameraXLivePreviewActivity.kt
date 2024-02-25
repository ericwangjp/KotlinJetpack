package com.example.kotlinjetpack.activity.scancode

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinjetpack.databinding.ActivityCameraxLivePreviewBinding
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.demo.kotlin.barcodescanner.BarcodeScannerProcessor

/**
 * desc: Live preview demo app for ML Kit APIs using CameraX
 * CameraX is only supported on SDK version >=21
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2024/1/22 16:37
 */
class CameraXLivePreviewActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback, CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityCameraxLivePreviewBinding
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var cameraSelector: CameraSelector
    private var targetAnalysisSize: Size? = null
    private var camera: Camera? = null
    private val zoomCallback: ZoomSuggestionOptions.ZoomCallback =
        ZoomSuggestionOptions.ZoomCallback { zoomRatio ->
            Log.e(TAG, "zoomRatio: $zoomRatio")
//            if (camera?.isClosed()) {
            if (camera == null) {
                return@ZoomCallback false
            }
            camera?.cameraControl?.setZoomRatio(zoomRatio)
            return@ZoomCallback true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraxLivePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate")
        if (savedInstanceState != null) {
            lensFacing = savedInstanceState.getInt(
                STATE_LENS_FACING, CameraSelector.LENS_FACING_BACK
            )
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        binding.tbCameraSwitch.setOnCheckedChangeListener(this)
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CameraXViewModel::class.java].processCameraProvider.observe(
            this
        ) { provider: ProcessCameraProvider? ->
            cameraProvider = provider
            if (allPermissionsGranted()) {
                bindAllCameraUseCases()
            }
        }

        if (!allPermissionsGranted()) {
            runtimePermissions
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt(STATE_LENS_FACING, lensFacing)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        Log.d(TAG, "切换摄像头")
        if (cameraProvider == null) {
            return
        }
        val newLensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
        try {
            if (cameraProvider!!.hasCamera(newCameraSelector)) {
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                bindAllCameraUseCases()
                return
            }
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
            Toast.makeText(
                applicationContext,
                "This device does not have lens with facing: $newLensFacing",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()

        imageProcessor?.run {
            this.stop()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run {
            this.stop()
        }
    }

    private fun bindAllCameraUseCases() {
        cameraProvider?.let {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            it.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        cameraProvider?.unbind(previewUseCase)
        previewUseCase = Preview.Builder().build()
        previewUseCase!!.setSurfaceProvider(binding.previewView.surfaceProvider)
        camera = cameraProvider!!.bindToLifecycle(
            this, cameraSelector, previewUseCase
        )
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        cameraProvider?.unbind(analysisUseCase)
        imageProcessor?.stop()
        imageProcessor = try {
            BarcodeScannerProcessor(this, zoomCallback)
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: barcode", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        analysisUseCase = ImageAnalysis.Builder().apply {
            targetAnalysisSize?.let {
                this.setTargetResolution(it)
            }
        }.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(this)
        ) { imageProxy: ImageProxy ->
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    binding.graphicOverlay.setImageSourceInfo(
                        imageProxy.width, imageProxy.height, isImageFlipped
                    )
                } else {
                    binding.graphicOverlay.setImageSourceInfo(
                        imageProxy.height, imageProxy.width, isImageFlipped
                    )
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }
            try {
                imageProcessor!!.processImageProxy(imageProxy, binding.graphicOverlay)
            } catch (e: MlKitException) {
                Log.e(
                    TAG, "Failed to process image. Error: " + e.localizedMessage
                )
                Toast.makeText(
                    applicationContext, e.localizedMessage, Toast.LENGTH_SHORT
                ).show()
            }
        }
        camera = cameraProvider!!.bindToLifecycle(
            this, cameraSelector, analysisUseCase
        )
    }

    private val requiredPermissions: Array<String?>
        get() = try {
            val info =
                this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            arrayOfNulls(0)
        }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            if (!isPermissionGranted(this, permission)) {
                return false
            }
        }
        return true
    }

    private val runtimePermissions: Unit
        get() {
            val allNeededPermissions: MutableList<String?> = ArrayList()
            for (permission in requiredPermissions) {
                if (!isPermissionGranted(this, permission)) {
                    allNeededPermissions.add(permission)
                }
            }
            if (allNeededPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
                )
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        Log.i(TAG, "Permission granted!")
        if (allPermissionsGranted()) {
            bindAllCameraUseCases()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val TAG = "CameraXLivePreview"
        private const val PERMISSION_REQUESTS = 1
        private const val STATE_LENS_FACING = "lens_facing"

        private fun isPermissionGranted(
            context: Context, permission: String?
        ): Boolean {
            if (ContextCompat.checkSelfPermission(
                    context, permission!!
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Permission granted: $permission")
                return true
            }
            Log.i(TAG, "Permission NOT granted: $permission")
            return false
        }
    }
}