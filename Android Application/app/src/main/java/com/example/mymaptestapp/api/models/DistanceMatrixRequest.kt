package com.example.mymaptestapp.api.models

data class DistanceMatrixRequest(
    val locations: List<List<Double>>,
    val metrics: List<String>
)