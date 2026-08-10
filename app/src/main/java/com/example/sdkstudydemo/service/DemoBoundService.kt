package com.example.sdkstudydemo.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.sdkstudydemo.core.SdkLogger

class DemoBoundService : Service() {
    private var count = 0
    private val binder = LocalBinder()

    inner class LocalBinder : Binder(){
        fun getService(): DemoBoundService{
            return this@DemoBoundService
        }
    }

    override fun onCreate() {
        super.onCreate()
        SdkLogger.d("DemoBoundService onCreate")
    }
    override fun onBind(intent: Intent?): IBinder? {
        SdkLogger.d("DemoBoundService onBind")

        return binder
    }
    fun increaseCount(): Int{
        count++
        SdkLogger.d("DemoBoundService increaseCount:$count")
        return count
    }

    fun getCount():Int{
        return count
    }

    override fun onDestroy() {
        super.onDestroy()
        SdkLogger.d("DemoBoundService onDestroy")
    }

}