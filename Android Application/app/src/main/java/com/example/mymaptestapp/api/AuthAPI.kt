package com.example.mymaptestapp.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthAPI {
    @POST("login")
    suspend fun loginRequest(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("register")
    suspend fun registerRequest(@Body requestBody: RequestBody)

    @POST("refresh")
    suspend fun refreshRequest(@Header("Authorization") refreshToken: String): Response<ResponseBody>
}