package com.example.sdkstudydemo

data class MainUiState(
    val clickCount: Int=0,
    val sdkInitialized: Boolean = false,
    val appId:String = "",
    val environment:String = "",
    val userConsent: Boolean = false,
    val message:String = "",
)
