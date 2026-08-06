package com.example.sdkstudydemo.event.policy

import com.example.sdkstudydemo.core.AppResult
import com.example.sdkstudydemo.event.model.SdkPendingEvent

interface SdkRetryPolicy {
    //判断本次上传失败后，要不要加入队列。
    fun shouldCacheFailedEvent(
        result: AppResult<Unit>
    ): Boolean
    //判断某条事件重试失败后，要不要丢弃。
    fun shouldDropAfterFailure(
        pendingEvent: SdkPendingEvent
    ): Boolean
}