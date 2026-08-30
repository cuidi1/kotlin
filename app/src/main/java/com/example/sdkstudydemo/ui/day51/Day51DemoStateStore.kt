package com.example.sdkstudydemo.ui.day51

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Day51StaticSnapshot(
    val receivedCount: Int,
    val lastAction: String,
    val lastThread: String,
    val lastPid: Int
)

data class Day51WorkerSnapshot(
    val workId: String?,
    val lastStartedAt: Long,
    val lastFinishedAt: Long,
    val lastResult: String
)

/*
 * SharedPreferences 和 StateFlow 只用于保存、展示 Day51 实验日志与结果。
 * 它们不是 BroadcastReceiver 或 WorkManager 可靠性的核心机制。
 */
object Day51DemoStateStore {
    private const val PREFS_NAME = "day51_demo_state"
    private const val KEY_EVENTS = "events"
    private const val KEY_STATIC_COUNT = "static_count"
    private const val KEY_STATIC_ACTION = "static_action"
    private const val KEY_STATIC_THREAD = "static_thread"
    private const val KEY_STATIC_PID = "static_pid"
    private const val KEY_WORK_ID = "work_id"
    private const val KEY_WORK_STARTED_AT = "work_started_at"
    private const val KEY_WORK_FINISHED_AT = "work_finished_at"
    private const val KEY_WORK_RESULT = "work_result"
    private const val EVENT_SEPARATOR = "\u001E"
    private const val MAX_EVENTS = 250
    private const val TAG = "Day51"

    private val _events = MutableStateFlow<List<String>>(emptyList())
    val events = _events.asStateFlow()

    @Volatile
    private var initialized = false

    @Synchronized
    fun initialize(context: Context) {
        if (initialized) return
        val saved = prefs(context).getString(KEY_EVENTS, "").orEmpty()
        _events.value = if (saved.isBlank()) emptyList() else saved.split(EVENT_SEPARATOR)
        initialized = true
    }

    @Synchronized
    fun record(context: Context, message: String) {
        initialize(context)
        Log.d(TAG, message)
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val updated = (_events.value + "$timestamp  $message").takeLast(MAX_EVENTS)
        _events.value = updated
        prefs(context).edit().putString(KEY_EVENTS, updated.joinToString(EVENT_SEPARATOR)).commit()
    }

    @Synchronized
    fun clearEvents(context: Context) {
        initialize(context)
        _events.value = emptyList()
        prefs(context).edit().remove(KEY_EVENTS).commit()
    }

    @Synchronized
    fun recordStaticReceipt(
        context: Context,
        action: String,
        thread: String,
        pid: Int
    ): Int {
        val preferences = prefs(context)
        val newCount = preferences.getInt(KEY_STATIC_COUNT, 0) + 1
        preferences.edit()
            .putInt(KEY_STATIC_COUNT, newCount)
            .putString(KEY_STATIC_ACTION, action)
            .putString(KEY_STATIC_THREAD, thread)
            .putInt(KEY_STATIC_PID, pid)
            .commit()
        return newCount
    }

    fun staticSnapshot(context: Context): Day51StaticSnapshot {
        val preferences = prefs(context)
        return Day51StaticSnapshot(
            receivedCount = preferences.getInt(KEY_STATIC_COUNT, 0),
            lastAction = preferences.getString(KEY_STATIC_ACTION, "—").orEmpty(),
            lastThread = preferences.getString(KEY_STATIC_THREAD, "—").orEmpty(),
            lastPid = preferences.getInt(KEY_STATIC_PID, 0)
        )
    }

    fun saveLatestWorkId(context: Context, workId: String) {
        prefs(context).edit().putString(KEY_WORK_ID, workId).commit()
    }

    fun recordWorkerStarted(context: Context) {
        prefs(context).edit()
            .putLong(KEY_WORK_STARTED_AT, System.currentTimeMillis())
            .putString(KEY_WORK_RESULT, "RUNNING")
            .commit()
    }

    fun recordWorkerFinished(context: Context, result: String) {
        prefs(context).edit()
            .putLong(KEY_WORK_FINISHED_AT, System.currentTimeMillis())
            .putString(KEY_WORK_RESULT, result)
            .commit()
    }

    fun workerSnapshot(context: Context): Day51WorkerSnapshot {
        val preferences = prefs(context)
        return Day51WorkerSnapshot(
            workId = preferences.getString(KEY_WORK_ID, null),
            lastStartedAt = preferences.getLong(KEY_WORK_STARTED_AT, 0L),
            lastFinishedAt = preferences.getLong(KEY_WORK_FINISHED_AT, 0L),
            lastResult = preferences.getString(KEY_WORK_RESULT, "尚未执行").orEmpty()
        )
    }

    fun formatTime(value: Long): String {
        if (value == 0L) return "—"
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(value))
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
