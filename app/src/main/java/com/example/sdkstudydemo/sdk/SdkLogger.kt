package com.example.sdkstudydemo.sdk
import android.util.Log
object SdkLogger {
    private const val TAG = "MySdk"
    private var enableLog: Boolean = true
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

}