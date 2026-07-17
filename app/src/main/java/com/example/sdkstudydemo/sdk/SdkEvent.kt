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
}