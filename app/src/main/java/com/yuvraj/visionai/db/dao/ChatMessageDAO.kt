package com.yuvraj.visionai.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yuvraj.visionai.db.entities.ChatMessageEntity


@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages")
    suspend fun getAllMessages(): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()
}