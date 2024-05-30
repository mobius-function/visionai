package com.yuvraj.visionai.model.chatBot

import com.google.gson.annotations.SerializedName

data class Input(
    @SerializedName("messages") val messages: List<MessageX>,
    @SerializedName("model")val model: String
)