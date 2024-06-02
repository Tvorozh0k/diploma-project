package com.example.mymaptestapp.api.utils

import android.content.Context
import com.example.mymaptestapp.R
import com.example.mymaptestapp.adapters.models.RouteInfoItem
import com.example.mymaptestapp.adapters.models.RouteInfoItemType
import com.example.mymaptestapp.api.DistanceMatrixAPI
import com.example.mymaptestapp.api.TspAPI
import com.example.mymaptestapp.api.UserRouteAPI
import com.example.mymaptestapp.api.models.AddRouteRequest
import com.example.mymaptestapp.api.models.DistanceMatrixRequest
import com.example.mymaptestapp.api.models.TspRequest
import com.example.mymaptestapp.utils.JwtTokenUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NewRouteUtils {
    companion object {
        private var option: String? = null
        private var startPoint: Int? = null
        private var routePoints: HashSet<Int> = hashSetOf()

        fun setOption(option: String?) {
            Companion.option = option
        }

        fun getOption(): String? {
            return option
        }

        fun setStartPoint(id: Int?) {
            startPoint = id
        }

        fun getStartPoint(): Int? {
            return startPoint
        }

        fun addRoutePoint(id: Int) {
            routePoints.add(id)
        }

        fun removeRoutePoint(id: Int) {
            routePoints.remove(id)
        }

        fun clearRoutePoints() {
            routePoints = hashSetOf()
        }

        fun routePointsContains(id: Int): Boolean {
            return routePoints.contains(id)
        }

        fun isRoutePointsEmpty(): Boolean {
            return routePoints.isEmpty()
        }

        fun getRoutePoints(): HashSet<Int> {
            return routePoints
        }

        private suspend fun getDistanceMatrix(locations: List<List<Double>>): List<List<Double>> {
            val apiToken = "" // API-токен для работы с Openrouteservice

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val distMatrixService = retrofit.create(DistanceMatrixAPI::class.java)

            val requestBody = DistanceMatrixRequest(locations, listOf("distance"))

            val response = distMatrixService.getDistance(
                "driving-car",
                apiToken,
                requestBody
            )

            return if (response.code() == 200) {
                val result = response.body()!!.distances

                for (i in result.indices) {
                    for (j in result.indices) {
                        if (i == j || result[i][j] == null) {
                            result[i][j] = 1e9 + 7
                        }
                    }
                }

                List(result.size) { i -> List(result.size) { j -> result[i][j]!! } }
            } else {
                listOf()
            }
        }

        suspend fun getSolution(context: Context, updateInfo: (Double, Int) -> Unit): List<RouteInfoItem> {
            // Отсортированный список всех вершин маршрута
            val sortedPoints =
                ((startPoint?.let { listOf(it) } ?: emptyList()) + routePoints.toList()).sorted()

            // Все точки в словаре
            val mapPoints = DeliveryPointUtils.mapPoints()

            // Координаты точек
            val locations = List(sortedPoints.size) { i ->
                listOf(
                    mapPoints.getValue(sortedPoints[i]).longitude,
                    mapPoints.getValue(sortedPoints[i]).latitude
                )
            }

            // Матрица расстояний между точками на карте
            val distMatrix = getDistanceMatrix(locations)

            // Стартовая точка
            val startPoint = sortedPoints.indexOfFirst { id -> id == startPoint }

            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/tsp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val tspService = retrofit.create(TspAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val requestBody = TspRequest(option!!, startPoint, distMatrix)

            val response = tspService.getSolution(
                "Bearer ".plus(accessToken),
                requestBody
            )

            return if (response.code() == 200) {
                val responseBody = response.body()!!.solution

                val userId = UserDataUtils.getUserData()!!.user.id
                val routeLength = response.body()!!.len
                val routePoints = List(responseBody.size) { i -> sortedPoints[responseBody[i]] }

                val routeId = UserRouteUtils.addRoute(context, AddRouteRequest(userId, routePoints, routeLength / 1000.0))

                updateInfo(routeLength, routeId)

                List(responseBody.size) { i ->
                    RouteInfoItem(
                        if (i == 0) RouteInfoItemType.START_ITEM else (if (i < responseBody.size - 1) RouteInfoItemType.LIST_ITEM else RouteInfoItemType.FINAL_ITEM),
                        i + 1,
                        mapPoints.getValue(routePoints[i])
                    )
                }
            } else {
                listOf()
            }
        }
    }
}