package com.example.sdkstudydemo.ui.day51

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.WorkManager
import com.example.sdkstudydemo.R
import kotlinx.coroutines.launch
import java.util.UUID

class Day51Activity : AppCompatActivity() {

    private lateinit var dynamicStateView: TextView
    private lateinit var staticStateView: TextView
    private lateinit var workStateView: TextView
    private lateinit var workerResultView: TextView
    private lateinit var eventLogView: TextView

    private var isDynamicRegistered = false
    private var dynamicReceivedCount = 0
    private var dynamicLastAction = "—"
    private var dynamicLastThread = "—"
    private var observedWorkId: UUID? = null

    private val dynamicReceiver = Day51DynamicReceiver { action, thread ->
        dynamicReceivedCount++
        dynamicLastAction = action
        dynamicLastThread = thread
        renderDynamicState()
        dynamicReceivedCount
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day51)

        Day51DemoStateStore.initialize(this)
        dynamicStateView = findViewById(R.id.tvDay51DynamicState)
        staticStateView = findViewById(R.id.tvDay51StaticState)
        workStateView = findViewById(R.id.tvDay51WorkState)
        workerResultView = findViewById(R.id.tvDay51WorkerResult)
        eventLogView = findViewById(R.id.tvDay51EventLog)

        findViewById<TextView>(R.id.tvDay51Actions).text =
            "Dynamic action：${Day51Actions.dynamic(this)}\n" +
                "Static action：${Day51Actions.static(this)}\n" +
                "Unsafe action：${Day51Actions.unsafe(this)}\n" +
                "Enqueue action：${Day51Actions.enqueueWork(this)}"
        renderAdbCommands()
        setupButtons()
        observeEventLog()
        renderDynamicState()
        renderPersistentState()
        observeSavedWork()
        Day51DemoStateStore.record(this, "Activity.onCreate")
    }

    override fun onResume() {
        super.onResume()
        renderPersistentState()
    }

    override fun onDestroy() {
        if (isDynamicRegistered) {
            unregisterReceiver(dynamicReceiver)
            isDynamicRegistered = false
            Day51DemoStateStore.record(
                this,
                "Activity.onDestroy：自动 unregisterReceiver，防止注册关系泄漏 Activity Context"
            )
        }
        Day51DemoStateStore.record(this, "Activity.onDestroy")
        super.onDestroy()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnDay51RegisterDynamic).setOnClickListener {
            if (isDynamicRegistered) {
                Day51DemoStateStore.record(this, "忽略重复 Register：Dynamic Receiver 已注册")
                return@setOnClickListener
            }

            ContextCompat.registerReceiver(
                this,
                dynamicReceiver,
                IntentFilter(Day51Actions.dynamic(this)),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            isDynamicRegistered = true
            renderDynamicState()
            Day51DemoStateStore.record(
                this,
                "Activity -> registerReceiver(RECEIVER_NOT_EXPORTED)"
            )
        }

        findViewById<Button>(R.id.btnDay51SendDynamic).setOnClickListener {
            val action = Day51Actions.dynamic(this)
            sendBroadcast(Intent(action).setPackage(packageName))
            Day51DemoStateStore.record(this, "Send Dynamic Broadcast action=$action")
        }

        findViewById<Button>(R.id.btnDay51UnregisterDynamic).setOnClickListener {
            if (!isDynamicRegistered) {
                Day51DemoStateStore.record(
                    this,
                    "忽略重复 Unregister：当前未注册，避免 IllegalArgumentException"
                )
                return@setOnClickListener
            }
            unregisterReceiver(dynamicReceiver)
            isDynamicRegistered = false
            renderDynamicState()
            Day51DemoStateStore.record(this, "Activity -> unregisterReceiver")
        }

        findViewById<Button>(R.id.btnDay51SendStatic).setOnClickListener {
            val intent = Intent(this, Day51StaticReceiver::class.java)
                .setAction(Day51Actions.static(this))
            sendBroadcast(intent)
            Day51DemoStateStore.record(this, "Send Static Broadcast（显式组件）")
        }

        findViewById<Button>(R.id.btnDay51UnsafeThread).setOnClickListener {
            val intent = Intent(this, Day51UnsafeReceiver::class.java)
                .setAction(Day51Actions.unsafe(this))
            sendBroadcast(intent)
            Day51DemoStateStore.record(this, "Send Unsafe Broadcast：Receiver 将启动 10 秒普通 Thread")
        }

        findViewById<Button>(R.id.btnDay51ScheduleWork).setOnClickListener {
            val workId = Day51UploadWorker.enqueue(this, "Day51Activity")
            observeWork(workId)
            renderPersistentState()
        }

        findViewById<Button>(R.id.btnDay51ReceiverEnqueueWork).setOnClickListener {
            val intent = Intent(this, Day51WorkEnqueueReceiver::class.java)
                .setAction(Day51Actions.enqueueWork(this))
            sendBroadcast(intent)
            Day51DemoStateStore.record(this, "Send Receiver→WorkManager Broadcast")
        }

        findViewById<Button>(R.id.btnDay51RefreshState).setOnClickListener {
            renderPersistentState()
            observeSavedWork()
            Day51DemoStateStore.record(this, "手动刷新 Static / Worker 状态")
        }

        findViewById<Button>(R.id.btnDay51ClearLog).setOnClickListener {
            Day51DemoStateStore.clearEvents(this)
        }
    }

    private fun observeEventLog() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Day51DemoStateStore.events.collect { events ->
                    eventLogView.text = if (events.isEmpty()) {
                        "暂无日志，请从页面按钮开始实验。"
                    } else {
                        events.joinToString("\n")
                    }
                    renderPersistentState()
                }
            }
        }
    }

    private fun observeSavedWork() {
        val savedId = Day51DemoStateStore.workerSnapshot(this).workId ?: return
        runCatching { UUID.fromString(savedId) }
            .onSuccess(::observeWork)
            .onFailure {
                workStateView.text = "保存的 Work ID 无效：$savedId"
            }
    }

    private fun observeWork(workId: UUID) {
        observedWorkId = workId
        workStateView.text = "Work ID：$workId\nWork State：查询中…"
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(workId)
            .observe(this) { workInfo ->
                if (observedWorkId != workId) return@observe
                workStateView.text =
                    "Work ID：$workId\nWork State：${workInfo?.state ?: "尚未查到"}"
                renderPersistentState()
                if (workInfo != null) {
                    Day51DemoStateStore.record(
                        this,
                        "WorkInfo 官方状态：id=$workId, state=${workInfo.state}"
                    )
                }
            }
    }

    private fun renderDynamicState() {
        if (!::dynamicStateView.isInitialized) return
        dynamicStateView.text =
            "Registered = ${isDynamicRegistered.toYesNo()}\n" +
                "Received Count = $dynamicReceivedCount\n" +
                "Last Action = $dynamicLastAction\n" +
                "onReceive Thread = $dynamicLastThread"
    }

    private fun renderPersistentState() {
        if (!::staticStateView.isInitialized) return
        val staticSnapshot = Day51DemoStateStore.staticSnapshot(this)
        staticStateView.text =
            "Received Count = ${staticSnapshot.receivedCount}\n" +
                "Last Action = ${staticSnapshot.lastAction}\n" +
                "onReceive Thread = ${staticSnapshot.lastThread}\n" +
                "Last Process PID = ${staticSnapshot.lastPid.takeIf { it != 0 } ?: "—"}"

        val workerSnapshot = Day51DemoStateStore.workerSnapshot(this)
        workerResultView.text =
            "Last Work ID：${workerSnapshot.workId ?: "—"}\n" +
                "Last Worker Started：${Day51DemoStateStore.formatTime(workerSnapshot.lastStartedAt)}\n" +
                "Last Worker Finished：${Day51DemoStateStore.formatTime(workerSnapshot.lastFinishedAt)}\n" +
                "Last Worker Result：${workerSnapshot.lastResult}\n\n" +
                "SharedPreferences 只保存 Demo 展示结果，不是 WorkManager 可靠性的核心。"
    }

    private fun renderAdbCommands() {
        val applicationId = packageName
        val staticComponent = "$applicationId/${Day51StaticReceiver::class.java.name}"
        findViewById<TextView>(R.id.tvDay51AdbCommands).text =
            "先按 Home，然后执行（不要使用 force-stop）：\n\n" +
                "adb shell am kill $applicationId\n\n" +
                "adb shell am broadcast --receiver-foreground \\\n" +
                "  -a ${Day51Actions.static(this)} \\\n" +
                "  -n $staticComponent\n\n" +
                "--receiver-foreground 只让这次测试广播优先派发，\n" +
                "不会把 Receiver 变成前台 Service。\n\n" +
                "Unsafe / WorkManager 杀进程实验同样使用：\n" +
                "adb shell am kill $applicationId"
    }

    private fun Boolean.toYesNo(): String = if (this) "YES" else "NO"
}
