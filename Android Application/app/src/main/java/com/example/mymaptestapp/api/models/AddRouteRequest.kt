package com.example.mymaptestapp.api.models

import java.util.Date

data class AddRouteRequest(
    val userId: Int,
    val routePoints: List<Int>,
    val routeLength: Double
)
