package com.example.subwayroutefinder

import java.util.PriorityQueue
// 그래프 형태로 데이터를 저장
class RouteFinder(routes: List<Route>) {

    private val graph = mutableMapOf<String, MutableList<Pair<String, Int>>>()

    init {
        // 출발역과 도착역 간의 경로 데이터를 그래프로 변환
        routes.forEach { route ->
            val from = route.departureStation
            val to = route.arrivalStation
            val cost = route.cost
        // 양방향 그래프 설정 (양쪽 경로를 모두 추가)
            graph.computeIfAbsent(from) { mutableListOf() }.add(Pair(to, cost))
            graph.computeIfAbsent(to) { mutableListOf() }.add(Pair(from, cost))
        }
    }
    // 다익스트라 알고리즘으로 최소 비용 경로 찾기
    fun findShortestPath(start: String, destination: String): Int {
        // 각 역까지의 최소 비용을 저장하는 맵 (기본값은 무한대)
        val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
        distances[start] = 0
        // 우선순위 큐를 이용하여 탐색할 역과 비용을 추적
        val priorityQueue = PriorityQueue(compareBy<Pair<String, Int>> { it.second })
        priorityQueue.add(Pair(start, 0))

        while (priorityQueue.isNotEmpty()) {
            val (currentStation, currentCost) = priorityQueue.poll()
            // 현재 역까지의 비용이 이미 기록된 비용보다 크면 무시
            if (currentCost > distances.getValue(currentStation)) continue
            // 현재 역에서 연결된 인접한 역들의 거리 계산
            graph[currentStation]?.forEach { (nextStation, cost) ->
                val newCost = currentCost + cost
                if (newCost < distances.getValue(nextStation)) {
                    distances[nextStation] = newCost
                    priorityQueue.add(Pair(nextStation, newCost))
                }
            }
        }
        // 목적지까지의 최소 비용 반환 (경로가 없으면 -1 반환)
        return distances.getOrDefault(destination, -1)
    }
}