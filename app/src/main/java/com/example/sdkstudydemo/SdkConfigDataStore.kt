package com.example.sdkstudydemo
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

//给Context扩展一个DataStore对象，名字叫sdk_config
private val Context.sdkConfigDataStore by preferencesDataStore(
    name = "sdk_config"
)

class SdkConfigDataStore (
    private val context: Context
){
    //定义key
    private object Keys {
        val ENABLE_UPLOAD = booleanPreferencesKey("enable_upload")
        val SAMPLE_RATE = intPreferencesKey("sample_rate")
        val CONFIG_VERSION = stringPreferencesKey("config_version")
    }
//DataStore 是异步存储，天然和协程搭配
    suspend fun saveRemoteConfig(config: SdkRemoteConfig) {
        context.sdkConfigDataStore.edit{ preferences ->
            preferences[Keys.ENABLE_UPLOAD] = config.enableUpload
            preferences[Keys.SAMPLE_RATE] = config.sampleRate
            preferences[Keys.CONFIG_VERSION] = config.configVersion
        }
    }
//本地没有缓存会返回null
    suspend fun getRemoteConfig(): SdkRemoteConfig? {
        val preferences = context.sdkConfigDataStore.data.first()

        val enableUpload = preferences[Keys.ENABLE_UPLOAD]
        val sampleRate = preferences[Keys.SAMPLE_RATE]
        val configVersion = preferences[Keys.CONFIG_VERSION]

        if(enableUpload == null || sampleRate == null || configVersion == null){
            return null
        }
        return SdkRemoteConfig(
            enableUpload = enableUpload,
            sampleRate = sampleRate,
            configVersion = configVersion
        )
    }
}