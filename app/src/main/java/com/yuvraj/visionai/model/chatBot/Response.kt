package com.yuvraj.visionai.model.chatBot

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("created") val created: Int,
    @SerializedName("id") val id: String,
    @SerializedName("model") val model: String,
    @SerializedName("object")  val `object`: String,
    @SerializedName("system_fingerprint") val system_fingerprint: String,
    @SerializedName("usage") val usage: Usage
)