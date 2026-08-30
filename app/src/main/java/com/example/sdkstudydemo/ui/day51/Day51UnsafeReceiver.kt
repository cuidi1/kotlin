package com.example.sdkstudydemo.ui.day51

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Day51UnsafeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        Day51DemoStateStore.record(
            appContext,
            "UNSAFE onReceive START [thread=${Thread.currentThread().name}]"
        )

        Thread({
            try {
                for (second in 1..10) {
                    Thread.sleep(1_000)
                    Day51DemoStateStore.record(
                        appContext,
                        "UNSAFE Thread running: $second/10 [thread=${Thread.currentThread().name}]"
                    )
                }
                Day51DemoStateStore.record(appContext, "UNSAFE Thread FINISHED")
            } catch (exception: InterruptedException) {
                Thread.currentThread().interrupt()
                Day51DemoStateStore.record(appContext, "UNSAFE Thread INTERRUPTED")
            }
        }, "Day51-Unsafe-Thread").start()

        Day51DemoStateStore.record(
            appContext,
            "UNSAFE onReceive RETURN：普通 Thread 已启动，但系统没有可靠执行保证"
        )
    }
}
