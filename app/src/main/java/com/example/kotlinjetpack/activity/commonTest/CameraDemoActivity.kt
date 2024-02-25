package com.example.kotlinjetpack.activity.commonTest

import android.graphics.Point
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.example.kotlinjetpack.databinding.ActivityCameraDemoBinding
import com.example.kotlinjetpack.utils.CameraXController
import com.example.kotlinjetpack.utils.camera.CameraHelper
import com.example.kotlinjetpack.utils.camera.CameraListener


class CameraDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraDemoBinding

    //    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mSurfaceHolder: SurfaceHolder
    private var mCamera: Camera? = null
    private var mCameraHelper: CameraHelper? = null
    private var mMediaRecorder: MediaRecorder? = null
    private lateinit var mPreviewView: PreviewView
    private lateinit var mCameraProvider: ProcessCameraProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }

    private fun initData() {
        binding.apply {
            btnTypeOne.setOnClickListener {
                initSurfaceView()
            }
            btnTypeTwo.setOnClickListener {
                Toast.makeText(this@CameraDemoActivity, "Camera2 暂无使用示例", Toast.LENGTH_SHORT)
                    .show()
            }
            btnTypeThree.setOnClickListener {
                mPreviewView = PreviewView(this@CameraDemoActivity).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            if (mPreviewView.isShown) {
                                startCameraX()
                            }
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })
                    binding.flyaoutContainer.removeAllViews()
                    binding.flyaoutContainer.addView(this)
                }
            }
        }
    }

    private fun startCameraX() {
        CameraXController.setUpCamera(this, mPreviewView.surfaceProvider)
    }

    private fun initSurfaceView() {
//        binding.surfaceView.layoutParams = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        val viewTreeObserveListener = object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                binding.surfaceView.post {
//                    setupCameraHelper()
//                }
//                binding.surfaceView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        }
//        binding.surfaceView.viewTreeObserver.addOnGlobalLayoutListener(viewTreeObserveListener)
        setupCameraHelper()

        mSurfaceHolder = binding.surfaceView.holder
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mSurfaceHolder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceCreated(p0: SurfaceHolder) {
                initCamera()
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                releaseAllCamera()
            }

            override fun surfaceRedrawNeeded(p0: SurfaceHolder) {

            }
        })
    }

    private fun setupCameraHelper() {
        Log.e("fqy-->", "measuredWidth: ${binding.surfaceView.measuredWidth}")
        Log.e("fqy-->", "measuredHeight: ${binding.surfaceView.measuredHeight}")
        mCameraHelper = CameraHelper.Builder()
            .previewViewSize(
                Point(
                    binding.surfaceView.measuredWidth,
                    binding.surfaceView.measuredHeight
                )
            )
            .rotation(windowManager.defaultDisplay.rotation)
            .specificCameraId(Camera.CameraInfo.CAMERA_FACING_BACK).isMirror(false)
            .previewOn(binding.surfaceView).cameraListener(object : CameraListener {
                override fun onCameraOpened(
                    camera: Camera,
                    cameraId: Int,
                    displayOrientation: Int,
                    isMirror: Boolean
                ) {
                    // 可以使用 MediaRecorder 去录制视频
                    mMediaRecorder = MediaRecorder()
                }

                override fun onPreview(data: ByteArray, camera: Camera) {

                }

                override fun onCameraClosed() {

                }

                override fun onCameraError(e: Exception) {

                }

                override fun onCameraConfigurationChanged(cameraID: Int, displayOrientation: Int) {

                }
            }).build()
        mCameraHelper?.start()
    }

    private fun initCamera() {
        if (mCamera != null) {
            releaseAllCamera()
        }
        try {
            mCamera = Camera.open()

            mCamera?.apply {
                //设置拍摄方向为90度（竖屏）
                setDisplayOrientation(90)
                setPreviewDisplay(mSurfaceHolder)
                startPreview()
                unlock()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            releaseAllCamera()
        }

    }


    private fun releaseAllCamera() {
        mCameraHelper?.stop()
        mCameraHelper?.release()
    }
}