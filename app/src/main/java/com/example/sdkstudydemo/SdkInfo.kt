package com.example.sdkstudydemo

import com.example.sdkstudydemo.sdk.SdkEnvironment

data class SdkInfo(
    val sdkInitialized: Boolean,
    val appId: String,
    val environment: String,
    val userConsent: Boolean
)