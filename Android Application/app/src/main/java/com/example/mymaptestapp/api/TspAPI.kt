package com.example.mymaptestapp.api

import com.example.mymaptestapp.api.models.TspRequest
import com.example.mymaptestapp.api.models.TspResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TspAPI {
    @POST("getSolution")
    suspend fun getSolution(
        @Header("Authorization") accessToken: String,
        @Body requestBody: TspRequest
    ): Response<TspResponse>
}