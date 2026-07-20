package com.example.sdkstudydemo

import androidx.lifecycle.ViewModel
import com.example.sdkstudydemo.sdk.MySdk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    var clickCount: Int = 0
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    fun refreshSdkInfo() {
        _uiState.value = _uiState.value.copy(
            clickCount = clickCount ,
            sdkInitialized = MySdk.isInitialized(),
            appId = MySdk.getAppId(),
            environment = MySdk.getEnvironment(),
            userConsent = MySdk.hasUserConsent())
    }

    fun increaseCount(){
        val oldState = _uiState.value
        _uiState.value = oldState.copy(
            clickCount = oldState.clickCount + 1,
            message = "点击了${oldState.clickCount + 1}次"
        )
    }
    fun setUserConsent(consent: Boolean) {
        MySdk.setUserConsent(consent)

        _uiState.value = _uiState.value.copy(
            userConsent = consent,
            message = "用户隐私授权状态更新：$consent"
        )
    }
}