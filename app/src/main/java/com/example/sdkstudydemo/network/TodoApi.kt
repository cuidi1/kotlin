package com.example.sdkstudydemo.network
import retrofit2.http.GET

interface TodoApi {
    @GET("todos/1")
    suspend fun getTodo(): Todo
    //suspend表示这个函数可以在协程中挂起
}