package com.example.sdkstudydemo
//sdk的远程配置
data class SdkRemoteConfig(
    //是否允许上报
    val enableUpload: Boolean,
    //采样率
    val sampleRate: Int,
    //配置版本
    val configVersion: String
)
