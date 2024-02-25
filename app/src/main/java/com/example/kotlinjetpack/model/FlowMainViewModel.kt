package com.example.kotlinjetpack.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.concurrent.timer

/**
 * desc: FlowMainViewModel
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/1/15 11:34 上午
 */
class FlowMainViewModel : ViewModel() {
    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    fun startTimer() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                _stateFlow.value += 1
            }
        }, 0, 1000)
    }
}