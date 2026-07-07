package com.example.sdkstudydemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sdkstudydemo.sdk.MySdk
import com.example.sdkstudydemo.sdk.SdkLogger

class SettingActivity : AppCompatActivity() {
    private lateinit var tvSettingInfo: TextView
    private lateinit var bthAgreeAndBack: Button
    private lateinit var bthCancelAndBack: Button
    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        SdkLogger.d("SettingActivity onCreate")

        setContentView(R.layout.activity_setting)
        tvSettingInfo = findViewById(R.id.tvSettingInfo)
        bthAgreeAndBack = findViewById(R.id.bthAgreeAndBack)
        bthCancelAndBack = findViewById(R.id.bthCancelAndBack)

        val textView = TextView(this)
        val fromPage = intent.getStringExtra("fromPage")?:"未知页面"
        val currentConsent = intent.getBooleanExtra("current_user_consent", false)
        textView.text= """
            设置页
            来自页面:$fromPage
            SDK AppId: ${MySdk.getAppId()}
            Sdk 环境:${MySdk.getEnvironment()}
            用户是否同意隐私:${MySdk.hasUserConsent()}
        """.trimIndent()
        bthAgreeAndBack.setOnClickListener {
            returnConsentResutl(true)
        }
        bthCancelAndBack.setOnClickListener {
            returnConsentResutl(false)
        }
    }
    private fun returnConsentResutl(consent: Boolean) {
        val resultIntent = Intent();
        resultIntent.putExtra("user_consent", consent)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
    override fun onStart() {
        super.onStart()
        SdkLogger.d("SettingCdActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        SdkLogger.d("SettingCdActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        SdkLogger.d("SettingCdActivity onPause")
    }

    override fun onStop() {
        super.onStop()
        SdkLogger.d("SettingCdActivity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        SdkLogger.d("SettingCdActivity onDestroy")
    }
}