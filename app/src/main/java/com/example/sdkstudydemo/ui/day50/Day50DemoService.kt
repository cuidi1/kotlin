package com.example.sdkstudydemo.ui.day50

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class Day50DemoService : Service() {

    private val localBinder = LocalBinder()
    private var count = 0

    inner class LocalBinder : Binder() {
        fun getService(): Day50DemoService = this@Day50DemoService
    }

    override fun onCreate() {
        super.onCreate()
        Day50ServiceLog.markServiceCreated(serviceIdentity, currentThread)
        record("Service.onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Day50ServiceLog.markServiceStarted(serviceIdentity, currentThread)
        record("Service.onStartCommand startId=$startId")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        Day50ServiceLog.markServiceBound(serviceIdentity, currentThread)
        record("Service.onBind，返回 LocalBinder")
        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Day50ServiceLog.markServiceUnbound(currentThread)
        record("Service.onUnbind")

        // false：同一 Service 实例下次重新绑定时会再次调用 onBind，而不是 onRebind。
        return false
    }

    override fun onDestroy() {
        record("Service.onDestroy")
        Day50ServiceLog.markServiceDestroyed(currentThread)
        super.onDestroy()
    }

    fun increaseCount() {
        count++
        record("Binder 调用 increaseCount，count=$count")
    }

    fun getCount(): Int {
        record("Binder 调用 getCount，count=$count")
        return count
    }

    private val serviceIdentity: Int
        get() = System.identityHashCode(this)

    private val currentThread: String
        get() = Thread.currentThread().name

    private fun record(callback: String) {
        val message = "$callback [thread=$currentThread, serviceId=$serviceIdentity]"
        Log.d(TAG, message)
        Day50ServiceLog.addEvent(message)
    }

    private companion object {
        const val TAG = "Day50"
    }
}
