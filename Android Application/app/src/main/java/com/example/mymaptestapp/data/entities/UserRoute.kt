package com.example.mymaptestapp.data.entities

import java.util.Date

data class UserRoute(
    val id: Int,
    val routePoints: List<Int>,
    val createdAt: Date,
    val routeLength: Double
)
