package com.example.sdkstudydemo

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