package com.example.sdkstudydemo.event.store

import com.example.sdkstudydemo.event.model.SdkPendingEvent

//都是suspend 因为本地读写可能涉及io
interface SdkPendingEventStore{
    suspend fun loadPendingEvents(): List<SdkPendingEvent>
    suspend fun savePendingEvents(
        pendingEvents:List<SdkPendingEvent>
    )
    suspend fun clear()
}