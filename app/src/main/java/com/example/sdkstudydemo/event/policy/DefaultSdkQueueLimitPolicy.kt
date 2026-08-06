package com.example.sdkstudydemo.event.policy

/**
 * 如果当前队列数量已经大于等于最大队列长度，
 * 那就应该移除最早的事件。
 */
class DefaultSdkQueueLimitPolicy(
    private val maxQueueSize: Int = 100
) : SdkQueueLimitPolicy {
    override fun shouldRemoveOldest(currentSize: Int): Boolean {
        return currentSize>=maxQueueSize
    }

}