package com.example.brainrowth.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // emulator: "http://10.0.2.2:3000/"
    // pair wifi hp: "http://IP_CONNENCTION_HP:3000/"
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val api: BrainRowthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrainRowthApi::class.java)
    }
}