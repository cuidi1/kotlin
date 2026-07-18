package com.example.sdkstudydemo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkLogger
import com.example.sdkstudydemo.sdk.SdkUploadCallback
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
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
    private lateinit var btnUploadEventNetwork:Button
    private lateinit var btnCoroutineTest:Button
    private lateinit var btnUploadEventCoroutine: Button
    private lateinit var btnStartLongCoroutine: Button
    private lateinit var btnCancelLongCoroutine: Button
    private lateinit var btnTimeoutTest: Button
    private var longCoroutineJob: Job? = null
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
    @SuppressLint("MissingInflatedId")
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
        btnUploadEventNetwork = findViewById(R.id.btnUploadEventNetwork)
        btnCoroutineTest = findViewById(R.id.btnCoroutineTest)
        btnUploadEventCoroutine = findViewById(R.id.btnUploadEventCoroutine)
        btnStartLongCoroutine = findViewById(R.id.btnStartLongCoroutine)
        btnCancelLongCoroutine = findViewById(R.id.btnCancelLongCoroutine)
        btnTimeoutTest = findViewById(R.id.btnTimeoutTest)
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
        //???
        btnUploadEventNetwork.setOnClickListener {
            val result = MySdk.trackEventAndUpload(
                eventName = "click_network_upload_button",
                params = mapOf(
                    "page" to "MainActivity",
                    "from" to "Day17",
                    "type" to "network"
                ),
                callback = object : SdkUploadCallback{
                    override fun onSuccess(response: String) {
                        //网络回调要刷新ui，就切回主线程
                        runOnUiThread {
                            SdkLogger.d("网络上报成功：$response")
                            refreshAll()
                        }
                    }

                    override fun onFailure(errorMessage: String) {
                        runOnUiThread {
                            SdkLogger.e("网络上报失败：$errorMessage")
                            refreshAll()
                        }
                    }

                },
            )
            SdkLogger.d("网络上报调用结果：code${result.code},message=${result.message}")
            refreshAll()
        }

        //协程测试点击事件
        btnCoroutineTest.setOnClickListener {
            SdkLogger.d("协程任务开始")
            refreshAll()

            lifecycleScope.launch{
                //这个delay不会卡住主线程
                delay(2000)
                SdkLogger.d("协程任务结束,延迟2秒后执行")
                refreshAll()
            }
        }


        //协程网络上报
        btnUploadEventCoroutine.setOnClickListener {
            SdkLogger.d("开始协程网络上报")
            refreshAll()

            lifecycleScope.launch {
                try {
                    val result = MySdk.trackEventAndUploadSuspend(
                        eventName = "click_coroutine_upload_button",
                        params = mapOf(
                            "page" to "MainActivity",
                            "from" to "Day19",
                            "type" to "coroutine"
                        )
                    )

                    if (result.success) {
                        SdkLogger.d("协程网络上报成功：${result.response}")
                    } else {
                        SdkLogger.e("协程网络上报失败：${result.message}")
                    }
                } catch (e: CancellationException) {
                    SdkLogger.d("协程网络上报被取消")
                    throw e
                } catch (e: Exception) {
                    SdkLogger.e("协程网络上报异常：${e.message}")
                } finally {
                    refreshAll()
                }
            }
        }
        //开始 取消协程
        btnStartLongCoroutine.setOnClickListener {
            startLongCoroutineTask()
        }

        btnCancelLongCoroutine.setOnClickListener {
            cancelLongCoroutineTask()
        }

        btnTimeoutTest.setOnClickListener {
            lifecycleScope.launch {
                SdkLogger.d("开始协程超时测试")
                refreshAll()

                val result = withTimeoutOrNull(3000) {
                    delay(5000)
                    "任务完成"
                }

                if (result == null) {
                    SdkLogger.d("协程超时：3 秒内没有完成")
                } else {
                    SdkLogger.d("协程结果：$result")
                }

                refreshAll()
            }
        }
//        clickCount = savedInstanceState?.getInt(Companion.KEY_CLICK_COUNT, 0)?:0
        supportFragmentManager.beginTransaction()
                .replace(R.id.infoFragmentContainer,sdkInfoFragment)
                .replace(R.id.fragmentContainer,sdkLogFragment)
                .commit()
    }

    //启动长协程任务
    private fun startLongCoroutineTask() {
        if(longCoroutineJob?.isActive == true){
            SdkLogger.d("已有长携程任务正在执行，不重复启动")
            refreshAll()
            return
        }
        longCoroutineJob = lifecycleScope.launch {
            try {
                SdkLogger.d("协程任务开始")
                refreshAll()
                for(i in 1..5){
                    delay(1000)
                    SdkLogger.d("长协程任务执行中：第$i 秒")
                    refreshAll()
                }
                SdkLogger.d("长携程任务完成")
                refreshAll()

            }catch (e: CancellationException){
                SdkLogger.d("协程任务被取消")
                refreshAll()
                throw e
            }catch (e: Exception){
                SdkLogger.e("协程任务出现异常", e)
                refreshAll()
            }
        }
    }

    //取消长协程任务
    private fun cancelLongCoroutineTask() {
        if(longCoroutineJob?.isActive == true){
            longCoroutineJob?.cancel()
            SdkLogger.d("已发出取消长携程任务请求")
        }else{
            SdkLogger.d("没有正在执行的长携程任务")
        }
        refreshAll()
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