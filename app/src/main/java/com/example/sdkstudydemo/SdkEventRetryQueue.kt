package com.example.sdkstudydemo
import com.example.sdkstudydemo.sdk.SdkEvent
import kotlin.math.max

class SdkEventRetryQueue(private val maxQueueSize : Int = 100) {
    private val pendingEvents = mutableListOf<SdkPendingEvent>()
    fun enqueue(
        event: SdkEvent,
        errorMessage: String
    ){
        if (pendingEvents.size>= maxQueueSize){
            pendingEvents.removeAt(0)
        }
        pendingEvents.add(
            SdkPendingEvent(
                event = event,
                lastErrorMessage = errorMessage
            )
        )
    }

    fun getPendingEvents(): List<SdkPendingEvent> {
        return pendingEvents.toList()
    }

    fun remove(
        pendingEventId: String
    ){
        pendingEvents.removeAll{
            pendingEvent-> pendingEvent.id == pendingEventId
        }
    }

    fun updateRetryFailure(
        pendingEventId:String,
        errorMessage: String
    ){
        val index = pendingEvents.indexOfFirst{
            pendingEvent -> pendingEvent.id==pendingEventId
        }
        if(index == -1) {
            return
        }

        val oldEvent = pendingEvents[index]
        pendingEvents[index] = oldEvent.copy(
            retryCount = oldEvent.retryCount + 1,
            lastErrorMessage = errorMessage
        )
    }
    fun size(): Int {
        return pendingEvents.size
    }

    fun clear() {
        pendingEvents.clear()
    }

    //toList()是为了返回一个新的列表副本，目的是保护内部队列，不让外部随便改
    fun getBatchEvents(batchSize: Int): List<SdkPendingEvent>{
        return pendingEvents.take(batchSize).toList()
    }

    fun removeAllByIds(pendingEventIds: List<String>){
        pendingEvents.removeAll { pendingEvent -> pendingEvent.id in pendingEventIds }
    }


}