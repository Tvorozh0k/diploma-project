package com.example.mymaptestapp.api

import com.example.mymaptestapp.api.models.AddRouteRequest
import com.example.mymaptestapp.data.entities.UserRoute
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserRouteAPI {
    @POST("add")
    suspend fun addRoute(
        @Header("Authorization") accessToken: String,
        @Body requestBody: AddRouteRequest
    ): Response<ResponseBody>

    @GET("get/{id}")
    suspend fun getAll(
        @Path("id") id: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<UserRoute>>

    @POST("delete/{id}")
    suspend fun removeRoute(
        @Path("id") id: Int,
        @Header("Authorization") accessToken: String
    ): Response<ResponseBody>
}