package com.example.sdkstudydemo.network
import com.google.gson.annotations.SerializedName
data class DeviceDto(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("battery")
    val battery:Int,
    @SerializedName("online")
    val online: Boolean
)
