package com.example.sdkstudydemo.sdk
import android.util.Log
object SdkLogger {
    private const val TAG = "MySdk"
    private var enableLog: Boolean = true
    //创建一个可以修改的字符串列表
    private val logList = mutableListOf<String>()

    fun setEnableLog(enable: Boolean){
        enableLog = enable
    }
    fun d(message: String){
        if(!enableLog){
            return;
        }
        Log.d(TAG, message)
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
    }
    private fun addLogs(leve: String, message: String) {
        val log = "[$level]$message"
        logList.add(log)
    }

}