package com.example.sdkstudydemo.ui.eventmonitor

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sdkstudydemo.R
import com.example.sdkstudydemo.databinding.ActivityEventMonitorBinding

import kotlinx.coroutines.launch

class EventMonitorActivity :
    AppCompatActivity() {

    private lateinit var binding: ActivityEventMonitorBinding
    //给当前 Activity 获取一个 EventMonitorViewModel。
    private val viewModel: EventMonitorViewModel by viewModels()

    private var currentState: EventMonitorUiState = EventMonitorUiState.Idle

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
//        拿 XML
//        ↓
//        inflate成真正View树
//        ↓
//        Binding持有这些View
//        ↓
//        把root交给Activity显示
        binding = ActivityEventMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInput()

        setupClick()

        observeState()
    }

    private fun setupInput() {
        binding.etEventName
            .doAfterTextChanged {
                updateUploadButton()
            }
    }

    private fun setupClick() {

        binding.btnUpload.setOnClickListener {

                val eventName =
                    binding.etEventName.text
                        .toString()
                        .trim()

                if (eventName.isBlank()) {

                    binding.etEventName.error =
                        getString(
                            R.string.event_name_required
                        )

                    return@setOnClickListener
                }

                viewModel.upload(
                    eventName
                )
            }
    }

    private fun observeState() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect {
                        state ->

                    render(state)
                }
            }
        }
    }

    private fun render(
        state: EventMonitorUiState
    ) {

        currentState = state

        when (state) {

            EventMonitorUiState.Idle -> {

                binding.progressBar.visibility =
                    View.GONE

                binding.tvStatus.text =
                    getString(
                        R.string.status_idle
                    )
            }

            EventMonitorUiState.Loading -> {

                binding.progressBar.visibility =
                    View.VISIBLE

                binding.tvStatus.text =
                    getString(
                        R.string.status_loading
                    )
            }

            is EventMonitorUiState.Success -> {

                binding.progressBar.visibility =
                    View.GONE

                binding.tvStatus.text =
                    state.message
            }

            is EventMonitorUiState.Error -> {

                binding.progressBar.visibility =
                    View.GONE

                binding.tvStatus.text =
                    state.message
            }
        }

        updateUploadButton()
    }

    private fun updateUploadButton() {

        val hasInput = binding.etEventName.text
                .isNullOrBlank()
                .not()

        val isLoading = currentState==EventMonitorUiState.Loading

        binding.btnUpload.isEnabled =
            hasInput && !isLoading
    }
}