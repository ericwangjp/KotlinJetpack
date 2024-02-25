package com.example.kotlinjetpack.utils

import android.view.View

/**
 * desc: ViewWrapper
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/5/31 16:40
 */
class ViewWrapper(private var targetView: View) {
    fun getWidth() = targetView.layoutParams.width
    fun setWidth(width: Int){
        targetView.layoutParams.width = width
        targetView.requestLayout()
    }
    fun getHeight() = targetView.layoutParams.height
    fun setHeight(height: Int){
        targetView.layoutParams.height = height
        targetView.requestLayout()
    }
}