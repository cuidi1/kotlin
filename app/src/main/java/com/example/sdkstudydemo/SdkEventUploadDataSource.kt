package com.example.sdkstudydemo

import com.example.sdkstudydemo.sdk.SdkEvent

interface SdkEventUploadDataSource {
    suspend fun uploadEvent(
        event: SdkEvent
    ): AppResult<Unit>
}