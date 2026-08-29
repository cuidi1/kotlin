package com.example.sdkstudydemo.ui.day49

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Day49SharedViewModel : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count = _count.asStateFlow()

    init {
        Log.d(TAG, "Day49SharedViewModel 创建：identityHashCode=${System.identityHashCode(this)}")
    }

    fun increase() {
        _count.value++
        Log.d(
            TAG,
            "Day49SharedViewModel.increase：identityHashCode=${System.identityHashCode(this)}, count=${_count.value}"
        )
    }

    override fun onCleared() {
        Log.d(TAG, "Day49SharedViewModel.onCleared：identityHashCode=${System.identityHashCode(this)}")
        super.onCleared()
    }

    private companion object {
        const val TAG = "Day49"
    }
}
