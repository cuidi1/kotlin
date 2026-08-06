package com.example.sdkstudydemo.config

import com.example.sdkstudydemo.core.AppResult

//定义一个远程配置数据源接口
//谁实现了这个接口，谁就必须提供fetchRemoteConfig
//这个接口只规定能力，你必须能获取远程配置，但是不管你怎么获取
//这里接口里的方法没有方法体，说明这是一个抽象方法，接口只提要求，不做具体实现，实现类必须自己写具体逻辑
interface SdkRemoteCongfigDataSource {
    suspend fun fetchRemoteConfig(
        mode: Int
    ): AppResult<SdkRemoteConfig>
}