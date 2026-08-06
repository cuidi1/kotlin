package com.example.sdkstudydemo.config.model

//模拟“拉取sdk远程配置”的结果
data class SdkConfigFetchResult(
    val success: Boolean,
    val message: String
)