package com.example.sdkstudydemo.app

import android.content.Context
import com.example.sdkstudydemo.event.store.DataStoreSdkPendingEventStore
import com.example.sdkstudydemo.event.policy.DefaultSdkBatchPolicy
import com.example.sdkstudydemo.event.policy.DefaultSdkQueueLimitPolicy
import com.example.sdkstudydemo.event.policy.DefaultSdkRetryPolicy
import com.example.sdkstudydemo.ui.main.MainViewModelFactory
import com.example.sdkstudydemo.event.policy.SdkBatchPolicy
import com.example.sdkstudydemo.event.queue.SdkEventRetryQueue
import com.example.sdkstudydemo.event.upload.SdkEventUploadClient
import com.example.sdkstudydemo.event.upload.SdkEventUploadDataSource
import com.example.sdkstudydemo.config.SdkNetworkClient
import com.example.sdkstudydemo.event.store.SdkPendingEventStore
import com.example.sdkstudydemo.event.policy.SdkQueueLimitPolicy
import com.example.sdkstudydemo.config.SdkRemoteCongfigDataSource
import com.example.sdkstudydemo.repository.SdkRepository
import com.example.sdkstudydemo.event.policy.SdkRetryPolicy

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
    val pendingEventStore: SdkPendingEventStore by lazy {
        DataStoreSdkPendingEventStore(appContext)
    }
    val eventRetryQueue: SdkEventRetryQueue by lazy{
        SdkEventRetryQueue(
            queueLimitPolicy,
            pendingEventStore
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