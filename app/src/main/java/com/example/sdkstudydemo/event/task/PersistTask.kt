package com.example.sdkstudydemo.event.task

import com.example.sdkstudydemo.event.model.SdkPendingEvent

data class PersistTask(
    val version: Long,
    val events: List<SdkPendingEvent>
)
