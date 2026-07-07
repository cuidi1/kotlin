package com.example.sdkstudydemo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sdkstudydemo.sdk.SdkLogger

class SdkLogFragment : Fragment(R.layout.fragment_sdk_log) {
    private lateinit var rvSdkLog: RecyclerView
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
        rvSdkLog.adapter = SdkLogAdapter(logs)
    }
}