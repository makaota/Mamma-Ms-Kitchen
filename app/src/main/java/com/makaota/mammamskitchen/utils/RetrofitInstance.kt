package com.makaota.mammamskitchen.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{

        // Create an OkHttp Interceptor to set the "Content-Type" header
        private val contentTypeInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val modifiedRequest = originalRequest.newBuilder()
                .header("Content-Type", Constants.CONTENT_TYPE)
                .build()
            chain.proceed(modifiedRequest)
        }

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient().newBuilder().addInterceptor(contentTypeInterceptor).build())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}