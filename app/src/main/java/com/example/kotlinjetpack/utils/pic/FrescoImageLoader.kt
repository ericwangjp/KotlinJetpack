package com.example.kotlinjetpack.utils.pic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.example.kotlinjetpack.utils.StreamTool.read
import com.example.kotlinjetpack.utils.StreamTool.write
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.memory.PooledByteBuffer
import com.facebook.common.memory.PooledByteBufferInputStream
import com.facebook.common.references.CloseableReference
import com.facebook.common.util.UriUtil
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.animated.base.AnimatedImageResult
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.image.CloseableAnimatedImage
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.BasePostprocessor
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * desc: FrescoImageLoader
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/7 6:49 下午
 */
object FrescoImageLoader {

    fun loadImage(simpleDraweeView: SimpleDraweeView?, url: String?) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, 0, 0, null, null, false)
    }

    fun loadImage(
        simpleDraweeView: SimpleDraweeView?,
        url: String?,
        reqWidth: Int,
        reqHeight: Int
    ) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, null, null, false)
    }

    fun loadImage(
        simpleDraweeView: SimpleDraweeView?,
        url: String?,
        processor: BasePostprocessor?
    ) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, 0, 0, processor, null, false)
    }

    fun loadImage(
        simpleDraweeView: SimpleDraweeView?, url: String?,
        reqWidth: Int, reqHeight: Int, processor: BasePostprocessor?
    ) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, processor, null, false)
    }

    fun loadImage(
        simpleDraweeView: SimpleDraweeView?,
        url: String?,
        controllerListener: ControllerListener<ImageInfo?>?
    ) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, 0, 0, null, controllerListener, false)
    }

    fun loadImageSmall(simpleDraweeView: SimpleDraweeView?, url: String?) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, 0, 0, null, null, true)
    }

    fun loadImageSmall(
        simpleDraweeView: SimpleDraweeView?,
        url: String?,
        reqWidth: Int,
        reqHeight: Int
    ) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, null, null, true)
    }

    fun loadImageSmall(
        simpleDraweeView: SimpleDraweeView?,
        url: String?,
        processor: BasePostprocessor?
    ) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, 0, 0, processor, null, true)
    }

    fun loadImageSmall(
        simpleDraweeView: SimpleDraweeView?, url: String?,
        reqWidth: Int, reqHeight: Int, processor: BasePostprocessor?
    ) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return
        }
        val uri = Uri.parse(url)
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, processor, null, true)
    }

    fun loadFile(simpleDraweeView: SimpleDraweeView, filePath: String?) {
        if (TextUtils.isEmpty(filePath)) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_FILE_SCHEME)
            .path(filePath)
            .build()
        loadImage(simpleDraweeView, uri, 0, 0, null, null, false)
    }

    fun loadFile(
        simpleDraweeView: SimpleDraweeView,
        filePath: String?,
        reqWidth: Int,
        reqHeight: Int
    ) {
        if (TextUtils.isEmpty(filePath)) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_FILE_SCHEME)
            .path(filePath)
            .build()
        val controllerListener: BaseControllerListener<ImageInfo?> =
            object : BaseControllerListener<ImageInfo?>() {
                override fun onFinalImageSet(
                    id: String,
                    imageInfo: ImageInfo?,
                    anim: Animatable?
                ) {
                    if (imageInfo == null) {
                        return
                    }
                    val vp = simpleDraweeView.layoutParams
                    vp.width = reqWidth
                    vp.height = reqHeight
                    simpleDraweeView.requestLayout()
                }
            }
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, null, controllerListener, false)
    }

    fun loadFile(
        simpleDraweeView: SimpleDraweeView,
        filePath: String?,
        processor: BasePostprocessor?
    ) {
        if (TextUtils.isEmpty(filePath)) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_FILE_SCHEME)
            .path(filePath)
            .build()
        loadImage(simpleDraweeView, uri, 0, 0, processor, null, false)
    }

    fun loadFile(
        simpleDraweeView: SimpleDraweeView, filePath: String?,
        reqWidth: Int, reqHeight: Int, processor: BasePostprocessor?
    ) {
        if (TextUtils.isEmpty(filePath)) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_FILE_SCHEME)
            .path(filePath)
            .build()
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, processor, null, false)
    }

    fun loadDrawable(simpleDraweeView: SimpleDraweeView?, resId: Int) {
        if (resId == 0 || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
            .path(resId.toString())
            .build()
        loadImage(simpleDraweeView, uri, 0, 0, null, null, false)
    }

    fun loadDrawable(
        simpleDraweeView: SimpleDraweeView?,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ) {
        if (resId == 0 || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
            .path(resId.toString())
            .build()
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, null, null, false)
    }

    fun loadDrawable(
        simpleDraweeView: SimpleDraweeView?,
        resId: Int,
        processor: BasePostprocessor?
    ) {
        if (resId == 0 || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
            .path(resId.toString())
            .build()
        loadImage(simpleDraweeView, uri, 0, 0, processor, null, false)
    }

    fun loadDrawable(
        simpleDraweeView: SimpleDraweeView?, resId: Int,
        reqWidth: Int, reqHeight: Int, processor: BasePostprocessor?
    ) {
        if (resId == 0 || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
            .path(resId.toString())
            .build()
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, processor, null, false)
    }

    fun loadAssetDrawable(simpleDraweeView: SimpleDraweeView?, filename: String?) {
        if (filename == null || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_ASSET_SCHEME)
            .path(filename)
            .build()
        loadImage(simpleDraweeView, uri, 0, 0, null, null, false)
    }

    fun loadAssetDrawable(
        simpleDraweeView: SimpleDraweeView?,
        filename: String?,
        reqWidth: Int,
        reqHeight: Int
    ) {
        if (filename == null || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_ASSET_SCHEME)
            .path(filename)
            .build()
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, null, null, false)
    }

    fun loadAssetDrawable(
        simpleDraweeView: SimpleDraweeView?,
        filename: String?,
        processor: BasePostprocessor?
    ) {
        if (filename == null || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_ASSET_SCHEME)
            .path(filename)
            .build()
        loadImage(simpleDraweeView, uri, 0, 0, processor, null, false)
    }

    fun loadAssetDrawable(
        simpleDraweeView: SimpleDraweeView?, filename: String?,
        reqWidth: Int, reqHeight: Int, processor: BasePostprocessor?
    ) {
        if (filename == null || simpleDraweeView == null) {
            return
        }
        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_ASSET_SCHEME)
            .path(filename)
            .build()
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, processor, null, false)
    }

    fun loadImage(
        simpleDraweeView: SimpleDraweeView,
        uri: Uri?,
        reqWidth: Int,
        reqHeight: Int,
        postprocessor: BasePostprocessor?,
        controllerListener: ControllerListener<ImageInfo?>?,
        isSmall: Boolean
    ) {
        val imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri)
        imageRequestBuilder.rotationOptions = RotationOptions.autoRotate()

        // 不支持图片渐进式加载，理由：https://github.com/facebook/fresco/issues/1204
        imageRequestBuilder.isProgressiveRenderingEnabled = false
        if (isSmall) {
            imageRequestBuilder.cacheChoice = ImageRequest.CacheChoice.SMALL
        }
        if (reqWidth > 0 && reqHeight > 0) {
            imageRequestBuilder.resizeOptions = ResizeOptions(reqWidth, reqHeight)
        }
        if (UriUtil.isLocalFileUri(uri)) {
            imageRequestBuilder.isLocalThumbnailPreviewsEnabled = true
        }
        if (postprocessor != null) {
            imageRequestBuilder.postprocessor = postprocessor
        }
        val imageRequest = imageRequestBuilder.build()
        val draweeControllerBuilder = Fresco.newDraweeControllerBuilder()
        draweeControllerBuilder.oldController = simpleDraweeView.controller
        draweeControllerBuilder.imageRequest = imageRequest
        if (controllerListener != null) {
            draweeControllerBuilder.controllerListener = controllerListener
        }
        draweeControllerBuilder.tapToRetryEnabled = false // 在加载失败后，禁用点击重试功能
        draweeControllerBuilder.autoPlayAnimations = true // 自动播放gif动画
        val draweeController: DraweeController = draweeControllerBuilder.build()
        simpleDraweeView.controller = draweeController
    }

    /**
     * 根据提供的图片URL加载原始图（该方法仅针对大小在100k以内的图片，若不确定图片大小，
     * 请使用下面的downloadImage(String url, final DownloadImageResult loadFileResult) ）
     *
     * @param url             图片URL
     * @param loadImageResult LoadImageResult
     */
    fun loadImage(context: Context, url: String, loadImageResult: IResult<Bitmap?>?) {
        loadOriginalImage(
            context,
            url,
            loadImageResult,
            UiThreadImmediateExecutorService.getInstance()
        )
    }

    /**
     * 根据提供的图片URL加载原始图（该方法仅针对大小在100k以内的图片，若不确定图片大小，
     * 请使用下面的downloadImage(String url, final DownloadImageResult loadFileResult) ）
     *
     * @param url             图片URL
     * @param loadImageResult LoadImageResult
     * @param executor        的取值有以下三个：
     * UiThreadImmediateExecutorService.getInstance() 在回调中进行任何UI操作
     * CallerThreadExecutor.getInstance() 在回调里面做的事情比较少，并且不涉及UI
     * Executors.newSingleThreadExecutor() 你需要做一些比较复杂、耗时的操作，并且不涉及UI（如数据库读写、文件IO），你就不能用上面两个Executor。
     * 你需要开启一个后台Executor，可以参考DefaultExecutorSupplier.forBackgroundTasks。
     */
    fun loadOriginalImage(
        context: Context?,
        url: String?,
        loadImageResult: IResult<Bitmap?>?,
        executor: Executor?
    ) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        val uri = Uri.parse(url)
        val imagePipeline = Fresco.getImagePipeline()
        val builder = ImageRequestBuilder.newBuilderWithSource(uri)
        val imageRequest = builder.build()
        // 获取已解码的图片，返回的是Bitmap
        val dataSource = imagePipeline.fetchDecodedImage(imageRequest, context)
        dataSource.subscribe(object : BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                if (!dataSource.isFinished) {
                    return
                }
                val imageReference = dataSource.result
                if (imageReference != null) {
                    val closeableReference = imageReference.clone()
                    try {
                        val closeableImage = closeableReference.get()
                        if (closeableImage is CloseableAnimatedImage) {
                            val animatedImageResult: AnimatedImageResult? =
                                closeableImage.imageResult
                            if (animatedImageResult?.image != null) {
                                val imageWidth: Int = animatedImageResult.image.width
                                val imageHeight: Int = animatedImageResult.image.height
                                val bitmapConfig = Bitmap.Config.ARGB_8888
                                val bitmap =
                                    Bitmap.createBitmap(imageWidth, imageHeight, bitmapConfig)
                                animatedImageResult.image.getFrame(0)
                                    .renderFrame(imageWidth, imageHeight, bitmap)
                                loadImageResult?.onResult(bitmap)
                            }
                        } else if (closeableImage is CloseableBitmap) {
                            val bitmap = closeableImage.underlyingBitmap
                            if (bitmap != null && !bitmap.isRecycled) {
                                // https://github.com/facebook/fresco/issues/648
                                val tempBitmap = bitmap.copy(bitmap.config, false)
                                loadImageResult?.onResult(tempBitmap)
                            }
                        }
                    } finally {
                        imageReference.close()
                        closeableReference.close()
                    }
                }
            }

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                loadImageResult?.onResult(null)
                val throwable = dataSource.failureCause
                if (throwable != null) {
                    Log.e("ImageLoader", "onFailureImpl = $throwable")
                }
            }
        }, executor)
    }

    /**
     * 从网络下载图片
     * 1、根据提供的图片URL，获取图片数据流
     * 2、将得到的数据流写入指定路径的本地文件
     *
     * @param url            URL
     * @param loadFileResult LoadFileResult
     */
    fun downloadImage(context: Context?, url: String?, loadFileResult: IDownloadResult?) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        val uri = Uri.parse(url)
        val imagePipeline = Fresco.getImagePipeline()
        val builder = ImageRequestBuilder.newBuilderWithSource(uri)
        val imageRequest = builder.build()

        // 获取未解码的图片数据
        val dataSource = imagePipeline.fetchEncodedImage(imageRequest, context)
        dataSource.subscribe(object : BaseDataSubscriber<CloseableReference<PooledByteBuffer?>?>() {
            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<PooledByteBuffer?>?>) {
                if (!dataSource.isFinished || loadFileResult == null) {
                    return
                }
                val imageReference = dataSource.result
                if (imageReference != null) {
                    val closeableReference = imageReference.clone()
                    try {
                        val pooledByteBuffer = closeableReference.get()
                        val inputStream: InputStream = PooledByteBufferInputStream(pooledByteBuffer)
                        val photoPath: String = loadFileResult.getFilePath()
                        val data = read(inputStream)
                        write(photoPath, data)
                        loadFileResult.onResult(photoPath)
                    } catch (e: IOException) {
                        loadFileResult.onResult(null)
                        e.printStackTrace()
                    } finally {
                        imageReference.close()
                        closeableReference.close()
                    }
                }
            }

            override fun onProgressUpdate(dataSource: DataSource<CloseableReference<PooledByteBuffer?>?>) {
                val progress = (dataSource.progress * 100).toInt()
                loadFileResult?.onProgress(progress)
            }

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<PooledByteBuffer?>?>) {
                loadFileResult?.onResult(null)
                val throwable = dataSource.failureCause
                if (throwable != null) {
                    Log.e("ImageLoader", "onFailureImpl = $throwable")
                }
            }
        }, Executors.newSingleThreadExecutor())
    }

    /**
     * 从本地文件或网络获取Bitmap
     *
     * @param context
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @param loadImageResult
     */
    fun loadImage(
        context: Context?,
        url: String?,
        reqWidth: Int,
        reqHeight: Int,
        loadImageResult: IResult<Bitmap?>?
    ) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        var uri = Uri.parse(url)
        if (!UriUtil.isNetworkUri(uri)) {
            uri = Uri.Builder()
                .scheme(UriUtil.LOCAL_FILE_SCHEME)
                .path(url)
                .build()
        }
        val imagePipeline = Fresco.getImagePipeline()
        val imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri)
        if (reqWidth > 0 && reqHeight > 0) {
            imageRequestBuilder.resizeOptions = ResizeOptions(reqWidth, reqHeight)
        }
        val imageRequest = imageRequestBuilder.build()

        // 获取已解码的图片，返回的是Bitmap
        val dataSource = imagePipeline.fetchDecodedImage(imageRequest, context)
        dataSource.subscribe(object : BaseDataSubscriber<CloseableReference<CloseableImage>?>() {
            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                if (!dataSource.isFinished) {
                    return
                }
                val imageReference = dataSource.result
                if (imageReference != null) {
                    val closeableReference = imageReference.clone()
                    try {
                        val closeableImage = closeableReference.get()
                        if (closeableImage is CloseableAnimatedImage) {
                            val animatedImageResult: AnimatedImageResult? =
                                closeableImage.imageResult
                            if (animatedImageResult?.image != null) {
                                val imageWidth: Int = animatedImageResult.image.width
                                val imageHeight: Int = animatedImageResult.image.height
                                val bitmapConfig = Bitmap.Config.ARGB_8888
                                val bitmap =
                                    Bitmap.createBitmap(imageWidth, imageHeight, bitmapConfig)
                                animatedImageResult.image.getFrame(0)
                                    .renderFrame(imageWidth, imageHeight, bitmap)
                                loadImageResult?.onResult(bitmap)
                            }
                        } else if (closeableImage is CloseableBitmap) {
                            val bitmap = closeableImage.underlyingBitmap
                            if (bitmap != null && !bitmap.isRecycled) {
                                // https://github.com/facebook/fresco/issues/648
                                val tempBitmap = bitmap.copy(bitmap.config, false)
                                loadImageResult?.onResult(tempBitmap)
                            }
                        }
                    } finally {
                        imageReference.close()
                        closeableReference.close()
                    }
                }
            }

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                loadImageResult?.onResult(null)
                val throwable = dataSource.failureCause
                if (throwable != null) {
                    Log.e("ImageLoader", "onFailureImpl = $throwable")
                }
            }
        }, UiThreadImmediateExecutorService.getInstance())
    }

    /**
     * 从本地缓存文件中获取Bitmap
     *
     * @param url
     * @param loadImageResult
     */
    fun loadLocalDiskCache(context: Context?, url: String?, loadImageResult: IResult<Bitmap?>?) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build()
        val imagePipeline = Fresco.getImagePipeline()

        // 获取已解码的图片，返回的是Bitmap
        val dataSource = imagePipeline.fetchDecodedImage(
            imageRequest, context,
            ImageRequest.RequestLevel.DISK_CACHE
        )
        dataSource.subscribe(object : BaseDataSubscriber<CloseableReference<CloseableImage>?>() {
            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                if (!dataSource.isFinished) {
                    return
                }
                val imageReference = dataSource.result
                if (imageReference != null) {
                    val closeableReference = imageReference.clone()
                    try {
                        val closeableImage = closeableReference.get()
                        if (closeableImage is CloseableAnimatedImage) {
                            val animatedImageResult: AnimatedImageResult? =
                                closeableImage.imageResult
                            if (animatedImageResult?.image != null) {
                                val imageWidth: Int = animatedImageResult.image.width
                                val imageHeight: Int = animatedImageResult.image.height
                                val bitmapConfig = Bitmap.Config.ARGB_8888
                                val bitmap =
                                    Bitmap.createBitmap(imageWidth, imageHeight, bitmapConfig)
                                animatedImageResult.image.getFrame(0)
                                    .renderFrame(imageWidth, imageHeight, bitmap)
                                loadImageResult?.onResult(bitmap)
                            }
                        } else if (closeableImage is CloseableBitmap) {
                            val bitmap = closeableImage.underlyingBitmap
                            if (bitmap != null && !bitmap.isRecycled) {
                                // https://github.com/facebook/fresco/issues/648
                                val tempBitmap = bitmap.copy(bitmap.config, false)
                                loadImageResult?.onResult(tempBitmap)
                            }
                        }
                    } finally {
                        imageReference.close()
                        closeableReference.close()
                    }
                }
            }

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>?>) {
                loadImageResult?.onResult(null)
                val throwable = dataSource.failureCause
                if (throwable != null) {
                    Log.e("ImageLoader", "onFailureImpl = $throwable")
                }
            }
        }, UiThreadImmediateExecutorService.getInstance())
    }

    fun pauseRequest() {
        Fresco.getImagePipeline().pause()
    }

    fun resumeRequest() {
        Fresco.getImagePipeline().resume()
    }
}