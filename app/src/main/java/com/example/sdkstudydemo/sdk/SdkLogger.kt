package com.example.sdkstudydemo.sdk
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SdkLogger {
    private const val TAG = "MySdk"
    private var enableLog: Boolean = true
    //创建一个可以修改的字符串列表
    private val logList = mutableListOf<String>()
    //内部可以修改的日志流
    private val _logFlow = MutableStateFlow<List<String>>(emptyList())
    //对外暴露一个只读的 StateFlow，让外部只能监听日志变化，不能修改日志。
    val logFlow: StateFlow<List<String>> = _logFlow.asStateFlow()
    fun setEnableLog(enable: Boolean){
        enableLog = enable
    }
    fun d(message: String){
        if(!enableLog){
            return;
        }
        Log.d(TAG, message)
        addLogs(TAG,message)
    }

    fun e(message: String, throwable: Throwable? = null){
        if(!enableLog){
            return;
        }
        Log.e(TAG, message)
    }

    fun getLogs(): List<String> {
        return logList.toList()
    }

    fun clearLogs(){
        logList.clear()
        _logFlow.value = emptyList()
    }
    private fun addLogs(level: String, message: String) {
        val log = "[$level]$message"
        logList.add(log)
        _logFlow.value=logList.toList()
    }

}