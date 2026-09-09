package com.example.sdkstudydemo.ui.eventmonitor

sealed interface EventMonitorUiState {
    data object Idle: EventMonitorUiState
    data object Loading: EventMonitorUiState
    data class  Success(
        val message: String
    ): EventMonitorUiState
    data class Error(
        val message: String
    ): EventMonitorUiState
}