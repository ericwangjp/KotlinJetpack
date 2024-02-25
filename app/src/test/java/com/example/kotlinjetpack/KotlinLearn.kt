package com.example.kotlinjetpack

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.random.Random

fun test1() {
    GlobalScope.launch {
        println("GlobalScope 当前线程：${Thread.currentThread().name}")
        val arg1 = suspendF1()
        val arg2 = suspendF2()
        println("suspend finish , result1= $arg1, result2= $arg2")
    }
}

private suspend fun suspendF1(): Int {
    println("suspendF1 当前线程：${Thread.currentThread().name}")
    delay(1000)
    println("suspend fun 1")
    return 1
}

private suspend fun suspendF2(): Int {
    println("suspendF2 当前线程：${Thread.currentThread().name}")
    delay(1000)
    println("suspend fun 2")
    return 2
}

//val mScope = MainScope()
//val job1 = mScope.launch {
//    println("job1 当前线程：${Thread.currentThread().name}")
//    println("job1 开始")
//    delay(1000)
//    println("job1 结束")
//}
//val job2 = mScope.launch(Dispatchers.IO) {
//    println("job2 当前线程：${Thread.currentThread().name}")
//    println("job2 开始")
//    delay(1000)
//    println("job2 结束,开始切换线程")
//
//    withContext(Dispatchers.Main) {
//        println("job2 切换线程后：${Thread.currentThread().name}")
//    }
//}
//val a = mScope.launch {
//    val b = async(Dispatchers.IO) {
//        delay(200)
//        "kotlin"
//    }
//    val c = b.await()
//}

private fun supervisorScopeFun() {
    println("====aaa")
    val scope = CoroutineScope(Job() + Dispatchers.Default)
    scope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
        println("CoroutineExceptionHandler：$throwable")
    }) {
        launch {
            println("coroutineScope1：${Thread.currentThread().name}")
            Log.e("supervisorScopeFun: ", "==> ")
        }
        supervisorScope {
            println("==>${coroutineContext[CoroutineName]}")
            launch {
                delay(500)
                println("supervisorScope1：${Thread.currentThread().name}")
            }

            launch {
                delay(500)
                println("supervisorScope2：${Thread.currentThread().name}")
                throw RuntimeException("=RuntimeException=")
            }

            launch {
                delay(500)
                println("supervisorScope2：${Thread.currentThread().name}")
            }
        }
    }
    println("是否执行了：")
}


suspend fun async1() {
//    CoroutineScope(Dispatchers.IO).launch { }
    coroutineScope {
        launch {
            println("协程开始执行--》${Thread.currentThread().name}")
            val job1 = async {
                delay(2000)
                "1"
            }
            val job2 = async {
                delay(3000)
                "2"
            }
            val job3 = async {
                delay(1000)
                "3"
            }
            val job4 = async {
                delay(4000)
                "4"
            }
            val job5 = async {
                delay(2000)
                "5"
            }

            println("执行结果：${job1.await()} - ${job2.await()} - ${job3.await()} - ${job4.await()} - ${job5.await()}")
        }
    }
}

//val testFlow = flow<String> {
//    emit("hello")
//    emit("world")
//}
//val scope = CoroutineScope(Dispatchers.Default)

//suspend fun fetchTwoDocs() = // called on any Dispatcher (any thread, possibly Main)
//    coroutineScope {
//        val deferreds = listOf( // fetch two docs at the same time
//            async { fetchDoc(1) }, // async returns a result for the first doc
//            async { fetchDoc(2) } // async returns a result for the second doc
//        )
//        deferreds.awaitAll() // use awaitAll to wait for both network requests
//    }

fun uploadMultipleFile(
    coroutineScope: CoroutineScope,
    fileList: ArrayList<Int>,
    keyList: ArrayList<String>? = null,
    token: String,
    onStart: (() -> Unit)? = null,
    onComplete: ((key: ArrayList<String>?, infoLists: ArrayList<Int>) -> Unit),
) {
    onStart?.invoke()
    val jobs = arrayListOf<Deferred<Int>>()
    jobs.clear()
//    val respInfoList = arrayListOf<Int>()
//    respInfoList.clear()
    coroutineScope.launch {
        for (i in 0 until fileList.size) {
            val job = async {
                delay(Random.nextLong(1000, 5000))
//                respInfoList.add(10 + i)
                10 + i
            }
            jobs.add(job)
        }
        val list = awaitAll(*jobs.toTypedArray())
        onComplete.invoke(keyList, list as ArrayList<Int>)
    }
}

