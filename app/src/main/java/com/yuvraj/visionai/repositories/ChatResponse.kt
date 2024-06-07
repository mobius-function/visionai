package com.yuvraj.visionai.repositories

import android.util.Log
import com.yuvraj.visionai.utils.Constants.CHAT_AUTHORIZATION
import com.yuvraj.visionai.utils.Constants.CHAT_BASE_URL
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object ChatResponse {
    private const val API_URL = CHAT_BASE_URL
    private val headers = mapOf("Authorization" to CHAT_AUTHORIZATION)

    fun getResFun(question:String, callback : (String) -> Unit) {
        val payload = mapOf("inputs" to question)

        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = JSONObject(payload).toString()
            .toRequestBody(mediaType)

        val requestBuilder = Request.Builder()
            .url(API_URL)
            .post(body)

        headers.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        val request = requestBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback("Unexpected code $response")
                    return
                }
                val responseBody = response.body?.string() ?: "No response from API"
                Log.d("ChatResponse", responseBody)
                try {
                    // Parse the JSON array
                    val jsonArray = JSONArray(responseBody)
                    if (jsonArray.length() > 0) {
                        val generatedText = jsonArray.getJSONObject(0).getString("generated_text")
                        callback(generatedText)
                    } else {
                        callback("No generated text found in response")
                    }
                } catch (e: Exception) {
                    callback("Error parsing JSON response: ${e.message}")
                }
            }
        })
    }

    fun extractAnswer(generatedText: String): String {
        val parts = generatedText.split("\n\n")
        return if (parts.size > 1) {
            parts[1].trim()
        } else {
            "No answer found"
        }
    }

}