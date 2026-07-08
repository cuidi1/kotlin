package com.example.sdkstudydemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SdkLogAdapter: RecyclerView.Adapter<SdkLogAdapter.LogViewHolder>() {
    private val logList = mutableListOf<String>()
    //每行view的持有者
    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLogItem: TextView = itemView.findViewById(R.id.tvLogItem)
    }
    //创建每一行view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_sdk_log,parent,false)
        return LogViewHolder(view)
    }
    //把position条数据绑定到对应的item上,就是给这几行填数据
    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log=logList[position]
        holder.tvLogItem.text=log
    }
    //这个列表一共有多少行
    override fun getItemCount(): Int {
        return logList.size
    }

    fun updateLogs(newLogs:List<String>) {
        logList.clear()
        logList.addAll(newLogs)
        notigyDataSetChanged()
    }
}