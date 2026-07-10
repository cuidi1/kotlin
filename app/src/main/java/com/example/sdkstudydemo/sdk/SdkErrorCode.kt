package com.example.sdkstudydemo.sdk

enum class SdkErrorCode(
    val code: Int,
    val message: String
) {
    SUCCESS(0,"成功"),
    SDK_NOT_INITIALIZED(1001,"SDK未初始化"),
    USER_CONSENT_REQUIRED(2001,"用户未同意隐私协议"),
    INVALID_EVENT_NAME(3001, "事件名称不能为空")
}