package com.example.sdkstudydemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkLogger

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var buttonAgree: Button
    private lateinit var buttonCancel: Button
    private lateinit var btnSetting: Button
    private lateinit var btnTrackEvent: Button
    private val sdkInfoFragment = SdkInfoFragment()
    private val sdkLogFragment = SdkLogFragment()
    private val settingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val consent = result.data?.getBooleanExtra(
                "user_consent",
                MySdk.hasUserConsent()
            ) ?: MySdk.hasUserConsent()
            MySdk.setUserConsent(consent)
            refreshSdkInfo()
            SdkLogger.d("从设置页返回userConsent = $consent")
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SdkLogger.d("cdMainActivity onCreate")

        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.tvSdkInfo)
        buttonAgree = findViewById(R.id.bthAgree)
        buttonCancel = findViewById(R.id.bthCancel)
        btnSetting = findViewById(R.id.bthSetting)
        btnTrackEvent = findViewById(R.id.btnTrackEvent)
        refreshSdkInfo()
        buttonAgree.setOnClickListener {
            MySdk.setUserConsent(true)
            SdkLogger.d("点击同意隐私协议")
            refreshAll()
        }
        buttonCancel.setOnClickListener {
            MySdk.setUserConsent(false)
            SdkLogger.d("点击取消隐私授权")
            refreshAll()
        }
        btnSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            intent.putExtra("fromPage", "MainActivity")
            intent.putExtra("current_user_consent", MySdk.hasUserConsent())
            settingLauncher.launch(intent)

        }
        btnTrackEvent.setOnClickListener {
            val result = MySdk.trackEvent(
                eventName = "click_demo_button",
                params = mapOf("page" to "MainActivity"))
            SdkLogger.d("上报结果：code=${result.code}, message=${result.message}")
            refreshAll()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.infoFragmentContainer,sdkInfoFragment)
                .replace(R.id.fragmentContainer,sdkLogFragment)
                .commit()
    }

    override fun onStart(){
        super.onStart()
        SdkLogger.d("cdMainCdActivity onStart")
    }

    override fun onResume(){
        super.onResume()
        SdkLogger.d("cdMainCdActivity onResume")
    }

    override fun onPause(){
        super.onPause()
        SdkLogger.d("cdMainCdActivity onPause")
    }
    override fun onStop(){
        super.onStop()
        SdkLogger.d("cdMainCdActivity onStop")
    }
    override fun onRestart(){
        super.onRestart()
        SdkLogger.d("cdMainCdActivity onRestart")
    }
    override fun onDestroy(){
        super.onDestroy()
        SdkLogger.d("cdMainCdActivity onDestroy")
    }
    private fun getSdkInfoText(): String {
        return """
            SDK 学习 Demo
            
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
    private fun refreshSdkInfo() {
        textView.text = getSdkInfoText()
    }

    private fun refreshAll(){
        sdkInfoFragment.refreshSdkInfo()
        sdkLogFragment.refreshLogs()
    }
}