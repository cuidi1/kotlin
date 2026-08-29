package com.example.sdkstudydemo.ui.day48

import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sdkstudydemo.R

class Day48Activity : AppCompatActivity() {

    private lateinit var lifecycleLogView: TextView
    private lateinit var lifecycleLogScrollView: ScrollView

    private val activityIdentity: Int
        get() = System.identityHashCode(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(
            TAG,
            "Day48Activity.onCreate pid=${Process.myPid()}, " +
                "activityIdentityHashCode=$activityIdentity, " +
                "savedInstanceStateIsNull=${savedInstanceState == null}, " +
                "applicationIdentityHashCode=${System.identityHashCode(application)}"
        )

        setContentView(R.layout.activity_day48)

        lifecycleLogView = findViewById(R.id.tvDay48LifecycleLog)
        lifecycleLogScrollView = findViewById(R.id.day48LifecycleLogScrollView)
        findViewById<Button>(R.id.btnDay48ClearLog).setOnClickListener {
            lifecycleLogView.text = ""
        }

        findViewById<TextView>(R.id.tvDay48ProcessInfo).text =
            "Process PID: ${Process.myPid()}"
        findViewById<TextView>(R.id.tvDay48ActivityInfo).text =
            "Activity identityHashCode: $activityIdentity"
        findViewById<Button>(R.id.btnDay48Recreate).setOnClickListener {
            Log.d(TAG, "点击 recreate Activity: oldActivityIdentityHashCode=$activityIdentity")
            recreate()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.day48FragmentContainer, Day48FragmentA())
                .commit()
            Log.d(TAG, "首次创建 Activity，添加 Day48FragmentA")
        } else {
            Log.d(TAG, "Activity 重建，复用 FragmentManager 恢复的 Fragment，不重复添加")
        }
    }

    fun appendLifecycleLog(message: String) {
        if (!::lifecycleLogView.isInitialized) return
        lifecycleLogView.append("$message\n")
        lifecycleLogScrollView.post {
            lifecycleLogScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Day48Activity.onStart activityIdentityHashCode=$activityIdentity")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Day48Activity.onResume activityIdentityHashCode=$activityIdentity")
    }

    override fun onPause() {
        Log.d(TAG, "Day48Activity.onPause activityIdentityHashCode=$activityIdentity")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "Day48Activity.onStop activityIdentityHashCode=$activityIdentity")
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "Day48Activity.onRestart activityIdentityHashCode=$activityIdentity")
    }

    override fun onDestroy() {
        Log.d(
            TAG,
            "Day48Activity.onDestroy activityIdentityHashCode=$activityIdentity, " +
                "isFinishing=$isFinishing, isChangingConfigurations=$isChangingConfigurations"
        )
        super.onDestroy()
    }

    private companion object {
        const val TAG = "Day48"
    }
}