val testFlow1 = flow<String> {
    emit("哈哈")
    emit("你好")
}

enum class Result(val type: String) {
    Loading("loading"), Success("success")
}

val uiState = MutableStateFlow(Result.Loading)
val singleEvent = MutableStateFlow(1)
val operator = flow<String> {
    emit("你好")
    emitAll(flowOf("哈哈", "真棒"))
    emitAll(listOf("1", "2", "3").asFlow())
}.onStart {
    emit("要开始发送数据了")
}

val channelFlow = channelFlow<Int> {
    send(123)
    println("curThread: ${Thread.currentThread().name}")
    withContext(Dispatchers.IO) {
        send(456)
        println("curThread: ${Thread.currentThread().name}")
    }
}

val flowEach = flowOf(1, 2, 3).onEach {
    println("每个值过滤：$it")
}.onEmpty {
    println("什么都没有")
}

/**
 * onSubscription SharedFlow 专属操作符
 *  和 onStart 有些区别 ，SharedFlow 是热流，因此如果在onStart里发送值，则下游可能接收不到。
 */
val shareFlow = MutableSharedFlow<String>().onSubscription {
    emit("onSubscription")
}.onStart {
    emit("onStart")
}

data class Person(val name: String, val age: Int)


// 函数相关
val strLenFun: (String) -> Int = { input -> input.length }
fun strLenFun1(input: String) = input.length
val strlen = strLenFun("android")

// 高阶函数
fun stringMapper(input: String, mapper: (String) -> Int) = mapper(input)

// 具名函数参数
// stringMapper("Aa",::strLenFun1)
fun stringMapperFun(input: String): (String) -> Int {
    return {
        println("it:$it")
        println("input:$input")
        it.length + input.length
    }
}

fun String.lastChar(): Char = this[length - 1]


