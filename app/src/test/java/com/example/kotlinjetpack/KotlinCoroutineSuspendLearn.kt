package com.example.kotlinjetpack

import kotlinx.coroutines.*
import kotlin.Result
import kotlin.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * desc: KotlinCoroutineSuspendLearn
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/3/18 16:48
 */
fun main() {
//    demo1()
//    demo2()
//    demo3()
//    demo4()
//    demo5()
    demo6()
}

fun demo6() {

}

fun demo5() {
    runBlocking {
        val job1 = GlobalScope.launch {
            try {
                delay(1000)
                printThreadDetail("job 1")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                withContext(NonCancellable) {
                    delay(1000)
                    printThreadDetail("job finish")
                }
            }
        }
        job1.cancel()
        delay(5000)
    }
}

fun demo4() {
    runBlocking {
        launch {
            printThreadDetail("1")
            yield()
            printThreadDetail("2")
        }
        launch {
            printThreadDetail("3")
            yield()
            printThreadDetail("4")
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun demo3() {
    val result = GlobalScope.launch {
        printThreadDetail("开始")
        withContext(Dispatchers.Default) {
            delay(2000)
            "返回值"
        }
    }
    printThreadDetail("结果：$result")
}

@OptIn(DelicateCoroutinesApi::class)
fun demo2() {
    printThreadDetail("开始")
    runBlocking {
        GlobalScope.launch(Dispatchers.Default) {
            printThreadDetail("我是计算密集型任务")
        }
        GlobalScope.launch {
            printThreadDetail("我是默认任务")
            withContext(Dispatchers.Default) {
                printThreadDetail("default 任务")
            }
//            Thread.sleep(2000)
            printThreadDetail("任务执行结束")
        }
    }
    printThreadDetail("结束")
}

fun printThreadDetail(string: String?) {
    println("输出：【$string】 当前线程：${Thread.currentThread().name}")
}

fun demo1() {
    val useTime = measureTimeMillis {
        runBlocking {
            println("main start")
            launch {
                println("launch 1")
            }
            launch {
                println("launch 2")
            }
            println("main end")
        }
    }
    println("总耗时：$useTime 毫秒")



    fun <T> launchFinish(block: suspend () -> T) {
        val coroutine = block.createCoroutine(object : Continuation<T> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resumeWith(result: Result<T>) {
                println("result:$result")
            }
        })
        coroutine.resume(Unit)
    }


    launchFinish {
        println("customer coroutine")
    }
}
