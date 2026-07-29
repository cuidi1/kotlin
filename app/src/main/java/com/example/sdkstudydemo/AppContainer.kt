package com.example.sdkstudydemo

import android.content.Context
//依赖容器
class AppContainer(
    private val appContext: Context
){
    //by lazy是kotlin懒加载，第一次用的时候才创建，创建一次后报错起来，以后直接返回同一个对象
    val sdkRepository: SdkRepository by lazy{
        SdkRepository(appContext)
    }
    val mainViewModelFactory: MainViewModelFactory by lazy {
        MainViewModelFactory(sdkRepository)
    }
}