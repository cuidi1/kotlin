package com.example.sdkstudydemo.sdk

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

object SdkEventUploader {
    //网络请求客户端
    private val client = OkHttpClient()
    //上传地址
    private const val UPLOAD_URL = "https://postman-echo.com/post"
    //Json请求体类型
    private val JSON_MEDTA_TYPE= "application/json;charset=utf-8".toMediaType()

    fun upload(
        event: SdkEvent,
        callback: SdkUploadCallback
    ){
        //把sdkevent转化成json字符串
        val jsonString = event.toJsonString()
        //再把Json字符串变成Http请求体
        val requestBody = jsonString.toRequestBody(JSON_MEDTA_TYPE)
        //构造post请求
        val request = Request.Builder()
            .url(UPLOAD_URL)
            .post(requestBody)
            .build()
        //发起异步请求
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e.message?:"网络请求失败")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use{
                    val responseBody = it.body?.string()?:""
                    if(it.isSuccessful){
                        callback.onSuccess(responseBody.take(200))
                    }else{
                        callback.onFailure("HTTP ${it.code}:${responseBody.take(200)}")
                    }
                }
            }
        })
    }

    fun uploadSync(event: SdkEvent): SdkUploadResult {
        return try{
            val jsonString = event.toJsonString()
            val requestBody = jsonString.toRequestBody(JSON_MEDTA_TYPE)
            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()
            val response = client.newCall(request).execute()
            response.use{
                val responseBody = it.body?.string()?:""
                if(it.isSuccessful){
                    SdkUploadResult(true, "上传成功", responseBody.take(200))
                }else{
                    SdkUploadResult(false, "HTTP ${it.code}:${responseBody.take(200)}")
                }
            }

        }catch (e : Exception) {
            SdkUploadResult(false, e.message ?: "未知错误")
        }
    }
}