package com.example.sdkstudydemo.ui.eventmonitor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sdkstudydemo.network.NetworkResult
import com.example.sdkstudydemo.network.RetrofitProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.sdkstudydemo.network.safeApiCall
import com.example.sdkstudydemo.repository.TodoRepository
import retrofit2.Retrofit

class EventMonitorViewModel(
    private val repository: TodoRepository
): ViewModel() {
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
    fun testNetworkRequest() {
        viewModelScope.launch {

            val result = safeApiCall {
                RetrofitProvider.todoApi.getTodo()
            }

            when (result) {

                is NetworkResult.Success -> {
                    Log.d(
                        "Day66",
                        "成功：${result.data.title}"
                    )
                }

                is NetworkResult.Error -> {
                    Log.e(
                        "Day66",
                        "失败类型：${result.type}，原因：${result.message}"
                    )
                }
            }
        }
    }

    fun loadTodo() {
        viewModelScope.launch {
            val result = repository.getTodo()

            when (result) {
                is NetworkResult.Success -> {
                    // 更新成功 UI State
                    _uiState.value = EventMonitorUiState.Success("成功1"+result.data.title)
                }

                is NetworkResult.Error -> {
                    // 更新错误 UI State
                    _uiState.value = EventMonitorUiState.Error("失败"+result.type+result.message)
                }
            }
        }
    }
}