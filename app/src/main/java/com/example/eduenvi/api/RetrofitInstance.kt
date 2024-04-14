package com.example.eduenvi.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    var gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    val api: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(Utils.BASE)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
            .build()
            .create(ApiInterface::class.java)
    }
}

