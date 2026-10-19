package com.example.sdkstudydemo.network

import com.google.gson.JsonParseException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException
//sealed interface 限制了直接实现它的类型范围，编译器能够知道这一套类型有哪些可能分支。
sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data:T
    ): NetworkResult<T>

    data class Error(
        val type:ErrorType,
        val message: String
    ) : NetworkResult<Nothing>
}
    enum class ErrorType{
        NETWORK,
        HTTP,
        PARSE,
        BUSINESS,
        UNKNOWM
    }

    suspend fun <T> safeApiCall(
        request: suspend() -> T
    ): NetworkResult<T>{
        return try {
            NetworkResult.Success(request())
        }catch (e: CancellationException){
            throw e
        }catch (e: HttpException){
            NetworkResult.Error(ErrorType.HTTP, "HTTP错误：${e.code()}")
        }catch (e: JsonParseException){
            NetworkResult.Error(ErrorType.PARSE, "数据类型解析失败")
        }
    }
