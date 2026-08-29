package com.example.sdkstudydemo.app

import android.app.Application
import android.os.Process
import android.util.Log
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkConfig
import com.example.sdkstudydemo.sdk.SdkEnvironment

class MyApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        Log.d(
            "Day48",
            "Application.onCreate pid=${Process.myPid()}, " +
                "application=$this, " +
                "applicationIdentityHashCode=${System.identityHashCode(this)}"
        )

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
