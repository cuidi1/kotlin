package com.example.sdkstudydemo.ui.eventmonitor

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sdkstudydemo.R
import com.example.sdkstudydemo.databinding.ActivityEventMonitorBinding
import com.example.sdkstudydemo.local.AppDatabase
import com.example.sdkstudydemo.local.SdkEventEntity
import com.example.sdkstudydemo.network.RetrofitProvider
import com.example.sdkstudydemo.repository.TodoRepository

import kotlinx.coroutines.launch

class EventMonitorActivity :
    AppCompatActivity() {

    private lateinit var binding: ActivityEventMonitorBinding
    //给当前 Activity 获取一个 EventMonitorViewModel。
    private lateinit var viewModel: EventMonitorViewModel
    private var currentState: EventMonitorUiState = EventMonitorUiState.Idle
    private val eventLogAdapter = EventLogAdapter()

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
        observeState()
        observeEventLogs()


        val repository = TodoRepository(RetrofitProvider.todoApi)
        val factory = EventMonitorViewModelFactory(repository)
        this.viewModel = ViewModelProvider(
            this,
            factory
        )[EventMonitorViewModel::class.java]
        viewModel.loadTodo()
        viewModel.testNetworkRequest()
        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.sdkEventDao()
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                dao.observeAll().collect {
                    events->Log.d("Day69", "数据库当前有${events.size}条事件")
                    events.forEach {
                        event->
                            Log.d("Day69", "${event.id}-${event.eventName}")
                    }

                }
            }
        }
        lifecycleScope.launch {

            // 1. 插入一条事件
            val event = SdkEventEntity(
                eventName = "ad_show",
                timestamp = System.currentTimeMillis()
            )

            val newId = dao.insert(event)

            Log.d("Day68", "插入成功，ID：$newId")

            // 2. 查询所有事件
            val events = dao.getAll()

            // 3. 打印查询结果
            events.forEach { item ->
                Log.d(
                    "Day68",
                    "事件：${item.id}，${item.eventName}，${item.timestamp}"
                )
            }
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
    private fun observeEventLogs() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.eventLogs.collect { logs ->

                    eventLogAdapter.submitList(logs)
                }
            }
        }
    }
    private fun render(
        state: EventMonitorUiState
    ) {

        currentState = state

//        when (state) {
//
//            EventMonitorUiState.Idle -> {
//
//                binding.progressBar.visibility =
//                    View.GONE
//
//                binding.tvStatus.text =
//                    getString(
//                        R.string.status_idle
//                    )
//            }
//
//            EventMonitorUiState.Loading -> {
//
//                binding.progressBar.visibility =
//                    View.VISIBLE
//
//                binding.tvStatus.text =
//                    getString(
//                        R.string.status_loading
//                    )
//            }
//
//            is EventMonitorUiState.Success -> {
//
//                binding.progressBar.visibility =
//                    View.GONE
//
//                binding.tvStatus.text =
//                    state.message
//            }

//    private lateinit var binding: ActivityEventMonitorBinding
//    //给当前 Activity 获取一个 EventMonitorViewModel。
//    private val viewModel: EventMonitorViewModel by viewModels()
//
//    private var currentState: EventMonitorUiState = EventMonitorUiState.Idle
//    private val eventLogAdapter = EventLogAdapter()
//
//    override fun onCreate(
//        savedInstanceState: Bundle?
//    ) {
//        super.onCreate(savedInstanceState)
////        拿 XML
////        ↓
////        inflate成真正View树
////        ↓
////        Binding持有这些View
////        ↓
////        把root交给Activity显示
//        binding = ActivityEventMonitorBinding.inflate(layoutInflater)
//        setupRecyclerView()
//        setContentView(binding.root)
//        setupInput()
//        setupClick()
//        observeState()
//        observeEventLogs()
//    }
//
//    private fun setupInput() {
//        binding.etEventName
//            .doAfterTextChanged {
//                updateUploadButton()
//            }
//    }
//    private fun setupRecyclerView() {
//        binding.rvEventLogs.layoutManager = LinearLayoutManager(this)
//        binding.rvEventLogs.adapter = eventLogAdapter
//    }
//    private fun setupClick() {
//
//        binding.btnUpload.setOnClickListener {
//                val eventName = binding.etEventName.text.toString().trim()
//                if (eventName.isBlank()) {
//                    binding.etEventName.error = getString(
//                            R.string.event_name_required
//                        )
//                    return@setOnClickListener
//                }
//                viewModel.upload(eventName)
//            }
//    }
//
//    private fun observeState() {
//
//        lifecycleScope.launch {
//
//            repeatOnLifecycle(
//                Lifecycle.State.STARTED
//            ) {
//
//                viewModel.uiState.collect {
//                        state ->
//
//                    render(state)
//                }
//            }
//        }
//    }
//    private fun observeEventLogs() {
//
//        lifecycleScope.launch {
//
//            repeatOnLifecycle(
//                Lifecycle.State.STARTED
//            ) {
//
//                viewModel.eventLogs.collect { logs ->
//
//                    eventLogAdapter.submitList(logs)
//                }
//            }
//        }
//    }
//    private fun render(
//        state: EventMonitorUiState
//    ) {
//
//        currentState = state
//
//        when (state) {
//
//            EventMonitorUiState.Idle -> {
//
//                binding.progressBar.visibility =
//                    View.GONE
//
//                binding.tvStatus.text =
//                    getString(
//                        R.string.status_idle
//                    )
//            }
//
//            EventMonitorUiState.Loading -> {
//
//                binding.progressBar.visibility =
//                    View.VISIBLE
//
//                binding.tvStatus.text =
//                    getString(
//                        R.string.status_loading
//                    )
//            }
//
//            is EventMonitorUiState.Success -> {
//
//                binding.progressBar.visibility =
//                    View.GONE
//
//                binding.tvStatus.text =
//                    state.message
//            }
//
//            is EventMonitorUiState.Error -> {
//
//                binding.progressBar.visibility =
//                    View.GONE
//
//                binding.tvStatus.text =
//                    state.message
//            }
//        }
//
//        updateUploadButton()
//    }
//
//    private fun updateUploadButton() {
//
//        val hasInput = binding.etEventName.text
//                .isNullOrBlank()
//                .not()
//
//        val isLoading = currentState==EventMonitorUiState.Loading
//
//        binding.btnUpload.isEnabled =
//            hasInput && !isLoading
//    }
}}