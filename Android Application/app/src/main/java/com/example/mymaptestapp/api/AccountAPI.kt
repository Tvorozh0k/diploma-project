package com.example.mymaptestapp.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountAPI {
    @POST("updateLogin/{id}")
    suspend fun updateLogin(
        @Path("id") id: Int,
        @Header("Authorization") accessToken: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @POST("checkPassword/{id}")
    suspend fun checkPassword(
        @Path("id") id: Int,
        @Header("Authorization") accessToken: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @POST("updatePassword/{id}")
    suspend fun updatePassword(
        @Path("id") id: Int,
        @Header("Authorization") accessToken: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @GET("getUser")
    suspend fun getUserAccount(@Header("Authorization") accessToken: String): Response<ResponseBody>
}