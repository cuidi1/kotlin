package com.example.sdkstudydemo.ui.day51

import android.content.Context

object Day51Actions {
    fun dynamic(context: Context): String = "${context.packageName}.DAY51_DYNAMIC"
    fun static(context: Context): String = "${context.packageName}.DAY51_STATIC"
    fun unsafe(context: Context): String = "${context.packageName}.DAY51_UNSAFE"
    fun enqueueWork(context: Context): String = "${context.packageName}.DAY51_ENQUEUE_WORK"

    const val WORK_TAG = "DAY51_RELIABLE_WORK"
    const val WORK_INPUT_SOURCE = "source"
}
