package com.example.kotlinjetpack.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoOutput
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs

/**
 * desc: CameraXController
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/8/30 14:02
 */
object CameraXController {

    private var mCameraProvider: ProcessCameraProvider? = null
    private var mLensFacing = 0
    private var mCameraSelector: CameraSelector? = null
    private var mVideoCapture: VideoCapture<VideoOutput>? = null
    private var mCameraCallback: (() -> Unit)? = null
    private val mExecutorService = Executors.newSingleThreadExecutor()

    //初始化 CameraX 相关配置
    @SuppressLint("RestrictedApi")
    fun setUpCamera(context: Context, surfaceProvider: Preview.SurfaceProvider) {

        //获取屏幕的分辨率与宽高比
        val displayMetrics = context.resources.displayMetrics
        val screenAspectRatio = aspectRatio(displayMetrics.widthPixels, displayMetrics.heightPixels)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            mCameraProvider = cameraProviderFuture.get()

            //镜头选择
            mLensFacing = lensFacing
            mCameraSelector = CameraSelector.Builder().requireLensFacing(mLensFacing).build()

            //预览对象
            val preview: Preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .build()

            preview.setSurfaceProvider(surfaceProvider)


            //录制视频对象
            val recorder =
                Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build()
//            mVideoCapture = VideoCapture.Builder()
//                .setTargetAspectRatio(screenAspectRatio)
//                .setAudioRecordSource(MediaRecorder.AudioSource.MIC) //设置音频源麦克风
            //视频帧率
//                .setVideoFrameRate(30)
            //bit率
//                .setBitRate(3 * 1024 * 1024)
//                .build()

            //绑定到页面
            mCameraProvider?.unbindAll()
            val camera = mCameraProvider?.bindToLifecycle(
                context as LifecycleOwner,
                mCameraSelector!!,
                mVideoCapture,
                preview
            )

            val cameraInfo = camera?.cameraInfo
            val cameraControl = camera?.cameraControl

        }, ContextCompat.getMainExecutor(context))
    }

    //根据屏幕宽高比设置预览比例为4:3还是16:9
    private fun aspectRatio(widthPixels: Int, heightPixels: Int): Int {
        val previewRatio =
            widthPixels.coerceAtLeast(heightPixels).toDouble() / widthPixels.coerceAtMost(
                heightPixels
            ).toDouble()
        return if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else {
            AspectRatio.RATIO_16_9
        }
    }

    //优先选择哪一个摄像头镜头
    private val lensFacing: Int
        get() {
            if (hasBackCamera()) {
                return CameraSelector.LENS_FACING_BACK
            }
            return if (hasFrontCamera()) {
                CameraSelector.LENS_FACING_FRONT
            } else -1
        }

    //是否有后摄像头
    private fun hasBackCamera(): Boolean {
        if (mCameraProvider == null) {
            return false
        }
        try {
            return mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }

    //是否有前摄像头
    private fun hasFrontCamera(): Boolean {
        if (mCameraProvider == null) {
            return false
        }
        try {
            return mCameraProvider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        } catch (e: CameraInfoUnavailableException) {
            e.printStackTrace()
        }
        return false
    }


    // 开始录制
    @SuppressLint("MissingPermission", "RestrictedApi")
    fun startCameraRecord(outFile: File) {
        mVideoCapture ?: return

//        val outputFileOptions: VideoCapture.OutputFileOptions =
//            VideoCapture.OutputFileOptions.Builder(outFile).build()
//        mVideoCapture!!.startRecording(
//            outputFileOptions,
//            mExecutorService,
//            object : VideoCapture.OnVideoSavedCallback {
//                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
//                    Log.d("视频保存成功:", "outputFileResults:${outputFileResults.savedUri}")
//                    mCameraCallback?.invoke()
//                }
//
//                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
//                    Log.e("onError", message)
//                }
//            })
    }

    // 停止录制
    @SuppressLint("RestrictedApi")
    fun stopCameraRecord() {
//        mVideoCapture?.stopRecording()
    }

    // 释放资源
    @SuppressLint("RestrictedApi")
    fun releaseAll() {
//        mVideoCapture?.stopRecording()
        mExecutorService.shutdown()
        mCameraProvider?.unbindAll()
//        mCameraProvider?.shutdown()
        mCameraProvider = null
    }
}
