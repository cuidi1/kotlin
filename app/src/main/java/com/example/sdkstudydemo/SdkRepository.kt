package com.example.sdkstudydemo

import android.content.Context
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkConfig
import com.example.sdkstudydemo.sdk.SdkEvent
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import java.io.IOException

//repository负责拉取数据，viewmodel不直接关心
class SdkRepository(
    private val context: Context,
    private val remoteConfigDataSource: SdkRemoteCongfigDataSource,
    private val eventUploadDataSource: SdkEventUploadDataSource,
    private val eventRetryQueue: SdkEventRetryQueue

) {
    private var cachedConfig: SdkRemoteConfig?=null
    private val configDataStore = SdkConfigDataStore(
        //Repository生命周期可能比Activity长，不应该持有Activity Context
        context.applicationContext
    )
    private suspend fun saveConfigToCache(config: SdkRemoteConfig) {
        cachedConfig = config
        configDataStore.saveRemoteConfig(config)
    }
    fun getSdkInfo(): SdkInfo {
        return SdkInfo(
            sdkInitialized = MySdk.isInitialized(),
            appId = MySdk.getAppId(),
            environment = MySdk.getEnvironment(),
            userConsent = MySdk.hasUserConsent()
        )
    }
    fun setUserConsent(consent: Boolean): SdkInfo {
        MySdk.setUserConsent(consent)
        return getSdkInfo()
    }
//模拟从远程拉取数据
//    suspend fun fetchRemoteConfigMock(
//        shouldSuccess: Boolean
//    ): SdkConfigFetchResult{
//        delay(1500)
//        return if(shouldSuccess) {
//            SdkConfigFetchResult(
//                success = true,
//                message = "请求成功，拿到Sdk配置"
//            )
//        }else {
//            SdkConfigFetchResult(
//                success = false,
//                message = "网络异常，请稍后重试"
//            )
//        }
//    }
    suspend fun fetchRemoteConfigMock(
        mode: Int
    ): AppResult<SdkRemoteConfig> {
        delay(1500)
        return when(mode) {
            0 -> {
                AppResult.Success(
                    SdkRemoteConfig(
                        enableUpload = true,
                        sampleRate = 100,
                        configVersion = "1.0.0"
                    )
                )
            }
            1 -> {
                AppResult.Error(
                    code = 4001,
                    message = "服务端返回配置失败"
                )
            }

            else -> {
                AppResult.Exception(
                    IOException("网络连接异常")
                )
            }

        }
    }
//    suspend fun fetchRemoteConfigWithFallback(
//        mode: Int
//    ): AppResult<SdkRemoteConfig> {
//        return try{
//            val result = withTimeout(2000) {
//                fetchRemoteConfigFromNetworkMock(mode)
//            }
//
//            when(result) {
//                is AppResult.Success -> {
//                    saveConfigToCache(result.data)
//                    result
//                }
//
//                is AppResult.Error -> {
//                    getFallbackConfigResult(
//                        reason = "远程配置业务失败：${result.message}"
//                    )
//                }
//
//                is AppResult.Exception ->{
//                    getFallbackConfigResult(
//                        reason = "远程配置异常：${result.throwable.message ?:"未知异常"}"
//                    )
//                }
//            }
//        }catch(e : TimeoutCancellationException){
//            getFallbackConfigResult(
//                reason = "远程配置请求超时"
//            )
//        }catch (e : Exception) {
//            getFallbackConfigResult(
//                reason = "远程配置未知异常：${e.message ?: "未知异常"}"
//            )
//        }
//    }
    suspend fun fetchRemoteConfigWithFallback(
        mode: Int
    ): AppResult<SdkRemoteConfig> {
        val result = requestRemoteConfigWithRetry(
            mode = mode,
            maxRetryCount = 2,
            retryDelayMillis = 500L
        )

        return when (result) {
            is AppResult.Success -> {
                saveConfigToCache(result.data)
                result
            }

            is AppResult.Error -> {
                getFallbackConfigResult(
                    reason = "远程配置业务失败：${result.message}"
                )
            }

            is AppResult.Exception -> {
                getFallbackConfigResult(
                    reason = "远程配置异常：${result.throwable.message ?: "未知异常"}"
                )
            }
        }
    }

    //移到了SdkNetworkClient里
//    private suspend fun fetchRemoteConfigFromNetworkMock(
//        mode: Int
//    ): AppResult<SdkRemoteConfig> {
//        delay(1500)
//
//        return when (mode) {
//            0 -> {
//                AppResult.Success(
//                    SdkRemoteConfig(
//                        enableUpload = true,
//                        sampleRate = 100,
//                        configVersion = "remote_1.0.0"
//                    )
//                )
//            }
//
//            1 -> {
//                AppResult.Error(
//                    code = 4001,
//                    message = "服务端返回配置失败"
//                )
//            }
//
//            2 -> {
//                AppResult.Exception(
//                    IOException("网络连接异常")
//                )
//            }
//
//
//            else -> {
//                delay(3000)
//
//                AppResult.Success(
//                    SdkRemoteConfig(
//                        enableUpload = true,
//                        sampleRate = 50,
//                        configVersion = "remote_slow"
//                    )
//                )
//            }
//        }
//    }
    private suspend fun getFallbackConfigResult(
        reason: String
    ): AppResult<SdkRemoteConfig> {
        val cache = getCachedConfig()
        return if(cache != null) {
            AppResult.Success(
                cache.copy(
                    configVersion =  "${cache.configVersion}_cache"
                )
            )
        } else{
            AppResult.Success(
                getDefaultConfig().copy(
                    configVersion = "default,原因：$reason"
                )
            )
        }
    }

    private suspend fun getCachedConfig(): SdkRemoteConfig? {
        return configDataStore.getRemoteConfig()
    }

    private fun getDefaultConfig(): SdkRemoteConfig{
        return SdkRemoteConfig(
            enableUpload = false,
            sampleRate = 10,
            configVersion = "default"
        )
    }


    //判断是否要重试获取远程配置
    private fun shouldRetry(
        result: AppResult<SdkRemoteConfig>
    ): Boolean {
        return when(result) {
            is AppResult.Success -> {
                false
            }
            is AppResult.Error ->{
                false
            }
            is AppResult.Exception -> {
                result.throwable is TimeoutCancellationException
            }
        }
    }

    private suspend fun requestRemoteConfigOnce(
        mode: Int
    ): AppResult<SdkRemoteConfig> {
        return try {
            withTimeout(2000) {
//                fetchRemoteConfigFromNetworkMock(mode)
//                networkClient.fetchRemoteConfig(mode)
                remoteConfigDataSource.fetchRemoteConfig(mode)
            }
        } catch (e: TimeoutCancellationException) {
            AppResult.Exception(e)
        } catch (e: IOException) {
            AppResult.Exception(e)
        } catch (e: Exception) {
            AppResult.Exception(e)
        }
    }
    //重连核心方法
    private suspend fun requestRemoteConfigWithRetry(
        mode:Int,
        maxRetryCount: Int = 2,
        retryDelayMillis: Long = 500L
    ): AppResult<SdkRemoteConfig> {
        var currentAttempt = 0
        var lastResult: AppResult<SdkRemoteConfig>
        while(true) {
            lastResult = requestRemoteConfigOnce(mode)
            if(lastResult is AppResult.Success) {
                return lastResult
            }

            if(!shouldRetry(lastResult)) {
                return lastResult
            }

            if (currentAttempt >= maxRetryCount) {
                return lastResult
            }

            currentAttempt++

            delay(retryDelayMillis)
        }
    }
    suspend fun uploadEvent(
        event: SdkEvent
    ): AppResult<Unit> {
        return eventUploadDataSource.uploadEvent(event)
    }

    //上传失败后加入队列
    suspend fun uploadEventWithCache(
        event: SdkEvent
    ): AppResult<Unit> {
//        先正常上传；
//        如果失败并且值得重试；
//        就放入失败队列；
//        最后仍然把本次结果返回给 ViewModel。
        val result = eventUploadDataSource.uploadEvent(event)
        if(shouldCacheFailedEvent(result)){
            eventRetryQueue.enqueue(
                event = event,
                errorMessage = getErrorMessage(result)
            )
        }
        return result
    }

    private fun getErrorMessage(result: AppResult<Unit>): String {
        return when(result) {
            is AppResult.Success -> {
                "成功"
            }

            is AppResult.Error -> {
                "错误码：${result.code}，错误信息：${result.message}"
            }

            is AppResult.Exception -> {
                result.throwable.message ?: "未知异常"
            }

        }
    }

    private fun shouldCacheFailedEvent(result: AppResult<Unit>): Boolean {
        return when (result) {
            is AppResult.Success->{
                false
            }
            //只缓存 408、429、500～599 这类可能临时失败的错误。
            is AppResult.Error ->{
                result.code == 408 || result.code == 429 || result.code in 500..599
            }
            is AppResult.Exception -> {
                true
            }
        }
    }

    //重试队列里的事件,这是连接通过网络上传时间的入口，带缓存队列的
    suspend fun retryCachedEvents(): AppResult<Int> {
        val pendingEvents = eventRetryQueue.getPendingEvents()
        if(pendingEvents.isEmpty()){
            return AppResult.Success(0)
        }
        var successCount = 0
        for(pendingEvent in pendingEvents) {
            val result = eventUploadDataSource.uploadEvent(pendingEvent.event)
            when(result) {
                is AppResult.Success ->{
                    eventRetryQueue.remove(pendingEvent.id)
                    successCount++
                }

                is AppResult.Error -> {
                    handleRetryFailure(
                        pendingEvent = pendingEvent,
                        errorMessage = "错误码：${result.code}，错误信息：${result.message}"
                    )
                }

                is AppResult.Exception -> {
                    handleRetryFailure(
                        pendingEvent = pendingEvent,
                        errorMessage = result.throwable.message ?: "未知异常"
                    )
                }
            }
        }
        return AppResult.Success(successCount)
    }

    private fun handleRetryFailure(
        pendingEvent: SdkPendingEvent,
        errorMessage: String
    ) {
        val maxRetryCount = 3

        if (pendingEvent.retryCount + 1 >= maxRetryCount) {
            eventRetryQueue.remove(pendingEvent.id)
            return
        }

        eventRetryQueue.updateRetryFailure(
            pendingEventId = pendingEvent.id,
            errorMessage = errorMessage
        )
    }


    suspend fun retryCachedEventsByBatch(): AppResult<Int> {
        val batchSize = 10

        val batchEvents = eventRetryQueue.getBatchEvents(batchSize)

        if (batchEvents.isEmpty()) {
            return AppResult.Success(0)
        }

        val events = batchEvents.map { pendingEvent ->
            pendingEvent.event
        }

        val result = eventUploadDataSource.uploadEvents(events)

        return when (result) {
            is AppResult.Success -> {
                val successIds = batchEvents.map { pendingEvent ->
                    pendingEvent.id
                }

                eventRetryQueue.removeAllByIds(successIds)

                AppResult.Success(batchEvents.size)
            }

            is AppResult.Error -> {
                handleBatchRetryFailure(
                    pendingEvents = batchEvents,
                    errorMessage = "错误码：${result.code}，错误信息：${result.message}"
                )

                AppResult.Error(
                    code = result.code,
                    message = result.message
                )
            }

            is AppResult.Exception -> {
                handleBatchRetryFailure(
                    pendingEvents = batchEvents,
                    errorMessage = result.throwable.message ?: "未知异常"
                )

                AppResult.Exception(result.throwable)
            }
        }
    }

    private fun handleBatchRetryFailure(
        pendingEvents: List<SdkPendingEvent>,
        errorMessage: String
    ) {
        for (pendingEvent in pendingEvents) {
            handleRetryFailure(
                pendingEvent = pendingEvent,
                errorMessage = errorMessage
            )
        }
    }
}