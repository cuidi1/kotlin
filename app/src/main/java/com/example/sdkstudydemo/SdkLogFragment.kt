package com.example.sdkstudydemo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdkstudydemo.sdk.SdkLogger
import kotlinx.coroutines.launch
class SdkLogFragment : Fragment(R.layout.fragment_sdk_log) {
    private lateinit var rvSdkLog: RecyclerView

    private val logAdapter = SdkLogAdapter()
    private val logs = listOf(
        "SDK 初始化成功",
        "用户同意隐私协议",
        "进入设置页",
        "从设置页返回",
        "用户取消隐私授权",
        "读取本地 userConsent",
        "刷新 SDK 信息 Fragment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SdkLogger.d("SdkLogFragment onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SdkLogger.d("SdkLogFragment onViewCreated")
        rvSdkLog = view.findViewById(R.id.rvSdkLog)
        //告诉列表怎么排列
        rvSdkLog.layoutManager = LinearLayoutManager(requireContext())
        //把日志数据交给 Adapter，让 Adapter 显示到 RecyclerView 上。
        rvSdkLog.adapter = logAdapter
        observeLogs()
    }

    override fun onResume() {
        super.onResume()
        SdkLogger.d("SdkLogFragment onResume")
        refreshLogs()
    }
    private fun observeLogs(){
        viewLifecycleOwner.lifecycleScope.launch{
//            当 Fragment 页面至少处于 STARTED 状态时，才 collect 日志。
//            页面停止时，暂停 collect。
//            页面重新显示时，再继续 collect。
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

//                我开始监听 logFlow。
//                只要 logFlow 发出新的日志列表，
//                我就拿到 logs，
//                然后更新 RecyclerView。
                SdkLogger.logFlow.collect {
                    logs -> logAdapter.updateLogs(logs)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SdkLogger.d("SdkLogFragment onDestroyView")
    }

    fun refreshLogs() {
        if (!::rvSdkLog.isInitialized) {
            return
        }

        val logs = SdkLogger.getLogs()
        logAdapter.updateLogs(logs)
    }
}