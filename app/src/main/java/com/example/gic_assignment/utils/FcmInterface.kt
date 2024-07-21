package com.example.gic_assignment.utils

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class FcmNotification(
    val to: String,
    val data: Map<String, String>
)

interface FcmInterface {
    @Headers(
        "Authorization: key=AIzaSyBUg0v7xHMeRdd_k7E3Tkr7rRoLzcdm2OA",
        "Content-Type: application/json"
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: FcmNotification): Call<Void>
}
