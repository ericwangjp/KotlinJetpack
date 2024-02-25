package com.example.kotlinjetpack.activity.flowCoroutine

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinjetpack.adapter.CommonRcvAdapter
import com.example.kotlinjetpack.databinding.ActivityFlowLearnBinding
import com.example.kotlinjetpack.model.FlowLearnViewModel
import com.example.kotlinjetpack.model.FlowMainViewModel
import com.example.kotlinjetpack.utils.addOnItemVisibilityChangeListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext

class FlowLearnActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityFlowLearnBinding
    private val flowLearnViewModel by viewModels<FlowLearnViewModel>()
    private val flowMainViewModel by viewModels<FlowMainViewModel>()
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        // 创建 CoroutineScope （用于管理CoroutineScope中的所有携程）
        coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        job = Job()
        binding.btnStart.setOnClickListener {
//            lifecycleScope.launch {
////            lifecycleScope.launchWhenStarted {
//                repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    flowLearnViewModel.timeFlow.collect { time ->
////                flowLearnViewModel.timeFlow.collectLatest { time ->
//                        binding.tvContent.text = time.toString()
////                    delay(3000)
//                        Log.e("FlowLearnActivity", "Update time $time in UI.")
//                    }
//                }
//            }

            flowMainViewModel.startTimer()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                flowMainViewModel.stateFlow.collect {
                flowLearnViewModel.stateFlow.collect {
                    binding.tvContent.text = it.toString()
                    Log.e("stateFlow collect", "Update time $it in UI.")
                }
            }
        }


//        binding.rcvList.apply {
//            layoutManager = LinearLayoutManager(this@FlowLearnActivity)
//            var data = arrayListOf<String>()
//            for (i in 0..20) {
//                data.add("第${i}个")
//            }
//            adapter = CommonRcvAdapter(data)
//
//            addOnItemVisibilityChangeListener { itemView, adapterIndex, isVisible ->
//                Log.e("可见item: ", "===》$adapterIndex -- $isVisible")
//            }
//        }

        asyncCoroutine()
        asyncCoroutine2()

    }

    private fun asyncCoroutine() {
        coroutineScope.launch(Dispatchers.Main) {
            val firstData = async(Dispatchers.IO) {
                Log.e("asyncCoroutine: ", "当前线程1：${Thread.currentThread().name}")
                delay(1000)
                3
            }
            val secondData = async {
                Log.e("asyncCoroutine: ", "当前线程2：${Thread.currentThread().name}")
                delay(1000)
                5
            }
            val resultData = firstData.await() + secondData.await()
            Log.e("asyncCoroutine: ", "结果 : $resultData")
        }
    }

    private fun asyncCoroutine2() {
        job = launch(Dispatchers.Main) {
            val firstData = async(Dispatchers.IO) {
                Log.e("asyncCoroutine2: ", "当前线程1：${Thread.currentThread().name}")
                delay(1000)
                3
            }
            val secondData = async {
                Log.e("asyncCoroutine2: ", "当前线程2：${Thread.currentThread().name}")
                delay(1000)
                5
            }
            val resultData = firstData.await() + secondData.await()
            Log.e("asyncCoroutine2: ", "结果 : $resultData")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 当 Activity 销毁的时候取消该 Scope 管理的所有协程
        coroutineScope.cancel()
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

}