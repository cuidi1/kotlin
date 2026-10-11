package com.example.sdkstudydemo.ui.eventmonitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sdkstudydemo.network.RetrofitProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class EventMonitorViewModel: ViewModel() {
    //一个事件状态的stateflow
    private val _uiState= MutableStateFlow<EventMonitorUiState>(
        EventMonitorUiState.Idle
    )
    val uiState = _uiState.asStateFlow()

    //注意和上述声明区分开来，一个事件列表的stateflow
    private val _eventLogs = MutableStateFlow<List<EventLogItem>>(emptyList())

    val eventLogs = _eventLogs.asStateFlow()
    fun upload(eventName:String){
        viewModelScope.launch {
            // 1. 开始上传
            _uiState.value =
                EventMonitorUiState.Loading

           try {
               val todo = RetrofitProvider.todoApi.getTodo()
               _uiState.value = EventMonitorUiState.Success(
                   "$eventName 请求成功：${todo.title}"
               )

               addEventLog(
                   eventName = eventName,
                   result = "成功：${todo.title}"
               )
           }catch (e: Exception) {
               _uiState.value =
                   EventMonitorUiState.Error(
                       "请求失败：${e.message}"
                   )

               addEventLog(
                   eventName = eventName,
                   result = "失败：${e.message}"
               )
           }
        }
    }

    private fun addEventLog(
        eventName: String,
        result: String
    ) {
        val newItem = EventLogItem(id = System.currentTimeMillis(), eventName = eventName, result = result)
        _eventLogs.value = _eventLogs.value + newItem
    }
}