package com.example.sdkstudydemo.ui.day50

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sdkstudydemo.R
import kotlinx.coroutines.launch

class Day50Activity : AppCompatActivity() {

    private lateinit var stateView: TextView
    private lateinit var binderResultView: TextView
    private lateinit var eventLogView: TextView
    private lateinit var eventLogScrollView: NestedScrollView
    private lateinit var readCountButton: Button
    private lateinit var increaseCountButton: Button

    private var demoService: Day50DemoService? = null
    private var isBound = false

    private val serviceIntent: Intent
        get() = Intent(this, Day50DemoService::class.java)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as Day50DemoService.LocalBinder
            demoService = binder.getService()
            Day50ServiceLog.markBinderConnected(true)

            val serviceIdentity = System.identityHashCode(demoService)
            recordActivityEvent(
                "Activity.onServiceConnected：拿到 LocalBinder，serviceId=$serviceIdentity"
            )
            binderResultView.text = "Binder 已连接，可读取或修改 Service count"
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            demoService = null
            Day50ServiceLog.markBinderConnected(false)
            recordActivityEvent(
                "Activity.onServiceDisconnected：连接异常丢失；主动 unbind 通常不会调用这里"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day50)

        stateView = findViewById(R.id.tvDay50ServiceState)
        binderResultView = findViewById(R.id.tvDay50BinderResult)
        eventLogView = findViewById(R.id.tvDay50EventLog)
        eventLogScrollView = findViewById(R.id.day50EventLogScrollView)
        readCountButton = findViewById(R.id.btnDay50ReadCount)
        increaseCountButton = findViewById(R.id.btnDay50IncreaseCount)

        setupButtons()
        observeLearningState()
        recordActivityEvent("Activity.onCreate [thread=${Thread.currentThread().name}]")
    }

    override fun onStart() {
        super.onStart()
        recordActivityEvent("Activity.onStart [thread=${Thread.currentThread().name}]")
    }

    override fun onResume() {
        super.onResume()
        recordActivityEvent("Activity.onResume [thread=${Thread.currentThread().name}]")
    }

    override fun onPause() {
        recordActivityEvent("Activity.onPause [thread=${Thread.currentThread().name}]")
        super.onPause()
    }

    override fun onStop() {
        recordActivityEvent("Activity.onStop [thread=${Thread.currentThread().name}]")
        super.onStop()
    }

    override fun onDestroy() {
        recordActivityEvent("Activity.onDestroy [thread=${Thread.currentThread().name}]")

        if (isBound) {
            // Activity 销毁只清理自己的 Bound 连接，不能顺便停止 Started Service。
            safeUnbind("Activity.onDestroy 自动 unbind；没有调用 stopService")
        }

        super.onDestroy()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnDay50StartService).setOnClickListener {
            startDemoService("点击 Start Service")
        }

        findViewById<Button>(R.id.btnDay50StartAgain).setOnClickListener {
            startDemoService("点击 Start Service Again")
        }

        findViewById<Button>(R.id.btnDay50StopService).setOnClickListener {
            val stopped = stopService(serviceIntent)
            Day50ServiceLog.markStopRequested()
            recordActivityEvent(
                "点击 Stop Service：stopService() 返回 $stopped；只移除 Started 理由，不会自动 unbind"
            )
        }

        findViewById<Button>(R.id.btnDay50BindService).setOnClickListener {
            if (isBound) {
                recordActivityEvent("忽略重复 Bind：当前 Activity 已注册连接")
                return@setOnClickListener
            }

            recordActivityEvent("点击 Bind Service：调用 bindService(BIND_AUTO_CREATE)")
            isBound = bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
            if (!isBound) {
                recordActivityEvent("bindService() 返回 false，连接未建立")
            }
        }

        findViewById<Button>(R.id.btnDay50UnbindService).setOnClickListener {
            if (!isBound) {
                recordActivityEvent("忽略重复 Unbind：当前没有已注册连接，避免异常")
                return@setOnClickListener
            }
            safeUnbind("点击 Unbind Service：只解除 Bound 理由，不会自动 stopService")
        }

        readCountButton.setOnClickListener {
            val count = demoService?.getCount()
            if (count == null) {
                binderResultView.text = "Binder 未连接，无法读取 count"
            } else {
                binderResultView.text = "Service count = $count"
                recordActivityEvent("Activity 通过 Binder 读取 Service count=$count")
            }
        }

        increaseCountButton.setOnClickListener {
            val service = demoService
            if (service == null) {
                binderResultView.text = "Binder 未连接，无法修改 count"
            } else {
                service.increaseCount()
                val count = service.getCount()
                binderResultView.text = "Service count = $count"
                recordActivityEvent("Activity 通过 Binder 执行 count +1，当前 count=$count")
            }
        }

        findViewById<Button>(R.id.btnDay50ClearLog).setOnClickListener {
            Day50ServiceLog.clearEvents()
        }
    }

    private fun startDemoService(actionName: String) {
        val componentName = startService(serviceIntent)
        Day50ServiceLog.markStartRequested()
        recordActivityEvent("$actionName：startService() 返回 $componentName")
    }

    private fun safeUnbind(reason: String) {
        if (!isBound) return

        try {
            unbindService(connection)
            recordActivityEvent(reason)
        } catch (exception: IllegalArgumentException) {
            recordActivityEvent("unbindService 被系统判定为未注册：${exception.message}")
        } finally {
            isBound = false
            demoService = null
            Day50ServiceLog.markBinderConnected(false)
            binderResultView.text = "Binder 未连接"
        }
    }

    private fun observeLearningState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    Day50ServiceLog.state.collect(::renderState)
                }
                launch {
                    Day50ServiceLog.events.collect { events ->
                        eventLogView.text = if (events.isEmpty()) {
                            "暂无日志，请从上方按钮开始实验。"
                        } else {
                            events.joinToString(separator = "\n")
                        }
                        eventLogScrollView.post {
                            eventLogScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
                        }
                    }
                }
            }
        }
    }

    private fun renderState(state: Day50ServiceState) {
        val hasReason = state.started || state.bound
        stateView.text = buildString {
            appendLine("Service 对象：${if (state.serviceExists) "已创建" else "不存在"}")
            appendLine("Started 状态：${state.started.toYesNo()}")
            appendLine("Bound 状态：${state.bound.toYesNo()}")
            appendLine("Activity 是否拿到 Binder：${state.binderConnected.toYesNo()}")
            appendLine("Service identityHashCode：${state.serviceIdentity ?: "—"}")
            appendLine("Service 最近回调线程：${state.currentThread}")
            appendLine()
            appendLine("【存活判断】")
            append(
                if (hasReason) {
                    "Started 或 Bound 至少一个为 YES → Service 仍有存活理由"
                } else {
                    "Started 和 Bound 都为 NO → Service 可以销毁"
                }
            )
        }

        val binderAvailable = state.binderConnected && demoService != null
        readCountButton.isEnabled = binderAvailable
        increaseCountButton.isEnabled = binderAvailable
    }

    private fun recordActivityEvent(message: String) {
        Log.d(TAG, message)
        Day50ServiceLog.addEvent(message)
    }

    private fun Boolean.toYesNo(): String = if (this) "YES" else "NO"

    private companion object {
        const val TAG = "Day50"
    }
}
