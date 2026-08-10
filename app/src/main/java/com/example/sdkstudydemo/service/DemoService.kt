package com.example.sdkstudydemo.service

import android.R.attr.delay
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.sdkstudydemo.core.SdkLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DemoService : Service(){
    //Service自己管理的后台协程作用域
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        SdkLogger.d("DemoService onCreate,线程=${Thread.currentThread().name}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SdkLogger.d( "DemoService onStartCommand，" +
                "startId=$startId，" +
                "线程=${Thread.currentThread().name}")
        serviceScope.launch {
            repeat(5) {
                    index->
                delay(1000)

                SdkLogger.d(
                    "DemoService 后台任务 ${index + 1}/5，" +
                            "线程=${Thread.currentThread().name}"
                )
            }
            SdkLogger.d("DemoService 后台任务执行完成")
            //自己结束Service
            stopSelf()
        }
        return START_NOT_STICKY

    }

    override fun onDestroy() {
        SdkLogger.d("DemoService onDestroy,线程=${Thread.currentThread().name}")

        serviceScope.cancel()
        super.onDestroy()
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}