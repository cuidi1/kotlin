package com.example.sdkstudydemo.event.policy

interface SdkQueueLimitPolicy {
    fun shouldRemoveOldest(
        currentSize: Int
    ): Boolean
}