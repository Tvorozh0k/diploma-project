package com.example.mymaptestapp.api.utils

import android.content.Context
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.DeliveryPointAPI
import com.example.mymaptestapp.data.entities.DeliveryPoint
import com.example.mymaptestapp.utils.JwtTokenUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DeliveryPointUtils {
    companion object {
        private var points: List<DeliveryPoint> = listOf()

        /**
         * Подгружаем данные о точках на карте
         */
        suspend fun setPoints(context: Context) {
            val retrofit = Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_ip) + "/points/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val deliveryPointService = retrofit.create(DeliveryPointAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val response = deliveryPointService.getAll(
                "Bearer ".plus(accessToken)
            )

            if (response.code() == 200) {
                points = response.body() ?: listOf()
            }
        }

        fun mapPoints(): Map<Int, DeliveryPoint> {
            val result: HashMap<Int, DeliveryPoint> = hashMapOf()

            for (point in points) {
                result[point.id] = point
            }

            return result
        }

        fun getPoints(): List<DeliveryPoint> {
            return points
        }
    }
}