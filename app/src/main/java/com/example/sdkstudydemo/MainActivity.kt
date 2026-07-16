package com.example.sdkstudydemo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkLogger
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var buttonAgree: Button
    private lateinit var buttonCancel: Button
    private lateinit var btnSetting: Button
    private lateinit var btnTrackEvent: Button
    private lateinit var btnRequestCameraPermission: Button
    private lateinit var btnTrackEventByBundle: Button
    private lateinit var tvStateCount: TextView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var btnIncreaseCount: Button
//    private var clickCount = 0
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

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        granted ->
        if(granted) {
            SdkLogger.d("相机权限申请成功")
        }else{
            SdkLogger.d("相机权限被用户拒绝")
        }
        refreshAll()
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
        btnRequestCameraPermission = findViewById(R.id.btnRequestCameraPermission)
        btnTrackEventByBundle = findViewById(R.id.btnTrackEventByBundle)
        tvStateCount = findViewById(R.id.tvStateCount)
        btnIncreaseCount = findViewById(R.id.btnIncreaseCount)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        refreshSdkInfo()
        btnIncreaseCount.setOnClickListener {
            mainViewModel.clickCount++;
//            clickCount++
//            SdkLogger.d("页面状态计数增加：$clickCount")
            refreshStateCount()
            refreshAll()
        }
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
        btnRequestCameraPermission.setOnClickListener {
            requestCameraPermission()
        }
        btnTrackEventByBundle.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("button", "bundle_track")
            bundle.putString("from", "Day14")
            val result = MySdk.trackEvent(
                eventName = "click_demo_button",
                bundle = bundle)
            SdkLogger.d("上报结果：code=${result.code}, message=${result.message}")
            refreshAll()

        }
//        clickCount = savedInstanceState?.getInt(Companion.KEY_CLICK_COUNT, 0)?:0
        supportFragmentManager.beginTransaction()
                .replace(R.id.infoFragmentContainer,sdkInfoFragment)
                .replace(R.id.fragmentContainer,sdkLogFragment)
                .commit()
    }
    private fun refreshStateCount(){
//        tvStateCount.text = "页面状态计数：$clickCount"
        tvStateCount.text = "页面状态计数：${mainViewModel.clickCount}"
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putInt(Companion.KEY_CLICK_COUNT,clickCount)
//        SdkLogger.d("onSaveInstanceState 保存页面状态：clickCount=$clickCount")
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
    private fun requestCameraPermission(){
        if(!MySdk.hasUserConsent()){
            SdkLogger.d("用户未同意隐私协议，无法请求相机权限")
            refreshAll()
            return
        }
        val hashPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

        if(hashPermission){
            SdkLogger.d("相机权限已申请")
            refreshAll()
            return
        }
        SdkLogger.d("开始申请相机权限")
        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}