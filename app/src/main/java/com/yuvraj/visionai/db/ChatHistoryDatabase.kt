package com.yuvraj.visionai.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yuvraj.visionai.db.dao.ChatMessageDao
import com.yuvraj.visionai.db.entities.ChatMessageEntity

@Database(entities = [ChatMessageEntity::class], version = 1)
abstract class ChatHistoryDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
}