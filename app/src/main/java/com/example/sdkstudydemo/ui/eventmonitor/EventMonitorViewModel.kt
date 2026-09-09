package com.example.sdkstudydemo.ui.eventmonitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventMonitorViewModel: ViewModel() {
    private val _uiState= MutableStateFlow<EventMonitorUiState>(
        EventMonitorUiState.Idle
    )
    val uiState = _uiState.asStateFlow()
    fun upload(eventName:String){
        viewModelScope.launch {
            // 1. 开始上传
            _uiState.value =
                EventMonitorUiState.Loading

            // 2. 暂时模拟网络请求
            delay(1500)

            // 3. 为了方便测试：
            // 输入 fail 就模拟失败
            if (eventName.equals(
                    "fail",
                    ignoreCase = true
                )
            ) {

                _uiState.value =
                    EventMonitorUiState.Error(
                        "模拟上传失败"
                    )

            } else {

                _uiState.value =
                    EventMonitorUiState.Success(
                        "$eventName 上传成功"
                    )
            }
        }
    }
}