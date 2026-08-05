package com.example.sdkstudydemo

class DefaultSdkBatchPolicy(
    private val batchSize: Int = 10
): SdkBatchPolicy {
    override fun getBatchSize(): Int {
        return batchSize
    }
}