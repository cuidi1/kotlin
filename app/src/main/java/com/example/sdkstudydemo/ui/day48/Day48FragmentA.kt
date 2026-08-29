package com.example.sdkstudydemo.ui.day48

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sdkstudydemo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Day48FragmentA : Fragment(R.layout.fragment_day48_a) {

    private val fragmentIdentity: Int
        get() = System.identityHashCode(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate | Fragment=$fragmentIdentity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewIdentity = System.identityHashCode(view)
        log(
            "onViewCreated | Fragment=$fragmentIdentity | View=$viewIdentity"
        )

        view.findViewById<TextView>(R.id.tvDay48FragmentInfo).text =
            "Fragment identityHashCode: $fragmentIdentity\n" +
                "当前 View identityHashCode: $viewIdentity"

        view.findViewById<Button>(R.id.btnDay48FragmentScopeTask).setOnClickListener {
            lifecycleScope.launch {
                log("Fragment lifecycleScope：任务开始")
                delay(5_000)
                log(
                    "Fragment lifecycleScope 5秒任务：完成，" +
                        "fragmentIdentityHashCode=$fragmentIdentity，" +
                        "Fragment当前Lifecycle状态=${lifecycle.currentState}"
                )
            }
        }

        view.findViewById<Button>(R.id.btnDay48ViewScopeTask).setOnClickListener {
            val job = viewLifecycleOwner.lifecycleScope.launch {
                log("View lifecycleScope：任务开始 | View=$viewIdentity")
                delay(5_000)
                log("View lifecycleScope：5秒任务完成 | View=$viewIdentity")
            }
            job.invokeOnCompletion { cause ->
                if (cause == null) {
                    log("View lifecycleScope：正常结束")
                } else {
                    log(
                        "View lifecycleScope 5秒任务：已取消，" +
                            "原因=${cause.javaClass.simpleName}: ${cause.message}"
                    )
                }
            }
        }

        view.findViewById<Button>(R.id.btnDay48OpenFragmentB).setOnClickListener {
            log("A -> B | replace + addToBackStack(A_TO_B)")
            parentFragmentManager.beginTransaction()
                .replace(R.id.day48FragmentContainer, Day48FragmentB())
                .addToBackStack(BACK_STACK_NAME)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        log("onStart | Fragment=$fragmentIdentity")
    }

    override fun onResume() {
        super.onResume()
        log("onResume | Fragment=$fragmentIdentity")
    }

    override fun onPause() {
        log("onPause | Fragment=$fragmentIdentity")
        super.onPause()
    }

    override fun onStop() {
        log("onStop | Fragment=$fragmentIdentity")
        super.onStop()
    }

    override fun onDestroyView() {
        log(
            "onDestroyView | Fragment=$fragmentIdentity | " +
                "Fragment 对象仍可能存在，但当前 View 已销毁"
        )
        super.onDestroyView()
    }

    override fun onDestroy() {
        log("onDestroy | Fragment=$fragmentIdentity")
        super.onDestroy()
    }

    private fun log(message: String) {
        val fullMessage = "Day48FragmentA.$message"
        Log.d(TAG, fullMessage)
        (activity as? Day48Activity)?.appendLifecycleLog(fullMessage)
    }

    private companion object {
        const val TAG = "Day48"
        const val BACK_STACK_NAME = "A_TO_B"
    }
}
