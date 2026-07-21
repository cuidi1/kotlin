package com.example.sdkstudydemo
 sealed class RequestState {
    //用data object是因为不需要任何参数，一个实例就够了
    data object Idle : RequestState()
    data object Loading : RequestState()
    data class Success (
        val message:String
    ): RequestState()

    data class Error(
        val errorMessage: String
    ): RequestState()

    data object Empty: RequestState()
}