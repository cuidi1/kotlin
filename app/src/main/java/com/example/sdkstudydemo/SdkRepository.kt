package com.example.sdkstudydemo

import com.example.sdkstudydemo.sdk.MySdk
import kotlinx.coroutines.delay
import java.io.IOException

//repository负责拉取数据，viewmodel不直接关心
class SdkRepository {
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

}