package com.example.sdkstudydemo.ui.eventmonitor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sdkstudydemo.databinding.ItemEventLogBinding

class EventLogAdapter :
    ListAdapter<
            EventLogItem,
            EventLogAdapter.EventLogViewHolder
            >(DiffCallback) {

    class EventLogViewHolder(
        val binding: ItemEventLogBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventLogViewHolder {

        val binding =
            ItemEventLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return EventLogViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: EventLogViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        holder.binding.tvEventName.text =
            item.eventName

        holder.binding.tvResult.text =
            item.result
    }

    companion object {

        private val DiffCallback =
            object :
                DiffUtil.ItemCallback<EventLogItem>() {

                override fun areItemsTheSame(
                    oldItem: EventLogItem,
                    newItem: EventLogItem
                ): Boolean {

                    return oldItem.eventName ==
                            newItem.eventName
                }

                override fun areContentsTheSame(
                    oldItem: EventLogItem,
                    newItem: EventLogItem
                ): Boolean {

                    return oldItem == newItem
                }
            }
    }
}