fun main() {
    println("1==>${stringMapperFun("aaa")}")
    println("2==>${stringMapperFun("aaa").invoke("bb")}")
    println("3==>${stringMapperFun("aaa")("ccc")}")

//    startCoroutine()

    runBlocking {

//        launch {
//            flow {
//                repeat(30) {
//                    delay(100)
//                    emit(it)
//                }
//            }.conflate().onEach { delay(1000) }.flowOn(Dispatchers.IO).collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flowOf("A", "B", "C")
//                .onEach  { println("1$it") }
//                .buffer()  // <--------------- buffer between onEach and collect
//                .collect { println("2$it") }
//        }

//        launch {
//            flow<Int> {
//                println("start")
//                throw IOException("")
//            }. retry (3){ e->
//                e is IOException
//            }.collect()
//
//        flow<Int> {
//            println("start")
//            throw IOException("")
//        }.retry(3).collect()
//        }

//        launch {
//            flow<Int> {
//                println("doing")
//                throw IOException("")
//            } .retryWhen { cause,attempt->
//                if(attempt > 4){
//                    return@retryWhen false
//                }
//                cause is IOException
//            }
//        }

//        launch {
//            val job = flowOf(1, 3, 5, 7).cancellable().onEach { value ->
//                println(value)
//            }.launchIn(this)
//
//            //取消
//            job.cancel()
//
//        }

//        launch {
//            val flow = flowOf(1, 2, 3).onEach { delay(10) }
//
//            val flow2 = flowOf("a", "b", "c", "d").onEach { delay(15) }
//
//            flow.zip(flow2) { i, s -> i.toString() + s }.collect {
//                println(it)
//            }
//
//        }

//        launch {
//            flowOf("a", "b", "c", "d", "e", "f").flatMapMerge(3) { value ->
//                flow {
//                    emit(value + 1)
//                }.flowOn(Dispatchers.IO)
//            }.collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                delay(100)
//                emit("b")
//            }.flatMapLatest { value ->
//                flow {
//                    emit(value)
//                    delay(200)
//                    emit(value + "_last")
//                }
//            }.collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flowOf(1, 2, 3).flatMapConcat {
//                flowOf("$it map")
//            } .collect { value ->
//                println (value)
//            }
//        }

//        launch {
//            flow {
//                emit(flowOf(1, 2, 3).flowOn(Dispatchers.IO))
//                emit(flowOf(4, 5, 6).flowOn(Dispatchers.IO))
//                emit(flowOf(7, 8, 9).flowOn(Dispatchers.IO))
//            }.flattenMerge(3).collect { value->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit(flowOf(1, 2, 3))
//                emit(flowOf(4, 5, 6))
//            } .flattenConcat().collect { value->
//                println(value)
//            }
//        }

//        launch {
//            val numberFlow = flowOf(1, 2).onEach { delay(10) }
//            val stringFlow = flowOf("a", "b", "c").onEach { delay(15) }
//
//            listOf(numberFlow, stringFlow).merge()
//                .collect { value ->
//                    println(value)
//                }
//        }

//        launch {
//            val numberFlow = flowOf(1, 2).onEach { delay(10) }
//            val stringFlow = flowOf("a", "b", "c").onEach { delay(15) }
//
//            numberFlow.combineTransform(stringFlow) { number, string ->
//                emit("$number : $string")
//            }.collect { value ->
//                println( value )
//            }
//        }

//        launch {
//            val flow = flowOf(1, 2).onEach { delay(5) }
//            val flow2 = flowOf("a", "b", "c").onEach { delay(15) }
//            flow.combine(flow2) { i, s -> i.toString() + s } .collect {
//                println(it) // Will print "1a 2a 2b 2c"
//            }
//        }

//        launch {
//            flowOf(1, 1, 3, 1).distinctUntilChanged()
//                .collect { value ->
//                    println(value)
//                }
//        }

//        launch {
//            flowOf(
//                Person(name = "Tom", age = 8),
//                Person(name = "Tom", age = 12),
//                Person(name = "Tom", age = 12),
//                Person(name = "jack", age = 9)
//            ).distinctUntilChangedBy { it.name }.collect { value ->
//                println(value.toString())
//            }
//        }

//        launch {
//            flow {
//                repeat(10) {
//                    emit(it)
//                    delay(110)
//                }
//            }.sample(200).collect{
//                println(it)
//            }
//        }

//        launch {
//            flow {
//                emit(1)
//                delay(90)
//                emit(2)
//                delay(100)
//                emit(3)
//                delay(80)
//                emit(4)
//                delay(100)
//                emit(5)
//            }.debounce(200).collect(){
//                println(it)
//            }
//        }

//        launch {
//            flow {
//                emit(1)
//                emit(2)
//                emit(3) //从此项开始不满足条件
//                emit(4)
//                emit(1)
//            } .takeWhile { it <3  } .collect { value ->
//                println(value)
//            }
//
//            flow {
//                emit(3)  //从此项开始不满足条件
//                emit(1)
//                emit(2)
//                emit(4)
//            } .takeWhile { it <3  }
//                .onEmpty {
//                print( "empty")
//            }
//                .collect { value ->
//                print(value)
//            }
//        }

//        launch {
//            flow {
//                emit(1)
//                emit(2)
//                emit(3)
//            } .take(2) .collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit(3)
//                emit(1) //从此项开始不满足条件
//                emit(2)
//                emit(3)
//                emit(4)
//            }.dropWhile { it == 3 }.collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit(1)
//                emit(2)
//                emit(3)
//            }.drop(2).collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                emit(null)
//                emit("b")
//            }.filterNotNull().collect { value->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                emit("b")
//                emit("c")
//            }.filterNot { it == "a" }.collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                emit("b")
//                emit(1)
//            }.filterIsInstance<String>().collect { value->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit(1)
//                emit(2)
//                emit(3)
//            }.filter { value ->
//                value >= 2
//            }.collect { value ->
//                println(value)
//            }
//        }

//        launch {
//            flowOf(1, 2, 3).produceIn(this)
//                .consumeEach { value ->
//                    println(value)
//                }
//
//        }

//        launch {
//            flowOf(1, 2, 3).scan(0) { acc, value ->
//                acc + value
//            }.collect {
//                println(it)
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                emit("b")
//            }.withIndex().collect() {
//                println("${it.index} - ${it.value}")
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                emit("b")
//                emit("c")
//            } .transformWhile { value ->
//                emit(value)
////                true
//                value == "a"
//            } .collect { value->
//                println(value)
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                delay(100)
//                emit("b")
//            }.transformLatest { value ->
//                emit(value)
//                delay(200)
//                emit(value + "_last")
//            }.collect {value->
//                println(value)
//            }
//
//        }

//        launch {
//            flow {
//                emit(1)
//                emit(2)
//            } .transform { value ->
//                if (value == 1) {
//                    emit("value :$value*2")
//                }
//                emit("transform :$value")
//            }.collect { value->
//                println(value)
//            }
//        }


//        launch {
//            flow {
//                emit("a")
//                emit("b")
//            } .mapNotNull { value ->
//                if (value != "a") {
//                    value
//                } else {
//                    null
//                }
//            }.collect { value ->
//                print(value)
//            }
//        }

//        launch {
//            flow {
//                emit("a")
//                delay(100)
//                emit("b")
//            }.mapLatest { value ->
//                println("Started computing $value")
//                delay(200)
//                "Computed $value"
//            }.collect {value->
//                print(value)
//            }
//
//        }

//        launch {
//            flow<Int> {
//                emit(1)
//                emit(2)
//            }.map {
//                it * it
//            }.collect(){
//                println(it)
//            }
//        }

//        launch {
//            shareFlow.collect(){
//                println(it)
//            }
//        }

//        flowEach.collect() {
//            println(it)
//        }

//        launch {
//            val sum = flowOf(2, 3, 4).fold(1) { result, value ->
//                result + value
//            }
//            println("result->$sum")
//
//            val sum2 = flowOf(2,3,4).reduce{result, value ->
//                result + value
//            }
//            println("result2->$sum2")
//        }

//        channelFlow.collect(){
//            println(it)
//        }

//        operator.collect{
//            println(it)
//        }

//        launch {
//            singleEvent.collect{
//                println(it)
//            }
//        }
//        singleEvent.tryEmit(12)
//        singleEvent.tryEmit(22)

//        launch {
//            uiState.collect{
//                println(it)
//                println("--1-- ${System.currentTimeMillis()}")
//            }
//        }
////        delay(100)
//        println("--2-- ${System.currentTimeMillis()}")
//        uiState.value = Result.Success


//        launch {
//            testFlow1.collect {
//                println(it)
//            }
//        }

//        uploadMultipleFile(
//            this,
//            arrayListOf<Int>().apply {
//                for (i in 0..6) {
//                    add(i + 1)
//                }
//            },
//            arrayListOf<String>().apply {
//                for (i in 0..6) {
//                    add("->${i + 1}")
//                }
//            },
//            "aaa", { println("开始执行了- ${System.currentTimeMillis()}") }
//        ) { key, infoLists ->
//            key?.forEach {
//                println("收到执行结果key：${it} - ${System.currentTimeMillis()}")
//            }
//            infoLists.forEach {
//                println("收到执行结果result：${it} - ${System.currentTimeMillis()}")
//            }
//
//        }


//        val supervisor = SupervisorJob()
//        with(CoroutineScope(coroutineContext + supervisor)) {
//            val firstChild = launch(CoroutineExceptionHandler { _, _ -> }) {
//                println("First child is failing")
//                throw AssertionError("First child is cancelled")
//            }
//            val secondChild = launch {
//                firstChild.join()
//                println("First child is cancelled: ${firstChild.isCancelled}, but second one is still active")
//                try {
//                    delay(Long.MAX_VALUE)
//                } finally {
//                    println("Second child is cancelled because supervisor is cancelled")
//                }
//            }
//            firstChild.join()
//            println("Cancelling supervisor")
////取消所有协程
//            supervisor.cancel()
//            secondChild.join()
//        }

        println("----执行结束了----")
    }
}

