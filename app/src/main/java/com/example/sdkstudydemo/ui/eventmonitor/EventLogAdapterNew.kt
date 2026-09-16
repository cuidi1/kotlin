package com.example.sdkstudydemo.ui.eventmonitor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sdkstudydemo.databinding.ItemEventLogBinding

//old
//class EventLogAdapter :
//    RecyclerView.Adapter<EventLogAdapter.EventLogViewHolder>() {
//
//    private val items =
//        mutableListOf<EventLogItem>()
//
//    class EventLogViewHolder(
//        val binding: ItemEventLogBinding
//    ) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): EventLogViewHolder {
//
//        val binding =
//            ItemEventLogBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//
//        return EventLogViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(
//        holder: EventLogViewHolder,
//        position: Int
//    ) {
//
//        val item = items[position]
//
//        holder.binding.tvEventName.text =
//            item.eventName
//
//        holder.binding.tvResult.text =
//            item.result
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    fun submitItems(
//        newItems: List<EventLogItem>
//    ) {
//        items.clear()
//        items.addAll(newItems)
//
//        notifyDataSetChanged()
//    }
//}