package com.example.sdkstudydemo.ui.day49

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sdkstudydemo.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class Day49FragmentB : Fragment(R.layout.fragment_day49_b) {

    /*
     * 大致等价于：
     * ViewModelProvider(requireActivity())[Day49SharedViewModel::class.java]
     * Provider 查询的是 Activity 的 ViewModelStore，所以会复用 A 已经拿到的实例。
     */
    private val sharedViewModel: Day49SharedViewModel by activityViewModels()

    // 仅用于和 activityViewModels() 比较；这个实例只属于 Fragment B。
    private val fragmentLocalViewModel: Day49SharedViewModel by viewModels()

    private val fragmentIdentity: Int
        get() = System.identityHashCode(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(
            TAG,
            "FragmentB.onCreate：Fragment=$fragmentIdentity, " +
                "activityViewModels=${System.identityHashCode(sharedViewModel)}, " +
                "viewModels=${System.identityHashCode(fragmentLocalViewModel)}"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewIdentity = System.identityHashCode(view)
        val parentManagerIdentity = System.identityHashCode(parentFragmentManager)
        val sharedViewModelIdentity = System.identityHashCode(sharedViewModel)
        val localViewModelIdentity = System.identityHashCode(fragmentLocalViewModel)

        Log.d(
            TAG,
            "FragmentB.onViewCreated：Fragment=$fragmentIdentity, View=$viewIdentity, " +
                "SharedViewModel=$sharedViewModelIdentity"
        )

        val countView = view.findViewById<TextView>(R.id.tvDay49BCount)
        view.findViewById<TextView>(R.id.tvDay49BDebug).text =
            "Fragment identityHashCode：$fragmentIdentity\n" +
                "View identityHashCode：$viewIdentity\n" +
                "B.parentFragmentManager：$parentManagerIdentity\n\n" +
                "activityViewModels（应与 A 相同）：$sharedViewModelIdentity\n" +
                "viewModels（应与 A 不同）：$localViewModelIdentity"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.count.collect { count ->
                    countView.text = "当前共享 count：$count"
                    Log.d(
                        TAG,
                        "FragmentB collect count=$count, SharedViewModel=$sharedViewModelIdentity"
                    )
                }
            }
        }

        view.findViewById<Button>(R.id.btnDay49BIncrease).setOnClickListener {
            sharedViewModel.increase()
        }

        view.findViewById<Button>(R.id.btnDay49SelectCity).setOnClickListener {
            // setFragmentResult() 只负责发送数据，不负责页面返回。
            parentFragmentManager.setFragmentResult(
                CITY_REQUEST_KEY,
                bundleOf(CITY_BUNDLE_KEY to "成都")
            )
            Log.d(TAG, "FragmentB.setFragmentResult：发送 city=成都；这一步不负责返回")

            // popBackStack() 只负责撤销 A -> B 的 Transaction，不负责发送数据。
            parentFragmentManager.popBackStack()
            Log.d(TAG, "FragmentB.popBackStack：返回 A；这一步不负责发送 Result")
        }

        view.findViewById<Button>(R.id.btnDay49OnlyPop).setOnClickListener {
            // 没有调用 setFragmentResult，所以只返回页面，不会产生新的城市结果。
            parentFragmentManager.popBackStack()
            Log.d(TAG, "FragmentB 仅调用 popBackStack：返回 A，但没有发送新 Result")
        }
    }

    override fun onDestroyView() {
        Log.d(
            TAG,
            "FragmentB.onDestroyView：Fragment=$fragmentIdentity, 当前 View 被销毁"
        )
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "FragmentB.onDestroy：Fragment=$fragmentIdentity")
        super.onDestroy()
    }

    private companion object {
        const val TAG = "Day49"
        const val CITY_REQUEST_KEY = "city_request"
        const val CITY_BUNDLE_KEY = "city"
    }
}
