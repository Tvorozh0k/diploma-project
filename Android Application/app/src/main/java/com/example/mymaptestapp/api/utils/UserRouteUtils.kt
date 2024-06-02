package com.example.mymaptestapp.api.utils

import android.content.Context
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.UserRouteAPI
import com.example.mymaptestapp.api.models.AddRouteRequest
import com.example.mymaptestapp.data.entities.UserRoute
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRouteUtils {
    companion object {
        private var routes: List<UserRoute> = listOf()

        /**
         * Подгружаем данные о маршрутах пользователя
         */
        suspend fun setRoutes(context: Context) {
            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/userRoute/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val userRouteService = retrofit.create(UserRouteAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val response = userRouteService.getAll(
                UserDataUtils.getUserData()!!.user.id,
                "Bearer ".plus(accessToken)
            )

            if (response.code() == 200) {
                routes = response.body() ?: listOf()
            }
        }

        /**
         * Добавление нового маршрута
         */
        suspend fun addRoute(context: Context, requestBody: AddRouteRequest): Int {
            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/userRoute/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val userRouteService = retrofit.create(UserRouteAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val responseBody = userRouteService.addRoute(
                "Bearer ".plus(accessToken),
                requestBody
            )

            setRoutes(context)

            return JSONSerializer.responseBodyToMap(responseBody.body()!!)["routeId"] as Int
        }

        /**
         * Удаление маршрута
         */
        suspend fun removeRoute(context: Context, routeId: Int) {
            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/userRoute/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val userRouteService = retrofit.create(UserRouteAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            userRouteService.removeRoute(
                routeId,
                "Bearer ".plus(accessToken)
            )

            setRoutes(context)
        }

        fun getRoutes(): List<UserRoute> {
            return routes
        }
    }
}