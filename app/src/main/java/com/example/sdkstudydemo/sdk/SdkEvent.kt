package com.example.sdkstudydemo.sdk

import org.json.JSONObject

data class SdkEvent (
    val eventName: String,
    val params: Map<String, String>,
    val appId:String,
    val environment: SdkEnvironment,
    val sdkVerSion: String,
    val timestamp: Long
){
    //转化json的方法
    fun toJsonString(): String{
        val json= JSONObject()
        json.put("eventName", eventName)
        json.put("appId", appId)
        json.put("environment", environment)
        json.put("timestamp", timestamp)
        val paramsJson = JSONObject()
        for((key,value ) in params){
            paramsJson.put(key, value)
        }
        json.put("params", paramsJson)
        return json.toString()
    }
//挂在类名上的工具方法
    companion object {

        fun fromJsonObject(
            json: JSONObject
        ): SdkEvent {
            val paramsJson = json.optJSONObject("params") ?: JSONObject()

            val params = mutableMapOf<String, String>()

            val keys = paramsJson.keys()

            while (keys.hasNext()) {
                val key = keys.next()
                val value = paramsJson.optString(key, "")
                params[key] = value
            }

            return SdkEvent(
                eventName = json.optString("eventName", ""),
                params = params,
                appId = json.optString("appId", ""),
                environment = SdkEnvironment.valueOf(
                    json.optString("environment", SdkEnvironment.TEST.name)
                ),
                json.optString("sdkVersion", ""),
                json.optLong("timestamp", System.currentTimeMillis())
            )
        }
    }
}