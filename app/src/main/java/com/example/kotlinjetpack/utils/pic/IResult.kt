package com.example.kotlinjetpack.utils.pic

/**
 * desc: IResult
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/7 7:51 下午
 */
interface IResult<T> {
    fun onResult(result: T)
}