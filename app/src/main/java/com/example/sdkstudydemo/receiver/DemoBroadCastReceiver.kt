package com.example.sdkstudydemo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sdkstudydemo.core.SdkLogger

class DemoBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message").orEmpty()
        SdkLogger.d("DemoBroadcastReceiver 收到广播，message=$message")
    }
}