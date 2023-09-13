package com.makaota.mammamskitchen.interfaces

import com.makaota.mammamskitchen.models.PushNotification
import com.makaota.mammamskitchen.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=${Constants.SERVER_KEY}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}