package com.yuvraj.visionai.repositories

import com.yuvraj.visionai.utils.Constants.API_KEY
import com.yuvraj.visionai.utils.Constants.CHAT_BOT_MODEL
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ChatResponse {

    private val client = OkHttpClient()
    private val JSON : MediaType = "application/json; charset=utf-8".toMediaType()

    fun getChatResponse(question: String) : String {
        var responseGenerated = ""
        val jsonBody = JSONObject()

        try {
            jsonBody.put("model", CHAT_BOT_MODEL)
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        } catch (e:JSONException){
            e.printStackTrace()
        }

        val body : RequestBody = jsonBody.toString().toRequestBody(JSON)

        val request:Request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(
            object :Callback {

                override fun onFailure(call: Call, e: IOException) {
                    responseGenerated = "Failed to load response due to ${e.message}"
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful){
                        var jsonObject :JSONObject? = null
                        try {
                            jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result = jsonArray.getJSONObject(0).getString("text")
                            responseGenerated = result.trim{it <= ' '}
                        }catch (e: JSONException){
                            e.printStackTrace()
                        }
                    }else{
                        responseGenerated = "Failed to load response due to ${response.body.toString()}"
                    }
                }
            }
        )

        return responseGenerated
    }
}