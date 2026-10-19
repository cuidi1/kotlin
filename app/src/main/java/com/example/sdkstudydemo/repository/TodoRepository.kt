package com.example.sdkstudydemo.repository

import com.example.sdkstudydemo.network.NetworkResult
import com.example.sdkstudydemo.network.Todo
import com.example.sdkstudydemo.network.TodoApi
import com.example.sdkstudydemo.network.safeApiCall

class TodoRepository(
    private val api: TodoApi
) {
    suspend fun getTodo(): NetworkResult<Todo>{
        return safeApiCall {
            api.getTodo()
        }
    }
}