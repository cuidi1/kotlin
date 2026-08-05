package com.example.sdkstudydemo

import com.example.sdkstudydemo.sdk.SdkEvent

interface SdkEventUploadDataSource {
    suspend fun uploadEvent(
        event: SdkEvent
    ): AppResult<Unit>
    //批量上传多条事件
    suspend fun uploadEvents(
        events: List<SdkEvent>
    ): AppResult<Unit>
}