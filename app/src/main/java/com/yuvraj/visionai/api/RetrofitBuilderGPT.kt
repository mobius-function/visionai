package com.yuvraj.visionai.api

import com.yuvraj.visionai.utils.Constants.CHAT_AUTHORIZATION
import com.yuvraj.visionai.utils.Constants.CHAT_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilderGPT {
    private var retrofit: Retrofit? = null
    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", CHAT_AUTHORIZATION)
                .addHeader("Content-Type","application/json")
                .build()
            chain.proceed(request)
        })
    }.build()

    fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(CHAT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
        }
        return retrofit!!
    }
}