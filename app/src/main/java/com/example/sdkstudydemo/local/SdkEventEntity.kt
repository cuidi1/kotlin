package com.example.sdkstudydemo.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//告诉 Room：
//这是一个数据库实体，对应数据库里的 sdk_events 表。
@Entity(tableName = "Sdk_events")
data class SdkEventEntity (
    //主键用于唯一标识一条数据。
    //autoGenerate = true 让数据库自动生成 ID。插入新对象时传入默认的 0，Room 会请求数据库生成主键。
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    //负责数据库列映射
    @ColumnInfo("event_name")
    val eventName : String,
    @ColumnInfo("timestamp")
    val timestamp: Long
)