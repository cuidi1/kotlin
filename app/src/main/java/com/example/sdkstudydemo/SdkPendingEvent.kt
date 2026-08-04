package com.example.sdkstudydemo

import com.example.sdkstudydemo.sdk.SdkEvent
import java.util.UUID

/**
 * id：
 *     每个待重试事件的唯一标识。
 *
 * event：
 *     真正要上传的事件。
 *
 * retryCount：
 *     已经重试了几次。
 *
 * lastErrorMessage：
 *     上一次失败原因。
 *
 * createdAt：
 *     事件进入队列的时间。
 */
data class SdkPendingEvent(
    val id: String = UUID.randomUUID().toString(),
    val event: SdkEvent,
    val retryCount: Int = 0,
    val lastErrorMessage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
