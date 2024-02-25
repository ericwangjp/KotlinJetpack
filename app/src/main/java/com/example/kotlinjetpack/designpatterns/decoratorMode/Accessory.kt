package com.example.kotlinjetpack.designpatterns.decoratorMode

/**
 * desc: 装饰者模式
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/9/14 16:13
 */
interface Accessory {
    fun name(): String
    fun cost(): Int
    fun type(): String
}