package com.example.sdkstudydemo.sdk

interface SdkUploadCallback {
    fun onSuccess(response: String)
    fun onFailure(errorMessage: String)
}