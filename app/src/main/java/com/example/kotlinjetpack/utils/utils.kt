package com.example.kotlinjetpack.utils

import android.content.Context

/**
 * desc: utils$
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/25$ 9:40 下午$
 */
fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density;
    return (dpValue * scale + 0.5f).toInt()
}

fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density;
    return (pxValue / scale + 0.5f).toInt()
}