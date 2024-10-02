package com.yuvraj.visionai.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val sentBy: String // Could use String or Int depending on how you store message sender type
)
