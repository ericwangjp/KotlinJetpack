package com.example.kotlinjetpack.utils.camera

import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.view.*
import java.io.IOException
import java.util.*
import kotlin.math.abs

/**
 * desc: CameraHelper
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/8/25 17:32
 */
class CameraHelper private constructor(builder: Builder) : Camera.PreviewCallback {
    private var mCamera: Camera? = null
    private var mCameraId = 0
    private var previewViewSize: Point?
    private var previewDisplayView: View?
    private var previewSize: Camera.Size? = null
    private var specificPreviewSize: Point?
    private var displayOrientation = 0
    private var rotation: Int
    private var additionalRotation: Int
    private var isMirror = false
    private var specificCameraId: Int = -1
    private var cameraListener //自定义监听回调
            : CameraListener?

    fun init() {
        if (previewDisplayView is TextureView) {
            (previewDisplayView as TextureView?)?.surfaceTextureListener = textureListener
        } else if (previewDisplayView is SurfaceView) {
            (previewDisplayView as SurfaceView?)?.holder?.addCallback(surfaceCallback)
        }
        if (isMirror) {
            previewDisplayView?.scaleX = -1f
        }
    }

    fun start(): String? {
        var firstSize: String? = null
        var finalSize: String? = null
        synchronized(this) {
            if (mCamera != null) {
                return null
            }
            //相机数量为2则打开1,1则打开0,相机ID 1为前置，0为后置
            mCameraId = Camera.getNumberOfCameras() - 1
            //若指定了相机ID且该相机存在，则打开指定的相机
            if (specificCameraId <= mCameraId) {
                mCameraId = specificCameraId
            }

            //没有相机
            if (mCameraId == -1) {
                if (cameraListener != null) {
                    cameraListener!!.onCameraError(Exception("camera not found"))
                }
                return null
            }

            //开启相机
            if (mCamera == null) {
                mCamera = Camera.open(mCameraId)
            }

            //获取预览方向
            displayOrientation = getCameraOri(rotation)
            mCamera?.setDisplayOrientation(displayOrientation)
            try {
                val parameters: Camera.Parameters? = mCamera?.parameters
                parameters?.previewFormat = ImageFormat.NV21

                //预览大小设置
                previewSize = parameters?.previewSize
                firstSize = "${previewSize?.width} - ${previewSize?.height}"
                val supportedPreviewSizes: List<Camera.Size>? =
                    parameters?.supportedPreviewSizes
                if (supportedPreviewSizes != null && supportedPreviewSizes.isNotEmpty()) {
                    previewSize = getBestSupportedSize(supportedPreviewSizes, previewViewSize)
                    finalSize = "${previewSize?.width} - ${previewSize?.height}"
                }
                parameters?.setPreviewSize(previewSize?.width ?: 720, previewSize?.height ?: 1080)

                //对焦模式设置
                val supportedFocusModes: List<String>? =
                    parameters?.supportedFocusModes
                if (supportedFocusModes != null && supportedFocusModes.isNotEmpty()) {
                    if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                    }
                }

                //Camera 配置完成，设置回去
                mCamera?.parameters = parameters

                //绑定到 TextureView 或 SurfaceView
                if (previewDisplayView is TextureView) {
                    mCamera?.setPreviewTexture((previewDisplayView as TextureView?)?.surfaceTexture)
                } else {
                    mCamera?.setPreviewDisplay((previewDisplayView as SurfaceView?)?.holder)
                }

                //启动预览并设置预览回调
                mCamera?.setPreviewCallback(this)
                mCamera?.startPreview()
                if (cameraListener != null) {
                    cameraListener!!.onCameraOpened(
                        mCamera!!,
                        mCameraId,
                        displayOrientation,
                        isMirror
                    )
                }
            } catch (e: Exception) {
                if (cameraListener != null) {
                    cameraListener!!.onCameraError(e)
                }
            }
        }
        return "firstSize :$firstSize finalSize:$finalSize"
    }

    private fun getCameraOri(rotation: Int): Int {
        var degrees = rotation * 90
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
            else -> {}
        }
        additionalRotation /= 90
        additionalRotation *= 90
        degrees += additionalRotation
        var result: Int
        val info: Camera.CameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(mCameraId, info)
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    fun stop() {
        synchronized(this) {
            if (mCamera == null) {
                return
            }
            mCamera!!.setPreviewCallback(null)
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
            if (cameraListener != null) {
                cameraListener!!.onCameraClosed()
            }
        }
    }

    fun isStopped(): Boolean {
        synchronized(this) { return mCamera == null }
    }

    fun release() {
        synchronized(this) {
            stop()
            previewDisplayView = null
            specificCameraId = -1
            cameraListener = null
            previewViewSize = null
            specificPreviewSize = null
            previewSize = null
        }
    }

    /**
     * 根据 Camera 获取支持的宽高，获取到最适合的预览宽高
     */
    private fun getBestSupportedSize(
        sizes: List<Camera.Size>,
        previewViewSize: Point?
    ): Camera.Size? {
        var sizes: List<Camera.Size> = sizes
        if (sizes.isEmpty()) {
            return mCamera?.parameters?.previewSize
        }
        val tempSizes: Array<Camera.Size> = sizes.toTypedArray()
        Arrays.sort(tempSizes, Comparator<Camera.Size> { o1, o2 ->
            if (o1.width > o2.width) {
                -1
            } else if (o1.width == o2.width) {
                if (o1.height > o2.height) -1 else 1
            } else {
                1
            }
        })
        sizes = tempSizes.asList()
        var bestSize: Camera.Size = sizes[0]
        var previewViewRatio: Float
        previewViewRatio = if (previewViewSize != null) {
            previewViewSize.x * 1.0f / previewViewSize.y
        } else {
            bestSize.width * 1.0f / bestSize.height
        }
        if (previewViewRatio > 1) {
            previewViewRatio = 1 / previewViewRatio
        }
        val isNormalRotate = additionalRotation % 180 == 0
        for (s in sizes) {
            if (specificPreviewSize != null && specificPreviewSize?.x == s.width && specificPreviewSize?.y == s.height) {
                return s
            }
            if (isNormalRotate) {
                if (abs(s.height / s.width - previewViewRatio) < abs(bestSize.height / bestSize.width - previewViewRatio)) {
                    bestSize = s
                }
            } else {
                if (abs(s.width / s.height - previewViewRatio) < abs(bestSize.width / bestSize.height - previewViewRatio)) {
                    bestSize = s
                }
            }
        }
        return bestSize
    }

    fun getSupportedPreviewSizes(): List<Camera.Size>? {
        return if (mCamera == null) {
            null
        } else mCamera!!.parameters.supportedPreviewSizes
    }

    fun getSupportedPictureSizes(): List<Camera.Size>? {
        return if (mCamera == null) {
            null
        } else mCamera!!.parameters.supportedPictureSizes
    }

    override fun onPreviewFrame(nv21: ByteArray?, camera: Camera) {
        if (cameraListener != null && nv21 != null) {
            cameraListener!!.onPreview(nv21, camera)
        }
    }

    /**
     * TextureView 的监听回调
     */
    private val textureListener: TextureView.SurfaceTextureListener =
        object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
