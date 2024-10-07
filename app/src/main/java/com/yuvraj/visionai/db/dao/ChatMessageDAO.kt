package com.yuvraj.visionai.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yuvraj.visionai.db.entities.ChatMessageEntity


@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM (SELECT * FROM chat_messages ORDER BY id DESC LIMIT :pageSize OFFSET :offset) AS recent_messages ORDER BY id ASC")
    suspend fun getMessagesWithPagination(pageSize: Int, offset: Int): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
}