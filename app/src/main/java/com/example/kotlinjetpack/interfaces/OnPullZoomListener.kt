package com.example.kotlinjetpack.interfaces

/**
 * desc: CheckListener
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/5 19:47
 */
interface OnPullZoomListener {
    fun onPullZooming(newScrollValue: Int)
    fun onPullZoomEnd()
}