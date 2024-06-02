package com.example.mymaptestapp.adapters.models

import com.example.mymaptestapp.data.entities.DeliveryPoint

data class RouteInfoItem(
    val type: RouteInfoItemType,
    val index: Int,
    val point: DeliveryPoint
)