package com.example.sdkstudydemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkEnvironment
import com.example.sdkstudydemo.sdk.SdkEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SdkRepository
) : ViewModel() {
    var clickCount: Int = 0
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
//    fun refreshSdkInfo() {
//        _uiState.value = _uiState.value.copy(
//            clickCount = clickCount ,
//            sdkInitialized = MySdk.isInitialized(),
//            appId = MySdk.getAppId(),
//            environment = MySdk.getEnvironment(),
//            userConsent = MySdk.hasUserConsent())
//    }
//加入repository之后
    fun refreshSdkInfo() {
        val sdkInfo = repository.getSdkInfo()

        _uiState.value = _uiState.value.copy(
            sdkInitialized = sdkInfo.sdkInitialized,
            appId = sdkInfo.appId,
            environment = sdkInfo.environment,
            userConsent = sdkInfo.userConsent
        )
    }
    fun increaseCount(){
        val oldState = _uiState.value
        _uiState.value = oldState.copy(
            clickCount = oldState.clickCount + 1,
            message = "点击了${oldState.clickCount + 1}次"
        )
    }
//    fun setUserConsent(consent: Boolean) {
//        MySdk.setUserConsent(consent)
//
//        _uiState.value = _uiState.value.copy(
//            userConsent = consent,
//            message = "用户隐私授权状态更新：$consent"
//        )
//    }

    fun setUserConsent(consent: Boolean) {
        val sdkInfo = repository.setUserConsent(consent)

        _uiState.value = _uiState.value.copy(
            userConsent = sdkInfo.userConsent,
            sdkInitialized = sdkInfo.sdkInitialized,
            appId = sdkInfo.appId,
            environment = sdkInfo.environment,
            message = "用户隐私授权状态更新：$consent"
        )
    }

//    fun simulateRequestSuccess(){
//        viewModelScope.launch{
//            _uiState.value = _uiState.value.copy(
//                requestState = RequestState.Loading,
//                message = "开始模拟请求"
//            )
//
//            delay(1500)
//            _uiState.value = _uiState.value.copy(
//                requestState = RequestState.Success("请求成功，拿到SDK配置"),
//                message = "请求成功"
//            )
//        }
//    }
//    fun simulateRequestError() {
//        //在 ViewModel 生命周期范围内启动协程。
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(
//                requestState = RequestState.Loading,
//                message = "开始模拟请求"
//            )
//
//            delay(1500)
//
//            _uiState.value = _uiState.value.copy(
//                requestState = RequestState.Error("网络异常，请稍后重试"),
//                message = "请求失败"
//            )
//        }
//    }
//    fun simulateRequestSuccess() {
//        fetchRemoteConfig(shouldSuccess = true)
//    }
//
//    fun simulateRequestError() {
//        fetchRemoteConfig(shouldSuccess = false)
//    }


    fun simulateRequestSuccess() {
        fetchRemoteConfig(mode = 0)
    }

    fun simulateRequestError() {
        fetchRemoteConfig(mode = 1)
    }

    fun simulateRequestException() {
        fetchRemoteConfig(mode = 2)
    }

    //时间上报，没有给这个接口写按钮，这个是网络层面的上报，就是通过http传过去了
    fun uploadTestEvent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                requestState = RequestState.Loading,
                message = "开始上传测试事件"
            )

            val event = SdkEvent(
                eventName = "test_event",
                params = mapOf(
                    "page" to "MainActivity",
                    "button" to "upload_test_event"
                ),
                appId = "demo_app_id",
                environment = SdkEnvironment.TEST,
                sdkVerSion = "1.0.0",
                timestamp = System.currentTimeMillis()
            )

            val result = repository.uploadEvent(event)

            _uiState.value = when (result) {
                is AppResult.Success -> {
                    _uiState.value.copy(
                        requestState = RequestState.Success("事件上传成功"),
                        message = "事件上传成功"
                    )
                }

                is AppResult.Error -> {
                    _uiState.value.copy(
                        requestState = RequestState.Error(
                            "事件上传失败：${result.code}，${result.message}"
                        ),
                        message = "事件上传业务失败"
                    )
                }

                is AppResult.Exception -> {
                    _uiState.value.copy(
                        requestState = RequestState.Error(
                            "事件上传异常：${result.throwable.message ?: "未知异常"}"
                        ),
                        message = "事件上传异常"
                    )
                }
            }
        }
    }
    private fun fetchRemoteConfig(mode: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                requestState = RequestState.Loading,
                message = "开始拉取远程配置"
            )

//            val result = repository.fetchRemoteConfigMock(mode)
            val result = repository.fetchRemoteConfigWithFallback(mode)
            _uiState.value =when(result){
                is AppResult.Success->{
                    val config = result.data
                    _uiState.value.copy(
                        requestState = RequestState.Success(
                            "配置版本：${config.configVersion}, 采样率：${config.sampleRate}, 允许上报：${config.enableUpload}"
                        ),
                        message = "远程配置请求成功"
                    )
                }
                is AppResult.Error->{
                    _uiState.value.copy(
                        requestState = RequestState.Error(
                            "错误码：${result.code}，错误信息：${result.message}"
                        ),
                        message = "远程配置请求失败"
                    )
                }


                is AppResult.Exception -> {
                    _uiState.value.copy(
                        requestState = RequestState.Error(
                            "异常：${result.throwable.message ?: "未知异常"}"
                        ),
                        message = "远程配置请求异常"
                    )
                }
            }
        }
    }
//重试上传事件
    fun retryCachedEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                requestState = RequestState.Loading,
                message = "开始重试缓存事件"
            )

            val result = repository.retryCachedEvents()

            _uiState.value = when (result) {
                is AppResult.Success -> {
                    _uiState.value.copy(
                        requestState = RequestState.Success(
                            "本次重试成功 ${result.data} 条事件"
                        ),
                        message = "缓存事件重试完成"
                    )
                }

                is AppResult.Error -> {
                    _uiState.value.copy(
                        requestState = RequestState.Error(
                            "重试失败：${result.code}，${result.message}"
                        ),
                        message = "缓存事件重试失败"
                    )
                }

                is AppResult.Exception -> {
                    _uiState.value.copy(
                        requestState = RequestState.Error(
                            "重试异常：${result.throwable.message ?: "未知异常"}"
                        ),
                        message = "缓存事件重试异常"
                    )
                }
            }
        }
    }
}