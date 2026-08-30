package com.example.sdkstudydemo.ui.day51

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Day51WorkEnqueueReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Day51DemoStateStore.record(
            context,
            "Receiver→WorkManager：onReceive START [thread=${Thread.currentThread().name}]"
        )
        val workId = Day51UploadWorker.enqueue(context, "BroadcastReceiver")
        Day51DemoStateStore.record(context, "Receiver→WorkManager：enqueue id=$workId")
        Day51DemoStateStore.record(
            context,
            "Receiver→WorkManager：onReceive RETURN，可靠任务已交给 WorkManager"
        )
    }
}
