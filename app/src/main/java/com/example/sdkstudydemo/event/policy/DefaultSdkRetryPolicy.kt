package com.example.sdkstudydemo.event.policy

import com.example.sdkstudydemo.core.AppResult
import com.example.sdkstudydemo.event.model.SdkPendingEvent

/**
 * 上传成功：
 *     不缓存。
 *
 * HTTP 408 / 429 / 5xx：
 *     认为可能是临时失败，可以缓存。
 *
 * Exception：
 *     网络异常、超时、解析异常，先缓存。
 *
 * 某条事件失败后：
 *     如果 retryCount + 1 >= 最大重试次数，就丢弃。
 */
class DefaultSdkRetryPolicy(
    private val maxRetryCount: Int = 3
): SdkRetryPolicy {
    override fun shouldCacheFailedEvent(result: AppResult<Unit>): Boolean {
        return when(result){
            is AppResult.Success -> {
                false
            }
            is AppResult.Error->{
                result.code ==408 ||
                        result.code == 429 ||
                        result.code in 500..599
            }

            is AppResult.Exception->{
                true
            }
        }
    }

    override fun shouldDropAfterFailure(pendingEvent: SdkPendingEvent): Boolean {
        return pendingEvent.retryCount +1>=maxRetryCount
    }
}