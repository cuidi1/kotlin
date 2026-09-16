package com.example.sdkstudydemo.ui.eventmonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sdkstudydemo.databinding.FragmentEventMonitorBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sdkstudydemo.R
import kotlinx.coroutines.launch

class EventMonitorFragment : Fragment() {

    private var _binding:
            FragmentEventMonitorBinding? = null

    private val binding get() = _binding!!
    private var currentState: EventMonitorUiState = EventMonitorUiState.Idle

    private val viewModel: EventMonitorViewModel by viewModels()

    private val eventLogAdapter =
        EventLogAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentEventMonitorBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupRecyclerView()
        setupInput()
        setupClick()

        observeState()
        observeEventLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun observeEventLogs() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.eventLogs.collect { logs ->

                    eventLogAdapter.submitList(logs)
                }
            }
        }
    }

    private fun observeState() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect {
                        state ->

                    render(state)
                }
            }
        }
    }
    private fun setupInput() {
        binding.etEventName
            .doAfterTextChanged {
                updateUploadButton()
            }
    }
    private fun setupRecyclerView() {
        binding.rvEventLogs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEventLogs.adapter = eventLogAdapter
    }
    private fun setupClick() {

        binding.btnUpload.setOnClickListener {
            val eventName = binding.etEventName.text.toString().trim()
            if (eventName.isBlank()) {
                binding.etEventName.error = getString(
                    R.string.event_name_required
                )
                return@setOnClickListener
            }
            viewModel.upload(eventName)
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