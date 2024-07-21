package com.example.gic_assignment.utils

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object RetrofitClient {
    private const val FCM_BASE_URL = "https://fcm.googleapis.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "key=AIzaSyBUg0v7xHMeRdd_k7E3Tkr7rRoLzcdm2OA")
                    .header("Content-Type", "application/json")
                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(FCM_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val fcmService: FcmService = retrofit.create(FcmService::class.java)
}

interface FcmService {
    @Headers("Content-Type: application/json")
    @POST("fcm/send")
    fun sendNotification(@Body notification: FcmNotification): Call<Void>
}

