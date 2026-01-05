package com.example.brainrowth.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // emulator: "http://10.0.2.2:3000/"
    private const val BASE_URL = "http://192.168.1.7:3000/"

    val api: BrainRowthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrainRowthApi::class.java)
    }
}