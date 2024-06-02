package com.example.mymaptestapp.data.entities

data class DeliveryPoint(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String
)