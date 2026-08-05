package com.example.sdkstudydemo

import com.example.sdkstudydemo.sdk.SdkEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SdkEventUploadClient : SdkEventUploadDataSource {
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    override suspend fun uploadEvent(event: SdkEvent): AppResult<Unit> {
//        return withContext(Dispatchers.IO) {
//            try {
//
//                val jsonString = event.toJsonString()
//                val requestBody = jsonString.toRequestBody(jsonMediaType)
//                val request = Request.Builder()
//                    .url("https://example.com/sdk/event/upload")
//                    .post(requestBody)
//                    .addHeader("SDK-Version", event.sdkVerSion)
//                    .addHeader("App-Id",event.appId)
//                    .addHeader("Content-Type", "application/json")
//                    .build()
//                val response = client.newCall(request).execute()
//                response.use{
//                        responseObj->
//                    val responseBody = responseObj.body?.string().orEmpty()
//                    if (!responseObj.isSuccessful) {
//                        return@withContext AppResult.Error(
//                            code = responseObj.code,
//                            message = "时间上报Http失败：${responseObj.code}"
//                        )
//                    }
//                    val json = JSONObject(responseBody)
//                    val businessCode = json.optInt("code", -1)
//                    val message = json.optString("message", "未知信息")
//                    if(businessCode != 0){
//                        return@withContext AppResult.Error(
//                            code= businessCode,
//                            message = "事件上报业务失败：$message"
//                        )
//                    }
//                    AppResult.Success(Unit)
//
//                }
//            } catch (e: IOException) {
//                AppResult.Exception(e)
//            } catch (e: Exception) {
//                AppResult.Exception(e)
//            }
//
//        }
        //只有一条事件的批量上传
        return uploadEvents(listOf(event))
    }

    override suspend fun uploadEvents(
        events: List<SdkEvent>
    ): AppResult<Unit> {
        if (events.isEmpty()) {
            return AppResult.Success(Unit)
        }

        return withContext(Dispatchers.IO) {
            try {
                val eventsJsonArray = JSONArray()

                for (event in events) {
                    eventsJsonArray.put(
                        JSONObject(event.toJsonString())
                    )
                }

                val rootJson = JSONObject()
                rootJson.put("events", eventsJsonArray)

                val requestBody = rootJson
                    .toString()
                    .toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url("https://example.com/sdk/event/upload/batch")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Event-Count", events.size.toString())
                    .build()

                val response = client.newCall(request).execute()

                response.use { responseObj ->
                    val responseBody = responseObj.body?.string().orEmpty()

                    if (!responseObj.isSuccessful) {
                        return@withContext AppResult.Error(
                            code = responseObj.code,
                            message = "批量事件上报 HTTP 失败：${responseObj.code}"
                        )
                    }

                    val json = JSONObject(responseBody)

                    val businessCode = json.optInt("code", -1)
                    val message = json.optString("message", "未知信息")

                    if (businessCode != 0) {
                        return@withContext AppResult.Error(
                            code = businessCode,
                            message = "批量事件上报业务失败：$message"
                        )
                    }

                    AppResult.Success(Unit)
                }
            } catch (e: IOException) {
                AppResult.Exception(e)
            } catch (e: Exception) {
                AppResult.Exception(e)
            }
        }
    }
}