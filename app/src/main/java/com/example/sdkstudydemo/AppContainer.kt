package com.example.sdkstudydemo

import android.content.Context
//依赖容器
class AppContainer(
    private val appContext: Context
){
    val remoteCongfigDataSource: SdkRemoteCongfigDataSource by lazy{
        SdkNetworkClient()
    }
    val eventUploadDataSource: SdkEventUploadDataSource by lazy {
        SdkEventUploadClient()
    }
    val sdkNetworkClient: SdkNetworkClient by lazy{
        SdkNetworkClient()
    }

    val retryPolicy: SdkRetryPolicy by lazy{
        DefaultSdkRetryPolicy(3)
    }
    val batchPolicy: SdkBatchPolicy by lazy{
        DefaultSdkBatchPolicy(
            10
        )
    }
    val queueLimitPolicy: SdkQueueLimitPolicy by lazy{
        DefaultSdkQueueLimitPolicy(
            100
        )
    }
    val eventRetryQueue: SdkEventRetryQueue by lazy{
        SdkEventRetryQueue(
            queueLimitPolicy
        )
    }
    //by lazy是kotlin懒加载，第一次用的时候才创建，创建一次后报错起来，以后直接返回同一个对象
    val sdkRepository: SdkRepository by lazy{
        SdkRepository(
            context = appContext,
            remoteConfigDataSource = remoteCongfigDataSource,
            eventUploadDataSource = eventUploadDataSource,
            eventRetryQueue = eventRetryQueue,
            retryPolicy,
            batchPolicy
        )
    }
    val mainViewModelFactory: MainViewModelFactory by lazy {
        MainViewModelFactory(sdkRepository)
    }
}