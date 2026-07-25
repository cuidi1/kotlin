package com.example.sdkstudydemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sdkstudydemo.sdk.MySdk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var clickCount: Int = 0
    private val repository = SdkRepository()
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
    private fun fetchRemoteConfig(mode: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                requestState = RequestState.Loading,
                message = "开始拉取远程配置"
            )

            val result = repository.fetchRemoteConfigMock(mode)

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
}