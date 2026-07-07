package com.example.sdkstudydemo.sdk

import android.content.Context
import android.content.SharedPreferences

object SdkStorage {
    private const val FILE_NAME = "my_sdk_storage"
    private lateinit var preferences: SharedPreferences
    fun init(context: Context){
        preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String){
        checkInitialized();
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String{
        checkInitialized();
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putBoolean(key: String, value: Boolean) {
        checkInitialized();
        preferences.edit().putBoolean(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        checkInitialized()
        preferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String,defaultValue: Long = 0L): Long{
        checkInitialized()
        return preferences.getLong(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        checkInitialized()
        return preferences.getBoolean(key, defaultValue)
    }


    private fun checkInitialized(){
        if(!::preferences.isInitialized){
            throw IllegalStateException("SdkStorage 未初始化, 请先调用 SdkStorage.init(context)")
        }
    }
}