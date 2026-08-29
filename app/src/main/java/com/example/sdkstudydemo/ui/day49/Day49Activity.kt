package com.example.sdkstudydemo.ui.day49

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sdkstudydemo.R

class Day49Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day49)

        val managerIdentity = System.identityHashCode(supportFragmentManager)
        Log.d(
            TAG,
            "Day49Activity.supportFragmentManager identityHashCode=$managerIdentity"
        )
        findViewById<TextView>(R.id.tvDay49ActivityDebug).text =
            "Activity.supportFragmentManager identityHashCode：$managerIdentity"

        if (savedInstanceState == null) {
            // replace：替换容器中已有 Fragment；commit：把 Transaction 提交给 FragmentManager。
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.day49FragmentContainer, Day49FragmentA())
                .commit()
            Log.d(TAG, "首次进入：replace Day49FragmentA，然后 commit")
        } else {
            Log.d(TAG, "Activity 重建：由 FragmentManager 恢复 Fragment，不重复添加 A")
        }
    }

    private companion object {
        const val TAG = "Day49"
    }
}
