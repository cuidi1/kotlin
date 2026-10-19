package com.example.sdkstudydemo.ui.eventmonitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sdkstudydemo.repository.TodoRepository

class EventMonitorViewModelFactory(
    private val repository: TodoRepository
): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(
        modelClass: Class<T>
    ):T{
        require(
            modelClass.isAssignableFrom(
                EventMonitorViewModel::class.java
            )
        )
        @Suppress("UNCHECKED_CAST")
        return EventMonitorViewModel(repository) as T
    }
}