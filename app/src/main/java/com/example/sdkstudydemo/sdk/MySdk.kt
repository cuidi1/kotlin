package com.example.sdkstudydemo.sdk

import android.content.Context
import android.util.Log

object MySdk {

    private const val TAG = "MySdk"

    private lateinit var appContext: Context

    private lateinit var sdkConfig: SdkConfig
    private var initialized = false

    fun init(context: Context, config: SdkConfig) {
        if (initialized) {
            Log.d(TAG, "SDK 已经初始化过了，不需要重复初始化")
            return
        }

        if(config.appId.isBlank()) {
            throw IllegalArgumentException("appId 不能为空")
        }
        // 注意：SDK 内部保存 applicationContext，不要直接保存 Activity
        appContext = context.applicationContext
        sdkConfig = config

        initialized = true
        SdkLogger.setEnableLog(config.enableLog)
        SdkStorage.init(appContext)
        SdkLogger.d("SDK 初始化成功")
        SdkLogger.d("packageName = ${appContext.packageName}")
        SdkLogger.d("appId = ${sdkConfig.appId}")
        SdkLogger.d("environment = ${sdkConfig.environment}")
        SdkLogger.d("enableLog = ${sdkConfig.enableLog}")
        SdkLogger.d("userConsent = ${sdkConfig.userConsent}")
        saveInitInfo(sdkConfig);
    }

    private fun saveInitInfo(config: SdkConfig) {
        SdkStorage.putString(SdkKeys.KEY_APP_ID, config.appId)
        SdkStorage.putString(SdkKeys.KEY_ENVIRONMENT, config.environment.name)
        SdkStorage.putBoolean(SdkKeys.KEY_ENABLE_LOG, config.enableLog)
        SdkStorage.putBoolean(SdkKeys.KEY_USER_CONSENT, config.userConsent)
        SdkStorage.putLong(SdkKeys.KEY_INIT_TIME, System.currentTimeMillis())
    }

    fun setUserConsent(consent: Boolean){
        SdkStorage.putBoolean(SdkKeys.KEY_USER_CONSENT, consent)
        SdkLogger.d("用户同意隐私政策: $consent")
    }

    fun hasUserConsent(): Boolean {
        return SdkStorage.getBoolean(SdkKeys.KEY_USER_CONSENT, false)
    }

    fun getSaveAppId(): String{
        return SdkStorage.getString(SdkKeys.KEY_APP_ID, "未保存")
    }

    fun getSaveEnvironment(): String{
        return SdkStorage.getString(SdkKeys.KEY_ENVIRONMENT, "未保存")
    }

    fun getInitTime(): Long{
        return SdkStorage.getLong(SdkKeys.KEY_INIT_TIME, 0L)
    }
    fun isInitialized(): Boolean {
        return initialized
    }

    fun getVersion(): String {
        return "1.0.0"
    }
    fun getAppId(): String {
        return if (::sdkConfig.isInitialized) {
            sdkConfig.appId
        } else {
            "未初始化"
        }
    }

    fun getEnvironment(): String {
        return if (::sdkConfig.isInitialized) {
            sdkConfig.environment.name
        } else {
            "未初始化"
        }
    }

    fun isLogEnabled(): Boolean {
        return if (::sdkConfig.isInitialized) {
            sdkConfig.enableLog
        } else {
            false
        }
    }

//    params 是一个 Map，key 是 String，value 也是 String。
//    如果调用方不传 params，就默认是空 Map。

    fun trackEvent(eventName: String, params: Map<String, Any> = emptyMap()): SdkResult{
        if(!initialized) {
            SdkLogger.e("事件上报失败：SDK尚未初始化")
            return createResult(SdkErrorCode.SDK_NOT_INITIALIZED)
        }
        //isBlank()表示字符串为空或者只包含空格
        if(eventName.isBlank()) {
            SdkLogger.e("事件上报失败：事件名称不能为空")
            return createResult(SdkErrorCode.INVALID_EVENT_NAME)
        }

        if(!hasUserConsent()){
            SdkLogger.e("事件上报失败：用户未同意隐私协议")
            return createResult(SdkErrorCode.USER_CONSENT_REQUIRED)
        }
        SdkLogger.d("事件上报成功：$eventName");
        return createResult(SdkErrorCode.SUCCESS)
    }

    private fun createResult(errorCode: SdkErrorCode): SdkResult {
        return SdkResult(
            success = errorCode == SdkErrorCode.SUCCESS,
            code = errorCode.code,
            message = errorCode.message
        )
    }

    private fun log(message: String) {
        if (::sdkConfig.isInitialized && !sdkConfig.enableLog) {
            return
        }

        Log.d(TAG, message)
    }
    override fun toString(): String {
        return "MySdk(TAG='$TAG', appContext=$appContext, sdkConfig=$sdkConfig, initialized=$initialized)"
    }


}