fun EditText.textChangeFlow(): Flow<CharSequence> = callbackFlow {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            p0?.let {
//                offer(it)
                trySend(it)
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }
    addTextChangedListener(watcher)
    awaitClose {
        println("阻塞等待关闭==")
        removeTextChangedListener(watcher)
    }
}

fun startCoroutine() {
    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    uiScope.launch {
        println("当前线程：${Thread.currentThread().name}")
        updateUI()
    }
    viewModelJob.cancel()
}

suspend fun updateUI() {
    delay(1000)
    println("--》更新UI")
}


//    val coroutineContext = Job() + Dispatchers.Default + CoroutineName("customContext")
//    println("-->$coroutineContext === ${coroutineContext[CoroutineName]}")
//    val newCoroutineContext = coroutineContext.minusKey(CoroutineName)
//    println("-->$newCoroutineContext")
//    runBlocking {
//        async1()
//    }
//    test1()
//    runBlocking {
//        println("当前线程1：${Thread.currentThread().name}")
//        coroutineScope {
//            println("当前线程2：${Thread.currentThread().name}")
//            launch {
//                println("当前线程3：${Thread.currentThread().name}")
//                testFlow.collect {
//                    println("当前线程4：${Thread.currentThread().name}")
//                    println(it)
//                }
//            }
//        }
//    }
