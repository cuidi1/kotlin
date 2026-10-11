package com.example.sdkstudydemo.network

import android.util.Log
import com.google.gson.Gson

object DeviceJsonDemo {
    private const val TAG="DeviceJsonDemo"
    fun run() {
        //1、模拟服务器返回JSON
        val json ="""
            {
                "device_id":"ABC123",
                "battery":86,
                "online":true
            }
        """.trimIndent()
        //2、创建Gson
        val gson = Gson()

        //3、JSON转化成Gson
        val device = gson.fromJson(json, DeviceDto::class.java)

        //4、使用转化后的对象
        Log.d(TAG, "设备ID: ${device.deviceId}")
        Log.d(TAG, "电量：${device.battery}")
        Log.d(TAG, "是否在线：${device.online}")

        //5. DTO转回JSON
        val resultJson = gson.toJson(device)
        Log.d(TAG, "重新生成的JSON:$resultJson")

    }
}