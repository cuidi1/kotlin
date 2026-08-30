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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sdkstudydemo.R
import kotlinx.coroutines.launch

class Day50Activity : AppCompatActivity() {

    private lateinit var stateView: TextView
    private lateinit var binderResultView: TextView
    private lateinit var eventLogView: TextView
    private lateinit var destroyExperimentHintView: TextView
    private lateinit var readCountButton: Button
    private lateinit var increaseCountButton: Button

    private var demoService: Day50DemoService? = null
    private var isBound = false
    private var didStartServiceInThisActivity = false

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
        destroyExperimentHintView = findViewById(R.id.tvDay50DestroyExperimentHint)
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

        val wasBound = isBound
        if (isBound) {
            // Activity 销毁只清理自己的 Bound 连接，不能顺便停止 Started Service。
            safeUnbind(
                "Activity.onDestroy：主动 unbindService()；这里只解除 Bound 连接，" +
                    "不会调用 stopService，所以 Started 状态应该继续存在"
            )
        }

        if (wasBound) {
            val stateAfterUnbind = Day50ServiceLog.state.value
            val message = buildString {
                appendLine("Activity 已销毁并执行 unbindService()")
                appendLine(
                    "Started=${stateAfterUnbind.started.toYesNo()}，" +
                        "Bound=${stateAfterUnbind.bound.toYesNo()}"
                )
                append(
                    when {
                        stateAfterUnbind.started && didStartServiceInThisActivity ->
                            "本次 Activity 调用过 startService()"

                        stateAfterUnbind.started ->
                            "本次 Activity 没有调用 Start；Service 在进入本页面前就已经 Started"

                        else ->
                            "当前没有 Started 存活理由"
                    }
                )
                if (stateAfterUnbind.started && !stateAfterUnbind.bound) {
                    append("\nService 仍因 Started 理由存活")
                }
            }
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
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
            bindDemoService("点击 Bind Service")
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

        findViewById<Button>(R.id.btnDay50StartAndBind).setOnClickListener {
            recordActivityEvent("实验5：点击 Start + Bind")

            if (!Day50ServiceLog.state.value.started) {
                startDemoService("实验5：调用 Start Service")
            } else {
                recordActivityEvent("实验5：Started 已经是 YES，不重复 startService")
            }

            if (!isBound) {
                bindDemoService("实验5：调用 Bind Service")
            } else {
                recordActivityEvent("实验5：当前 Activity 已绑定，不重复 bindService")
            }
        }

        findViewById<Button>(R.id.btnDay50FinishActivity).setOnClickListener {
            recordActivityEvent("实验5：点击 Finish Activity；只调用 finish()，没有 stopService()")
            finish()
        }
    }

    private fun startDemoService(actionName: String) {
        val componentName = startService(serviceIntent)
        didStartServiceInThisActivity = true
        Day50ServiceLog.markStartRequested()
        recordActivityEvent("$actionName：startService() 返回 $componentName")
        renderState(Day50ServiceLog.state.value)
    }

    private fun bindDemoService(actionName: String) {
        if (isBound) {
            recordActivityEvent("忽略重复 Bind：当前 Activity 已注册连接")
            return
        }

        recordActivityEvent("$actionName：调用 bindService(BIND_AUTO_CREATE)")
        isBound = bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        if (!isBound) {
            recordActivityEvent("bindService() 返回 false，连接未建立")
        }
    }

    private fun safeUnbind(reason: String) {
        if (!isBound) return

        recordActivityEvent(reason)
        try {
            unbindService(connection)
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
            appendLine(
                "本次 Activity 是否调用过 startService：" +
                    didStartServiceInThisActivity.toYesNo()
            )
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

        destroyExperimentHintView.text = when {
            state.started && state.bound && state.binderConnected ->
                "当前已确认：Started = YES，Bound = YES。\n下一步点击 Finish Activity。"

            state.started && !state.bound ->
                "Service 之前已经被 Start，但当前 Activity 尚未绑定：\n" +
                    "Started = YES，Bound = NO。\n" +
                    "这正是 Activity 销毁并 unbind 后重新进入时应看到的状态。"

            state.started && state.bound ->
                "Started = YES，Bound = YES，正在等待 onServiceConnected。"

            !state.started && state.bound ->
                "当前只有 Bound 理由。请先 Start，再做 Activity 销毁实验。"

            else ->
                "请先点击“Start + Bind，然后销毁 Activity”。"
        }
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
