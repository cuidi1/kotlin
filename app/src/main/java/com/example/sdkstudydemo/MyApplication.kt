package com.example.sdkstudydemo

import android.app.Application
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkConfig
import com.example.sdkstudydemo.sdk.SdkEnvironment

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MySdk.init(
            context = this,
            config = SdkConfig(
                appId = "demo_app_id",
                environment = SdkEnvironment.TEST,
                enableLog = true
            )
        )
    }
}