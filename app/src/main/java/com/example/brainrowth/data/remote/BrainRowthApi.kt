package com.example.brainrowth.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface BrainRowthApi {

    @POST("api/solve-text")
    suspend fun solveText(
        @Body request: SolveRequest
    ): SolveResponse
}