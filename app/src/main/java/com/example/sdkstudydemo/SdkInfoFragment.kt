package com.example.sdkstudydemo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkLogger

class SdkInfoFragment : Fragment(R.layout.fragment_sdk_info) {
    private lateinit var tvFragmentSdkInfo: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SdkLogger.d("SdkInfoFragment onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SdkLogger.d("SdkInfoFragment onViewCreated")
        tvFragmentSdkInfo = view.findViewById(R.id.tvFragmentSdkInfo)
        refreshSdkInfo()
    }

    override fun onStart() {
        super.onStart()
        SdkLogger.d("SdkInfoFragment onStart")
    }

    override fun onResume() {
        super.onResume()
        SdkLogger.d("SdkInfoFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        SdkLogger.d("SdkInfoFragment onPause")
    }

    override fun onStop() {
        super.onStop()
        SdkLogger.d("SdkInfoFragment onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SdkLogger.d("SdkInfoFragment onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        SdkLogger.d("SdkInfoFragment onDestroy")
    }

    fun refreshSdkInfo() {
        if (!::tvFragmentSdkInfo.isInitialized) {
            return
        }

        tvFragmentSdkInfo.text = """
            SDK 信息 Fragment
            
            SDK 是否初始化：${MySdk.isInitialized()}
            SDK 版本：${MySdk.getVersion()}
            SDK AppId：${MySdk.getAppId()}
            SDK 环境：${MySdk.getEnvironment()}
            SDK 日志开关：${MySdk.isLogEnabled()}
            
            本地保存 AppId：${MySdk.getSaveAppId()}
            本地保存环境：${MySdk.getSaveEnvironment()}
            用户是否同意隐私：${MySdk.hasUserConsent()}
            SDK 初始化时间戳：${MySdk.getInitTime()}
        """.trimIndent()
    }


}