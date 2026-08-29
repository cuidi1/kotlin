package com.example.sdkstudydemo.ui.day50

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Day50ServiceState(
    val serviceExists: Boolean = false,
    val started: Boolean = false,
    val bound: Boolean = false,
    val binderConnected: Boolean = false,
    val serviceIdentity: Int? = null,
    val currentThread: String = "—"
)

/*
 * 这个单例仅用于学习 Demo 的状态和日志展示，不是 Service 核心机制的一部分。
 * Service 的真实生命周期仍由 Android 系统、start/stop 和 bind/unbind 决定。
 */
object Day50ServiceLog {
    private val _state = MutableStateFlow(Day50ServiceState())
    val state = _state.asStateFlow()

    private val _events = MutableStateFlow<List<String>>(emptyList())
    val events = _events.asStateFlow()

    fun addEvent(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _events.value = (_events.value + "$time  $message").takeLast(MAX_EVENT_COUNT)
    }

    fun clearEvents() {
        _events.value = emptyList()
    }

    fun markStartRequested() {
        updateState { it.copy(started = true) }
    }

    fun markStopRequested() {
        updateState { it.copy(started = false) }
    }

    fun markServiceCreated(serviceIdentity: Int, thread: String) {
        updateState {
            it.copy(
                serviceExists = true,
                serviceIdentity = serviceIdentity,
                currentThread = thread
            )
        }
    }

    fun markServiceStarted(serviceIdentity: Int, thread: String) {
        updateState {
            it.copy(
                serviceExists = true,
                started = true,
                serviceIdentity = serviceIdentity,
                currentThread = thread
            )
        }
    }

    fun markServiceBound(serviceIdentity: Int, thread: String) {
        updateState {
            it.copy(
                serviceExists = true,
                bound = true,
                serviceIdentity = serviceIdentity,
                currentThread = thread
            )
        }
    }

    fun markServiceUnbound(thread: String) {
        updateState { it.copy(bound = false, currentThread = thread) }
    }

    fun markBinderConnected(connected: Boolean) {
        updateState { it.copy(binderConnected = connected) }
    }

    fun markServiceDestroyed(thread: String) {
        _state.value = Day50ServiceState(currentThread = thread)
    }

    private fun updateState(transform: (Day50ServiceState) -> Day50ServiceState) {
        _state.value = transform(_state.value)
    }

    private const val MAX_EVENT_COUNT = 200
}
