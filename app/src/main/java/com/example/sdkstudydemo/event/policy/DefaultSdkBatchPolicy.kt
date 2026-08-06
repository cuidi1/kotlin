package com.example.sdkstudydemo.event.policy

class DefaultSdkBatchPolicy(
    private val batchSize: Int = 10
): SdkBatchPolicy {
    override fun getBatchSize(): Int {
        return batchSize
    }
}