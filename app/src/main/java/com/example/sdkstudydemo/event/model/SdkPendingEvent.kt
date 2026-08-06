package com.example.sdkstudydemo.event.model

import com.example.sdkstudydemo.sdk.SdkEvent
import org.json.JSONObject
import java.util.UUID

/**
 * id：
 *     每个待重试事件的唯一标识。
 *
 * event：
 *     真正要上传的事件。
 *
 * retryCount：
 *     已经重试了几次。
 *
 * lastErrorMessage：
 *     上一次失败原因。
 *
 * createdAt：
 *     事件进入队列的时间。
 */
data class SdkPendingEvent(
    val id: String = UUID.randomUUID().toString(),
    val event: SdkEvent,
    val retryCount: Int = 0,
    val lastErrorMessage: String = "",
    val createdAt: Long = System.currentTimeMillis()
){

    fun toJsonObject(): JSONObject {
        val json = JSONObject()

        json.put("id", id)
        json.put("event", JSONObject(event.toJsonString()))
        json.put("retryCount", retryCount)
        json.put("lastErrorMessage", lastErrorMessage)
        json.put("createdAt", createdAt)

        return json
    }

    companion object {

        fun fromJsonObject(
            json: JSONObject
        ): SdkPendingEvent {
            val eventJson = json.optJSONObject("event") ?: JSONObject()

            return SdkPendingEvent(
                id = json.optString("id", UUID.randomUUID().toString()),
                event = SdkEvent.Companion.fromJsonObject(eventJson),
                retryCount = json.optInt("retryCount", 0),
                lastErrorMessage = json.optString("lastErrorMessage", ""),
                createdAt = json.optLong("createdAt", System.currentTimeMillis())
            )
        }
    }
}