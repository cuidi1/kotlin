package com.example.sdkstudydemo.sdk

data class SdkUploadResult(
    val success: Boolean,
    val message: String,
    val response: String = ""
)