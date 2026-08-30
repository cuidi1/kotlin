package com.example.sdkstudydemo.ui.day51

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Process

class Day51StaticReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: "null"
        val thread = Thread.currentThread().name
        val pid = Process.myPid()
        val count = Day51DemoStateStore.recordStaticReceipt(context, action, thread, pid)
        Day51DemoStateStore.record(
            context,
            "StaticReceiver.onReceive action=$action, count=$count, thread=$thread, pid=$pid"
        )
    }
}
