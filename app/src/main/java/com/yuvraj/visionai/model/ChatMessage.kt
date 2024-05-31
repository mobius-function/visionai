package com.yuvraj.visionai.model

import com.yuvraj.visionai.enums.ChatMessageSender

data class ChatMessage(
    val message: String,
    val sender: ChatMessageSender,
)
