package com.example.kotlinjetpack.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * desc: KotlinFlow
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/12/4 3:13 下午
 */
fun main() {
    runBlocking {
//        val lFlow = flowOf(1, 2, 3, 4, 5)
//        lFlow.map {
//            it * it
//        }.collect {
//            println(it)
//        }

//        lFlow.filter {
//            it % 2 == 0
//        }.onEach {
//            println(it)
//        }.map {
//            it * it
//        }.collect {
//            println(it)
//        }

//        flow {
//            emit(1)
//            emit(2)
//            delay(600)
//            emit(3)
//            delay(100)
//            emit(4)
//            delay(100)
//            emit(5)
//        }.debounce(500).collect {
//            println(it)
//        }

//        flow {
//            while (true) {
//                emit("发送一条弹幕")
//            }
//        }.sample(1000).flowOn(Dispatchers.IO).collect {
//            println(it)
//        }

//        val result = flow {
//            for (i in 1..100) {
//                emit(i)
//            }
//        }.reduce { acc, value -> acc + value }
//        println(result)

//        val result = flow {
//            for (i in 'A'..'Z') {
//                emit(i.toString())
//            }
//        }.fold("Alphabet：") { acc, value -> acc + value }
//        println(result)

//        flowOf(1, 2, 3).flatMapConcat {
//            println(it)
//            flowOf("a$it", "b$it")
//        }.collect {
//            println(it)
//        }

//        flowOf(300,200,100)
//            .flatMapMerge {
////            .flatMapConcat {
//            flow {
//                kotlinx.coroutines.delay(it.toLong())
//                emit("a$it")
//                emit("b$it")
//            }
//        }.collect{
//            println(it)
//        }

//        flow {
//            emit(1)
//            kotlinx.coroutines.delay(150)
//            emit(2)
//            kotlinx.coroutines.delay(50)
//            emit(3)
//        }.flatMapLatest {
//            flow {
//                kotlinx.coroutines.delay(100)
//                emit("$it")
//            }
//        }.collect {
//            println(it)
//        }

//        val flow1 = flowOf("a", "b", "c")
//        val flow2 = flowOf(1, 2, 3, 4, 5)
//        flow1.zip(flow2) { a, b ->
//            a + b
//        }.collect {
//            println(it)
//        }

//        flow {
//            emit(1)
//            kotlinx.coroutines.delay(1000)
//            emit(2)
//            kotlinx.coroutines.delay(1000)
//            emit(3)
//        }.onEach {
//            println("$it is ready")
//        }.buffer()
//            .collect {
//                delay(1000)
//                println("$it is handled")
//            }

        flow {
            var count = 0
            while (count<10) {
                emit(count)
                delay(1000)
                count++
            }
        }
            .conflate().collect {
//            .collectLatest {
                println("开始处理：$it")
                delay(2000)
                println("处理完成：$it")
            }

    }


}


