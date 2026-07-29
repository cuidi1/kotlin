package com.example.sdkstudydemo

import android.app.Application
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkConfig
import com.example.sdkstudydemo.sdk.SdkEnvironment

class MyApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(applicationContext)

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