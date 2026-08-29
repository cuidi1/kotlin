package com.example.sdkstudydemo.ui.day49

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sdkstudydemo.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class Day49FragmentA : Fragment(R.layout.fragment_day49_a) {

    /*
     * activityViewModels() 大致等价于：
     * ViewModelProvider(requireActivity())[Day49SharedViewModel::class.java]
     *
     * ViewModelProvider 会到 Activity 的 ViewModelStore 中按类型查找：
     * 已存在就复用，不存在才创建。因此 A、B 会拿到同一个实例。
     */
    private val sharedViewModel: Day49SharedViewModel by activityViewModels()

    // 仅用于 identity 对比：viewModels() 使用当前 Fragment 自己的 ViewModelStore。
    private val fragmentLocalViewModel: Day49SharedViewModel by viewModels()

    private val fragmentIdentity: Int
        get() = System.identityHashCode(this)

    private var selectedCity = "未选择"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(
            TAG,
            "FragmentA.onCreate：Fragment=$fragmentIdentity, " +
                "activityViewModels=${System.identityHashCode(sharedViewModel)}, " +
                "viewModels=${System.identityHashCode(fragmentLocalViewModel)}"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewIdentity = System.identityHashCode(view)
        val parentManagerIdentity = System.identityHashCode(parentFragmentManager)
        val activityManagerIdentity =
            System.identityHashCode((requireActivity() as Day49Activity).supportFragmentManager)
        val sharedViewModelIdentity = System.identityHashCode(sharedViewModel)
        val localViewModelIdentity = System.identityHashCode(fragmentLocalViewModel)

        Log.d(
            TAG,
            "FragmentA.onViewCreated：Fragment=$fragmentIdentity, View=$viewIdentity"
        )
        Log.d(
            TAG,
            "FragmentA.parentFragmentManager=$parentManagerIdentity, " +
                "Activity.supportFragmentManager=$activityManagerIdentity, " +
                "是否同一个=${parentFragmentManager === (requireActivity() as Day49Activity).supportFragmentManager}"
        )
        Log.d(
            TAG,
            "FragmentA ViewModel：activityViewModels=$sharedViewModelIdentity, viewModels=$localViewModelIdentity"
        )

        val countView = view.findViewById<TextView>(R.id.tvDay49ACount)
        val cityView = view.findViewById<TextView>(R.id.tvDay49ACity)
        cityView.text = "当前选择的城市：$selectedCity"

        view.findViewById<TextView>(R.id.tvDay49ADebug).text =
            "Fragment identityHashCode：$fragmentIdentity\n" +
                "View identityHashCode：$viewIdentity\n\n" +
                "Activity.supportFragmentManager：$activityManagerIdentity\n" +
                "A.parentFragmentManager：$parentManagerIdentity\n" +
                "两者是同一个：${activityManagerIdentity == parentManagerIdentity}\n\n" +
                "activityViewModels（Activity 作用域）：$sharedViewModelIdentity\n" +
                "viewModels（A 自己的作用域）：$localViewModelIdentity"

        parentFragmentManager.setFragmentResultListener(
            CITY_REQUEST_KEY,
            viewLifecycleOwner
        ) { requestKey, bundle ->
            selectedCity = bundle.getString(CITY_BUNDLE_KEY) ?: "未选择"
            cityView.text = "当前选择的城市：$selectedCity"
            Log.d(
                TAG,
                "FragmentA 收到 Fragment Result：requestKey=$requestKey, city=$selectedCity"
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.count.collect { count ->
                    countView.text = "当前共享 count：$count"
                    Log.d(
                        TAG,
                        "FragmentA collect count=$count, SharedViewModel=$sharedViewModelIdentity"
                    )
                }
            }
        }

        view.findViewById<Button>(R.id.btnDay49AIncrease).setOnClickListener {
            sharedViewModel.increase()
        }

        view.findViewById<Button>(R.id.btnDay49OpenB).setOnClickListener {
            Log.d(
                TAG,
                "A -> B：parentFragmentManager=$parentManagerIdentity，" +
                    "replace + addToBackStack(A_TO_B) + commit"
            )

            /*
             * parentFragmentManager：管理当前 Fragment A 的 FragmentManager。
             * 当前 A 由 Activity 直接管理，所以它就是 Activity.supportFragmentManager。
             * childFragmentManager 则用于管理 A 内部的子 Fragment，本 Demo 不需要嵌套 Fragment。
             *
             * add() 会把新 Fragment 加到容器中；replace() 会替换容器里的当前 Fragment。
             * commit() 负责提交这次 FragmentTransaction。
             */
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.day49FragmentContainer, Day49FragmentB())
                .addToBackStack(BACK_STACK_NAME)
                .commit()
        }
    }

    override fun onDestroyView() {
        Log.d(
            TAG,
            "FragmentA.onDestroyView：Fragment=$fragmentIdentity, 当前 View 被销毁"
        )
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "FragmentA.onDestroy：Fragment=$fragmentIdentity")
        super.onDestroy()
    }

    private companion object {
        const val TAG = "Day49"
        const val BACK_STACK_NAME = "A_TO_B"
        const val CITY_REQUEST_KEY = "city_request"
        const val CITY_BUNDLE_KEY = "city"
    }
}
