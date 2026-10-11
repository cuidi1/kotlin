package com.example.sdkstudydemo.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(
                "https://jsonplaceholder.typicode.com/"
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val todoApi: TodoApi =
        retrofit.create(TodoApi::class.java)
}