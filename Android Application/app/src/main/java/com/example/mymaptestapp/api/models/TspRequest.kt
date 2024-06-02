package com.example.mymaptestapp.api.models

data class TspRequest(
    val option: String,
    val startPoint: Int,
    val distMatrix: List<List<Double>>
)