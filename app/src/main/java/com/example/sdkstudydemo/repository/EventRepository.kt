package com.example.sdkstudydemo.repository

import com.example.sdkstudydemo.local.SdkEventDao
import com.example.sdkstudydemo.local.SdkEventEntity
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val dao: SdkEventDao
) {
    val eventsFlow: Flow<List<SdkEventEntity>> = dao.observeAll()
    suspend fun saveEvent(name: String){
        dao.insert(
            SdkEventEntity(eventName = name, timestamp = System.currentTimeMillis())
        )
    }
}