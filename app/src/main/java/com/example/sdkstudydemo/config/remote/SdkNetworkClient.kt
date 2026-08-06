package com.example.sdkstudydemo.config.remote

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


