package com.example.sdkstudydemo.sdk

data class SdkEvent (
    val eventName: String,
    val params: Map<String, String>,
    val appId:String,
    val environment: SdkEnvironment,
    val sdkVerSion: String,
    val timestamp: Long)