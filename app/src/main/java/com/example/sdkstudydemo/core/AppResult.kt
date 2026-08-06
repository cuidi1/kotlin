package com.example.sdkstudydemo.core

sealed class AppResult<out T> {
    data class Success<T>(
        val data:T
    ): AppResult<T>()
    data class Error(
        val code:Int,
        val message: String
    ): AppResult<Nothing>()
    data class Exception(
        val throwable: Throwable
    ): AppResult<Nothing>()
}