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

    fun getResFun(question:String, callback : (String) -> Unit) {

        val query : String = "Your name is Doctor Vision GPT.\n" +
                "You are an eye specialist.\n" +
                "You will be asked queries related to eye problems.\\n\\n\n" +
                "If the query is eye related answer it with appropriate solution\n" +
                "or else reply \"Sorry that query doesn't align with any eye related problems\".\n" + question
        Log.d("ChatResponse", "Query: $query")
        val headers = mapOf("Authorization" to CHAT_AUTHORIZATION)
        val payload = mapOf("inputs" to query)

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
                Log.d("ChatResponseDebug", responseBody)
                try {
                    // Parse the JSON array
                    val jsonArray = JSONArray(responseBody)
                    if (jsonArray.length() > 0) {
                        val generatedText = jsonArray.getJSONObject(0).getString("generated_text")
                        callback(extractAnswer(generatedText, query))
                    } else {
                        callback("No generated text found in response")
                    }
                } catch (e: Exception) {
                    callback("Error parsing JSON response: ${e.message}")
                }
            }
        })
    }

    fun extractAnswer(generatedText: String, query: String): String {
        // Extract the answer from the generated text, the generated text is in the format: "$query\n\n*Text to be extracted*"
        val answer = generatedText.substringAfter(query).trim()
        return if (answer.isNotEmpty()) {
            answer
        } else {
            "No answer found in response"
        }
    }

}