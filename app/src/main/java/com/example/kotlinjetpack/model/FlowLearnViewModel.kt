package com.example.kotlinjetpack.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

/**
 * desc: FlowLearnViewModel
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/10/29 4:43 下午
 */
class FlowLearnViewModel : ViewModel() {
    private val timeFlow = flow {
        var time = 0
        while (time < 10) {
            emit(time)
            delay(1000)
            time++
        }
    }

    val stateFlow = timeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    override fun onCleared() {
        super.onCleared()
    }
}