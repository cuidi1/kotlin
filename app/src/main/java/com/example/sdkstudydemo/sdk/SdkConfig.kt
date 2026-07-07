package com.example.sdkstudydemo.sdk

data class SdkConfig (
    val appId: String,
    val environment: SdkEnvironment = SdkEnvironment.TEST,
    val enableLog: Boolean = true,
    val userConsent: Boolean = false
)