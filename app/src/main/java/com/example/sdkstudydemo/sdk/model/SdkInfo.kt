package com.example.sdkstudydemo.sdk.model

data class SdkInfo(
    val sdkInitialized: Boolean,
    val appId: String,
    val environment: String,
    val userConsent: Boolean
)