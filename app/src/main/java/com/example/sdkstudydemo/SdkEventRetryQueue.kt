package com.example.sdkstudydemo
import com.example.sdkstudydemo.sdk.SdkEvent

class SdkEventRetryQueue {
    private val pendingEvents = mutableListOf<SdkPendingEvent>()
    fun enqueue(
        event: SdkEvent,
        errorMessage: String
    ){
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

        fun size(): Int {
            return pendingEvents.size
        }

        fun clear() {
            pendingEvents.clear()
        }
    }


}