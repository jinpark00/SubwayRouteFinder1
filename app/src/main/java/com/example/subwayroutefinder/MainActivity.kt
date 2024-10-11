package com.example.subwayroutefinder

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var startStationInput: EditText
    private lateinit var destinationStationInput: EditText
    private lateinit var searchButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startStationInput = findViewById(R.id.startStationInput)
        destinationStationInput = findViewById(R.id.destinationStationInput)
        searchButton = findViewById(R.id.searchButton)
        resultTextView = findViewById(R.id.resultTextView)

        searchButton.setOnClickListener {
            val startStation = startStationInput.text.toString()
            val destinationStation = destinationStationInput.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 서버에서 경로 데이터를 가져오기
                    val routesResponse = RetrofitInstance.api.getRoutes()
                    routesResponse.enqueue(object : Callback<List<Route>> {
                        override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
                            if (response.isSuccessful) {
                                val routes = response.body() ?: emptyList()
                                val routeFinder = RouteFinder(routes)

                                // 다익스트라 알고리즘으로 최단 경로 찾기
                                val shortestPathCost = routeFinder.findShortestPath(startStation, destinationStation)

                                withContext(Dispatchers.Main) {
                                    if (shortestPathCost != -1) {
                                        resultTextView.text = "최소 비용: $shortestPathCost 원"
                                    } else {
                                        resultTextView.text = "경로를 찾을 수 없습니다."
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    resultTextView.text = "데이터를 가져오는 중 오류가 발생했습니다."
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Route>>, t: Throwable) {
                            withContext(Dispatchers.Main) {
                                resultTextView.text = "API 호출 실패