//            start();
                if (mCamera != null) {
                    try {
                        mCamera!!.setPreviewTexture(surfaceTexture)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                stop()
                return false
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
            }
        }

    /**
     * SurfaceView 的监听回调
     */
    private val surfaceCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            if (mCamera != null) {
                try {
                    mCamera!!.setPreviewDisplay(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
        override fun surfaceDestroyed(holder: SurfaceHolder) {
            stop()
        }

    }

    init {
        previewDisplayView = builder.previewDisplayView
        specificCameraId = builder.specificCameraId
        cameraListener = builder.cameraListener
        rotation = builder.rotation
        additionalRotation = builder.additionalRotation
        previewViewSize = builder.previewViewSize
        specificPreviewSize = builder.previewSize
        if (builder.previewDisplayView is TextureView) {
            isMirror = builder.isMirror
        } else if (isMirror) {
            throw RuntimeException("mirror is effective only when the preview is on a textureView")
        }
    }

    /**
     * 切换摄像头方向
     */
    fun changeDisplayOrientation(rotation: Int) {
        if (mCamera != null) {
            this.rotation = rotation
            displayOrientation = getCameraOri(rotation)
            mCamera!!.setDisplayOrientation(displayOrientation)
            if (cameraListener != null) {
                cameraListener!!.onCameraConfigurationChanged(mCameraId, displayOrientation)
            }
        }
    }

    /**
     * 翻转前后摄像镜头
     */
    fun switchCamera(): Boolean {
        if (Camera.getNumberOfCameras() < 2) {
            return false
        }
        // cameraId ,0为后置，1为前置
        specificCameraId = 1 - mCameraId
        stop()
        start()
        return true
    }

    /**
     * 使用构建者模式创建，配置Camera
     */
    data class Builder(
        //预览显示的view，目前仅支持surfaceView和textureView
        var previewDisplayView: View? = null,

        //是否镜像显示，只支持textureView
        var isMirror: Boolean = false,

        //指定的相机ID
        var specificCameraId: Int = -1,

        //事件回调
        var cameraListener: CameraListener? = null,

        //屏幕的长宽，在选择最佳相机比例时用到
        var previewViewSize: Point? = null,

        //屏幕的方向，一般传入getWindowManager().getDefaultDisplay().getRotation()的值即可
        var rotation: Int = 0,

        //指定的预览宽高，若系统支持则会以这个预览宽高进行预览
        var previewSize: Point? = null,

        //额外的旋转角度（用于适配一些定制设备）
        var additionalRotation: Int = 0
    ) {


        //必须要绑定到SurfaceView或者TextureView上
        fun previewOn(view: View?): Builder {
            return if (view is SurfaceView || view is TextureView) {
                previewDisplayView = view
                this
            } else {
                throw RuntimeException("you must preview on a textureView or a surfaceView")
            }
        }

        fun isMirror(mirror: Boolean): Builder {
            isMirror = mirror
            return this
        }

        fun previewSize(point: Point?): Builder {
            previewSize = point
            return this
        }

        fun previewViewSize(point: Point?): Builder {
            previewViewSize = point
            return this
        }

        fun rotation(rotation: Int) = apply {
            this.rotation = rotation
        }

        fun additionalRotation(rotation: Int) = apply {
            this.additionalRotation = rotation
        }

        fun specificCameraId(id: Int) = apply {
            this.specificCameraId = id
        }

        fun cameraListener(listener: CameraListener?) = apply {
            this.cameraListener = listener
        }

        fun build(): CameraHelper {
            if (previewViewSize == null) {
                throw RuntimeException("previewViewSize is null, now use default previewSize")
            }
            if (cameraListener == null) {
                throw RuntimeException("cameraListener is null, callback will not be called")
            }
            if (previewDisplayView == null) {
                throw RuntimeException("you must preview on a textureView or a surfaceView")
            }

            //build的时候顺便执行初始化
            val cameraHelper = CameraHelper(this)
            cameraHelper.init()
            return cameraHelper
        }
    }
}
