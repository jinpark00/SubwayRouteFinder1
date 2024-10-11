package com.example.subwayroutefinder

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/routes")  // 실제 API URL에 맞게 수정
    fun getRoutes(): Call<List<Route>>

    @GET("api/routes")
    fun getRouteByStations(
        @Query("departure") departure: String,
        @Query("arrival") arrival: String
    ): Call<Route>
}