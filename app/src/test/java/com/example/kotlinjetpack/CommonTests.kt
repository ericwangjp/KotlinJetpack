package com.example.kotlinjetpack

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * desc: CommonTests
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/8/31 09:49
 */
fun main() {
    // 泛型擦除
    val c1 = ArrayList<Int>().javaClass
    val c2 = ArrayList<String>().javaClass
    println(c1 == c2)
}

inline fun <reified C:Activity> Context.startActivity(){
    startActivity(Intent(this,C::class.java))
}