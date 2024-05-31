package com.yuvraj.visionai.api

import com.yuvraj.visionai.model.chatBot.Input
import com.yuvraj.visionai.model.chatBot.Response
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIInstance {
//    @POST(CHAT_END_POINT)
    fun getCompletion(
        @Body chatBotInput: Input
    ): Single<Response>
}