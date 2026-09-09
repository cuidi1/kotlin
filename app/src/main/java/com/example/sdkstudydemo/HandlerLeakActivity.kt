package com.example.sdkstudydemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import android.os.Handler
import android.os.Looper
class HandlerLeakActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("HandlerLeak", "onCreate：$this")

        handler.postDelayed(
            {
                Log.d(
                    "HandlerLeak",
                    """
                    延迟任务执行
                    activity = ${this@HandlerLeakActivity}
                    isDestroyed = $isDestroyed
                    """.trimIndent()
                )
            },
            10000
        )
    }

    override fun onDestroy() {
        Log.d(
            "HandlerLeak",
            "onDestroy：$this"
        )

        super.onDestroy()
    }
}