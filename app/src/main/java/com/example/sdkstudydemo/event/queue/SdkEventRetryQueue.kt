package com.example.sdkstudydemo.event.queue

import com.example.sdkstudydemo.event.store.SdkPendingEventStore
import com.example.sdkstudydemo.event.policy.SdkQueueLimitPolicy
import com.example.sdkstudydemo.event.model.SdkPendingEvent
import com.example.sdkstudydemo.sdk.SdkEvent
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SdkEventRetryQueue(
    private val queueLimitPolicy: SdkQueueLimitPolicy,
    private val pendingEventStore: SdkPendingEventStore
) {
    private val pendingEvents = mutableListOf<SdkPendingEvent>()
    private val queueMutex = Mutex()
    //是否从DataStore里恢复过
    private var restored = false

    private  suspend fun ensureRestoredLocked(){
        if(restored){
            return
        }
        val localEvents = pendingEventStore.loadPendingEvents()
        pendingEvents.clear()
        pendingEvents.addAll(localEvents)
        restored = true
    }

    private suspend fun persistLocked(){
        pendingEventStore.savePendingEvents(pendingEvents.toList())
    }
    suspend fun enqueue(
        event: SdkEvent,
        errorMessage: String
    ){
        queueMutex.withLock {
//            val pendingEvents = pendingEventStore.loadPendingEvents().toMutableList()
           ensureRestoredLocked()
            if (queueLimitPolicy.shouldRemoveOldest(pendingEvents.size)){
                pendingEvents.removeAt(0)
            }
            pendingEvents.add(
                SdkPendingEvent(
                    event = event,
                    lastErrorMessage = errorMessage
                )
            )
            persistLocked()
        }

    }

    suspend fun getPendingEvents(): List<SdkPendingEvent> {
        return queueMutex.withLock {
            pendingEvents.toList()
        }
    }

    suspend fun remove(
        pendingEventId: String
    ){
        queueMutex.withLock {
            val pendingEvents=pendingEventStore.loadPendingEvents().toMutableList()
            pendingEvents.removeAll{
                pendingEvent-> pendingEvent.id == pendingEventId}
            pendingEventStore.savePendingEvents(pendingEvents)
        }
    }

    suspend fun updateRetryFailure(
        pendingEventId:String,
        errorMessage: String
    ){
        queueMutex.withLock {
            ensureRestoredLocked()
            val index = pendingEvents.indexOfFirst{
                    pendingEvent -> pendingEvent.id==pendingEventId
            }
            if(index == -1) {
                return@withLock
            }

            val oldEvent = pendingEvents[index]
            pendingEvents[index] = oldEvent.copy(
                retryCount = oldEvent.retryCount + 1,
                lastErrorMessage = errorMessage
            )
            persistLocked()
        }

    }
    suspend fun size(): Int {
        return pendingEvents.size
    }

    suspend fun clear() {
        queueMutex.withLock {
            pendingEventStore.clear()
            pendingEvents.clear()
        }

    }

    //toList()是为了返回一个新的列表副本，目的是保护内部队列，不让外部随便改
    suspend fun getBatchEvents(batchSize: Int): List<SdkPendingEvent>{
        return queueMutex.withLock {
            //不需要每次都从DataStore里读
            ensureRestoredLocked()
            pendingEvents.take(batchSize).toList()}
    }

    suspend fun removeAllByIds(pendingEventIds: List<String>){
        queueMutex.withLock {
            ensureRestoredLocked()
            pendingEvents.removeAll { pendingEvent -> pendingEvent.id in pendingEventIds }
            persistLocked()
        }
    }


}