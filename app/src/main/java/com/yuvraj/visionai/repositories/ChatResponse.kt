package com.yuvraj.visionai.repositories

import android.util.Log
import com.yuvraj.visionai.utils.Constants.CHAT_BOT_API_KEY
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ChatResponse {

    private val client = OkHttpClient()
    private val JSON : MediaType = "application/json; charset=utf-8".toMediaType()

    fun getResFun(question:String, callback : (String) -> Unit) {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = JSONObject()
            .put("model", "gpt-3.5-turbo")
            .put("messages", JSONArray()
                .put(JSONObject()
                    .put("role", "user")
                    .put("content", question)
                )
            )
            .put("temperature", 0.7)
            .toString()
            .toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(body)
            .header("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $CHAT_BOT_API_KEY")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback("responseFailure: Failed to load response due to ${response.body.toString()}")
                    Log.d("ChatResponse", "responseFailure: Failed to load response due to $response")
                    return
                }

                val jsonResponse = JSONObject(response.body?.string() ?: "")
                val choices = jsonResponse.getJSONArray("choices")
                if (choices.length() > 0) {
                    val result = choices.getJSONObject(0).getJSONObject("message").getString("content").trim()
                    callback(result)
                } else {
                    callback("No response from API")
                }
            }
        })
    }

    fun getChatResponse(
        question: String,
        onResponse: (String) -> Unit
    ) {
        val jsonBody = JSONObject()

        try {
            jsonBody.put("model", "gpt-3.5-turbo-16k")
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        } catch (e:JSONException){
            e.printStackTrace()
        }

        val body : RequestBody = jsonBody.toString().toRequestBody(JSON)

        val request:Request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $CHAT_BOT_API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(
            object :Callback {

                override fun onFailure(call: Call, e: IOException) {
                    onResponse("onFailure: Failed to load response due to ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        var jsonObject: JSONObject? = null

                        try {
                            jsonObject = JSONObject(response.body!!.string())
                            val jsonArray = jsonObject.getJSONArray("choices")
                            val result = jsonArray.getJSONObject(0).getString("text")
                            onResponse(result.trim { it <= ' ' })
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            onResponse("responseFailureJSONException: Failed to load response due to ${response.body.toString()}")
                        }
                    } else {
                        onResponse("responseFailure: Failed to load response due to ${response.body.toString()}")
                    }
                }
            }
        )
    }


}