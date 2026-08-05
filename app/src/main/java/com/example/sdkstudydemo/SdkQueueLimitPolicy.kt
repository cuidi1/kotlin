package com.example.sdkstudydemo

interface SdkQueueLimitPolicy {
    fun shouldRemoveOldest(
        currentSize: Int
    ): Boolean
}