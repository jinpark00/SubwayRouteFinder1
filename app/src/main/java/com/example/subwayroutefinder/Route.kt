package com.example.subwayroutefinder

data class Route(
    val id: Int,
    val departureStation: String,
    val arrivalStation: String,
    val distance: Int,
    val time: Int,
    val cost: Int
)

