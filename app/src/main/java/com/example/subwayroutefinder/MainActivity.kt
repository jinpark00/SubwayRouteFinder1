package com.example.subwayroutefinder


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI 요소 선언
    private lateinit var startStationInput: EditText
    private lateinit var destinationStationInput: EditText
    private lateinit var searchButton: Button

    // 그래프 데이터: 역과 거리 정보
    private val stationsGraph: MutableMap<String, MutableList<Pair<String, Int>>> = mutableMapOf(
        "A" to mutableListOf(Pair("B", 10), Pair("C", 15)),
        "B" to mutableListOf(Pair("D", 12), Pair("E", 15)),
        "C" to mutableListOf(Pair("F", 10)),
        "D" to mutableListOf(Pair("E", 2), Pair("F", 1)),
        "E" to mutableListOf(Pair("G", 5)),
        "F" to mutableListOf(Pair("G", 10)),
        "G" to mutableListOf()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        startStationInput = findViewById(R.id.startStationInput)
        destinationStationInput = findViewById(R.id.destinationStationInput)
        searchButton = findViewById(R.id.searchButton)

        // 버튼 클릭 시 동작 정의
        searchButton.setOnClickListener {
            val startStation = startStationInput.text.toString()
            val destinationStation = destinationStationInput.text.toString()

            if (startStation.isNotEmpty() && destinationStation.isNotEmpty()) {
                // 백그라운드에서 경로 탐색 (다익스트라 알고리즘 적용)
                CoroutineScope(Dispatchers.IO).launch {
                    val result = dijkstra(startStation, destinationStation)

                    withContext(Dispatchers.Main) {
                        // 검색 결과를 새로운 화면에 전달
                        val intent = Intent(this@MainActivity, SearchResultActivity::class.java)
                        intent.putExtra("searchResult", result)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    // 다익스트라 알고리즘 구현
    private fun dijkstra(start: String, destination: String): String {
        val distances = mutableMapOf<String, Int>()
        val previousNodes = mutableMapOf<String, String?>()
        val unvisited = PriorityQueue<Pair<String, Int>>(compareBy { it.second })

        // 초기값 설정
        stationsGraph.keys.forEach {
            distances[it] = Int.MAX_VALUE
            previousNodes[it] = null
        }
        distances[start] = 0
        unvisited.add(Pair(start, 0))

        while (unvisited.isNotEmpty()) {
            val (currentStation, currentDistance) = unvisited.poll()

            if (currentStation == destination) {
                // 목적지에 도달한 경우 경로 재구성
                return reconstructPath(previousNodes, start, destination, distances[destination] ?: 0)
            }

            stationsGraph[currentStation]?.forEach { (neighbor, weight) ->
                val distance = currentDistance + weight
                if (distance < distances[neighbor] ?: Int.MAX_VALUE) {
                    distances[neighbor] = distance
                    previousNodes[neighbor] = currentStation
                    unvisited.add(Pair(neighbor, distance))
                }
            }
        }

        return "경로를 찾을 수 없습니다."
    }

    // 경로 재구성 함수
    private fun reconstructPath(previousNodes: Map<String, String?>, start: String, destination: String, totalDistance: Int): String {
        var path = destination
        var currentNode = destination

        while (previousNodes[currentNode] != null) {
            currentNode = previousNodes[currentNode]!!
            path = "$currentNode -> $path"
        }

        return if (currentNode == start) {
            "최단 경로: $path\n총 거리: $totalDistance"
        } else {
            "경로를 찾을 수 없습니다."
        }
    }
}