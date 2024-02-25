package com.example.kotlinjetpack

/**
 * desc: CommonTest
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/12/24 16:42
 */

fun main() {
    val a:Int = 0x01
    val b:Int = 0x06
    val c:Int = 0x0A
    println("是否相等1：${a == 1}")
    println("是否相等2：${b == 6}")
    println("是否相等3：${b == 0x06}")
    println("是否相等4：${c == 0x0A}")
    println("是否相等5：${c == 10}")
}