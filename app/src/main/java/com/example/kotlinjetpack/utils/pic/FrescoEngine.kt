package com.example.kotlinjetpack.utils.pic

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.facebook.drawee.view.SimpleDraweeView
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.utils.ActivityCompatHelper


/**
 * desc: PicassoEngine
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/7 4:31 下午
 */
class FrescoEngine private constructor() : ImageEngine {

    private val TAG = "FrescoEngine==>"

    companion object {
        val instance: FrescoEngine by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FrescoEngine()
        }
    }

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       资源url
     * @param imageView 图片承载控件
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Log.e(TAG, "loadImage: ")
        if (imageView is SimpleDraweeView) {
            Log.e(TAG, "loadImage22: ")
            FrescoImageLoader.loadImage(imageView, url)
        } else {
            FrescoImageLoader.loadImage(context, url, object : IResult<Bitmap?> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        imageView.setImageBitmap(result)
                    }
                }
            })
        }

    }

    /**
     * 加载指定url并返回bitmap
     * 参考：https://www.fresco-cn.org/docs/datasources-datasubscribers.html
     *
     * @param context   上下文
     * @param url       资源url
     * @param maxWidth  资源最大加载尺寸
     * @param maxHeight 资源最大加载尺寸
     * @param call      回调接口
     */
    override fun loadImageBitmap(
        context: Context,
        url: String,
        maxWidth: Int,
        maxHeight: Int,
        call: OnCallbackListener<Bitmap>?
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Log.e(TAG, "loadImageBitmap: ")
        FrescoImageLoader.loadImage(context, url, maxWidth, maxHeight, object : IResult<Bitmap?> {
            override fun onResult(result: Bitmap?) {
                call?.onCall(result)
            }
        })
    }

    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Log.e(TAG, "loadAlbumCover: ")
        if (imageView is SimpleDraweeView) {
            Log.e(TAG, "loadAlbumCover22: ")
            FrescoImageLoader.loadImage(imageView, url)
        } else {
            FrescoImageLoader.loadImage(context, url, object : IResult<Bitmap?> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        imageView.setImageBitmap(result)
                    }
                }
            })
        }
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Log.e(TAG, "loadGridImage: ")
        if (imageView is SimpleDraweeView) {
            Log.e(TAG, "loadGridImage22: ")
            FrescoImageLoader.loadImage(imageView, url)
        } else {
            FrescoImageLoader.loadImage(context, url, 100, 100, object : IResult<Bitmap?> {
                override fun onResult(result: Bitmap?) {
                    result?.let {
                        imageView.setImageBitmap(result)
                    }
                }
            })
        }

    }

    override fun pauseRequests(context: Context?) {
        FrescoImageLoader.pauseRequest()
    }

    override fun resumeRequests(context: Context?) {
        FrescoImageLoader.resumeRequest()
    }

}