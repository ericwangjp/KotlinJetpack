package com.example.kotlinjetpack.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * desc: LifecycleObserverManager
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/12/6 11:13 上午
 */
class LifecycleObserverManager : DefaultLifecycleObserver {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        stopScope()
        coroutineScope.launch {
            // 执行生命周期任务
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        stopScope()
    }

    private fun stopScope() {
        if (coroutineScope.isActive){
            coroutineScope.cancel()
        }
    }
}