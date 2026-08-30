package com.example.sdkstudydemo.ui.day51

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.UUID
import java.util.concurrent.TimeUnit

class Day51UploadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val source = inputData.getString(Day51Actions.WORK_INPUT_SOURCE) ?: "未知来源"
        Day51DemoStateStore.recordWorkerStarted(applicationContext)
        Day51DemoStateStore.record(
            applicationContext,
            "Worker START source=$source [thread=${Thread.currentThread().name}]"
        )

        return try {
            // 模拟短暂的“可靠上传日志”，运行在线程池，不阻塞主线程。
            Thread.sleep(1_500)
            Day51DemoStateStore.recordWorkerFinished(applicationContext, "SUCCESS")
            Day51DemoStateStore.record(
                applicationContext,
                "Worker SUCCESS source=$source [thread=${Thread.currentThread().name}]"
            )
            Result.success()
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            Day51DemoStateStore.recordWorkerFinished(applicationContext, "INTERRUPTED → RETRY")
            Day51DemoStateStore.record(applicationContext, "Worker INTERRUPTED，返回 retry()")
            Result.retry()
        }
    }

    companion object {
        fun enqueue(context: Context, source: String): UUID {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val inputData = Data.Builder()
                .putString(Day51Actions.WORK_INPUT_SOURCE, source)
                .build()
            val request = OneTimeWorkRequest.Builder(Day51UploadWorker::class.java)
                .setConstraints(constraints)
                .setInitialDelay(12, TimeUnit.SECONDS)
                .setInputData(inputData)
                .addTag(Day51Actions.WORK_TAG)
                .build()

            WorkManager.getInstance(context.applicationContext).enqueue(request)
            Day51DemoStateStore.saveLatestWorkId(context, request.id.toString())
            Day51DemoStateStore.record(
                context,
                "Work ENQUEUED id=${request.id} source=$source，网络约束=CONNECTED，initialDelay=12秒"
            )
            return request.id
        }
    }
}
