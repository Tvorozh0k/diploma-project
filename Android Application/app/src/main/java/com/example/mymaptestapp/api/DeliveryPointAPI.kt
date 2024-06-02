package com.example.mymaptestapp.api

import com.example.mymaptestapp.data.entities.DeliveryPoint
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DeliveryPointAPI {
    @GET("get")
    suspend fun getAll(@Header("Authorization") accessToken: String): Response<List<DeliveryPoint>>
}