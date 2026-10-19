package com.example.sdkstudydemo.local

import androidx.collection.LongSet
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//Retrofit
//→ 运行时动态代理实现接口
//Room
//→ 编译阶段生成 DAO 实现代码
@Dao
interface SdkEventDao {
    // 插入
    @Insert
    suspend fun insert(event: SdkEventEntity): Long

    // 查询一次
    @Query("SELECT * FROM sdk_events ORDER BY id DESC")
    suspend fun getAll(): List<SdkEventEntity>

    // 持续观察数据库
    @Query("SELECT * FROM sdk_events ORDER BY id DESC")
    fun observeAll(): Flow<List<SdkEventEntity>>

    // 根据对象的主键更新
    @Update
    suspend fun update(event: SdkEventEntity): Int

    // 根据对象的主键删除
    @Delete
    suspend fun delete(event: SdkEventEntity): Int

    // 条件查询
    @Query("SELECT * FROM sdk_events WHERE event_name = :name")
    suspend fun getByName(name: String): List<SdkEventEntity>

    // 查询最近 N 条
    @Query("SELECT * FROM sdk_events ORDER BY id DESC LIMIT :count")
    suspend fun getLatest(count: Int): List<SdkEventEntity>

    // 根据 ID 删除
    @Query("DELETE FROM sdk_events WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}