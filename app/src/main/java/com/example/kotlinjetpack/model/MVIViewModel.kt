package com.example.kotlinjetpack.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * desc: MVIViewModel
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/9/21 18:02
 */
class MVIViewModel : ViewModel() {
    private val _btnLiveData = MutableLiveData<ButtonDataState>()
    val btnLiveData: LiveData<ButtonDataState> = _btnLiveData
    fun updateBtnState(state: ButtonDataState) {
        _btnLiveData.value = state
    }
}

data class ButtonDataState(val title: String)