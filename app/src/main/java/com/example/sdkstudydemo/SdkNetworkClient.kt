package com.example.sdkstudydemo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject

//class SdkNetworkClient : SdkRemoteCongfigDataSource {
//    override suspend fun fetchRemoteConfig(
//        mode: Int
//    ): AppResult<SdkRemoteConfig>{
//        delay(1500)
//
//        return when(mode) {
//            0->{
//                AppResult.Success(
//                    SdkRemoteConfig(
//                        enableUpload = true,
//                        sampleRate = 100,
//                        configVersion = "remote_1.0.0"
//                    )
//                )
//            }
//            1->{
//                AppResult.Error(
//                    code = 4001,
//                    message = "服务器返回配置失败"
//                )
//
//            }
//            2->{
//                AppResult.Exception(
//                    IOException("网络连接异常")
//                )
//            }
//            else ->{
//                delay(3000)
//                AppResult.Success(
//                    SdkRemoteConfig(
//                        enableUpload = true,
//                        sampleRate = 50,
//                        configVersion = "remote_slow"
//                    )
//                )
//            }
//        }
//    }
//}


//OKHttp版本的
class SdkNetworkClient: SdkRemoteCongfigDataSource{
    private val client = OkHttpClient()
    override suspend fun fetchRemoteConfig(
        mode: Int
    ) : AppResult<SdkRemoteConfig>{
        //withContext的意思是把大括号里的内容切换到IO线程里去
        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder()
                    .url("https://example.com/sdk/config?mode=$mode")
                    .get()
                    .addHeader("SDK-Version", "1.0.0")
                    .addHeader("Content-Type", "application/json")
                    .build()
                //真正发起网络请求
                val response = client.newCall(request).execute()
                //use是Kotlin里边提供的资源管理写法
                response.use{responseObj->
                    val responseBody = responseObj.body?.string().orEmpty()
                    //里面的 responseObj 就是 response 本身。
                    if(!responseObj.isSuccessful) {
                        return@withContext AppResult.Error(
                            code = responseObj.code,
                            message = "HTTP 请求你失败：${responseObj.code}"
                        )
                    }

                    val json = JSONObject(responseBody)
                    val businessCode = json.optInt("code", -1)
                    val message = json.optString("message", "未知信息")

                    if(businessCode != 0){
                        //我要从这个 withContext 代码块里返回结果。
                        return@withContext AppResult.Error(
                            code=businessCode,
                            message=message
                        )
                    }
                    val data = json.optJSONObject("data")

                    if(data==null){
                        return@withContext AppResult.Error(
                            code = 4002,
                            message = "远程配置data为空"
                        )
                    }

                    val config = SdkRemoteConfig(
                        enableUpload = data.optBoolean("enableUpload", false),
                        sampleRate = data.optInt("sampleRate", 10),
                        configVersion = data.optString("configVersion", "unknown")
                    )

                    AppResult.Success(config);
                }
            }catch (e: IOException){
                AppResult.Exception(e)
            }catch (e: Exception) {
                AppResult.Exception(e)
            }



        }
    }
}