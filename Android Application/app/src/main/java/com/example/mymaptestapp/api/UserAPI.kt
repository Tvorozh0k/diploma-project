package com.example.mymaptestapp.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserAPI {
    @POST("update/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Header("Authorization") accessToken: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>
}