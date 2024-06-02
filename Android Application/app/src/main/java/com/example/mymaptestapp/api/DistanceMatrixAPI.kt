package com.example.mymaptestapp.api

import com.example.mymaptestapp.api.models.DistanceMatrixRequest
import com.example.mymaptestapp.api.models.DistanceMatrixResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface DistanceMatrixAPI {
    @POST("/v2/matrix/{profile}")
    suspend fun getDistance(
        @Path("profile") profile: String,
        @Header("Authorization") apiToken: String,
        @Body requestBody: DistanceMatrixRequest
    ): Response<DistanceMatrixResponse>
}