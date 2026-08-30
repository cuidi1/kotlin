package com.example.sdkstudydemo.ui.day51

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Day51DynamicReceiver(
    private val onReceived: (action: String, thread: String) -> Int
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: "null"
        val thread = Thread.currentThread().name
        val count = onReceived(action, thread)
        Day51DemoStateStore.record(
            context,
            "DynamicReceiver.onReceive action=$action, count=$count, thread=$thread"
        )
    }
}
