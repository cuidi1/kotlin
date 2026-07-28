package com.example.sdkstudydemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(
    private val repository: SdkRepository
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    //这个函数使用一个泛型T，函数名叫create，接收一个参数modelClass，返回一个T类型的对象
    override  fun <T: ViewModel> create(
        modelClass:Class<T>
    ): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            //创建MainViewModel的时候，把repository传进去
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("未知的ViewModel 类型：${modelClass.name}")
    }
}