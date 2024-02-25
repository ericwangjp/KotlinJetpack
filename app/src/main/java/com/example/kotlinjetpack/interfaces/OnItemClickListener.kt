package com.example.kotlinjetpack.interfaces

import android.view.View

/**
 * desc: OnItemClickListener
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/5 19:49
 */
interface OnItemClickListener {
    fun onItemClick(view: View?, position: Int)
    fun onItemLongClick(view: View?, position: Int)
}