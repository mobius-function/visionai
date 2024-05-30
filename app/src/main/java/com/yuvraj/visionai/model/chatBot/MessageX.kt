package com.yuvraj.visionai.model.chatBot

import com.google.gson.annotations.SerializedName

data class MessageX(
    @SerializedName("content")val content: String,
    @SerializedName("role")val role: String
)

/*
test in postman before going into API
{
    "model": "gpt-3.5-turbo",
    "messages":[
    {"role": "system", "content": "You are my friend, the name of mine is pavan"},
    {"role": "user", "content": "hi AI, I'm fine how are you?"}
    ]

}
pass the headers by refering to retrofit builder*/