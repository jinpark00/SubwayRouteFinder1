package com.example.subwayroutefinder

import java.util.PriorityQueue

class RouteFinder(routes: List<Route>) {

    private val graph = mutableMapOf<String, MutableList<Pair<String, Int>>>()

    init {
        routes.forEach { route ->
            val from = route.departureStation
            val to = route.arrivalStation
            val cost = route.cost

            graph.computeIfAbsent(from) { mutableListOf() }.add(Pair(to, cost))
            graph.computeIfAbsent(to) { mutableListOf() }.add(Pair(from, cost))
        }
    }

    fun findShortestPath(start: String, destination: String): Int {
        val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
        distances[start] = 0

        val priorityQueue = PriorityQueue(compareBy<Pair<String, Int>> { it.second })
        priorityQueue.add(Pair(start, 0))

        while (priorityQueue.isNotEmpty()) {
            val (currentStation, currentCost) = priorityQueue.poll()

            if (currentCost > distances.getValue(currentStation)) continue

            graph[currentStation]?.forEach { (nextStation, cost) ->
                val newCost = currentCost + cost
                if (newCost < distances.getValue(nextStation)) {
                    distances[nextStation] = newCost
                    priorityQueue.add(Pair(nextStation, newCost))
                }
            }
        }

        return distances.getOrDefault(destination, -1)
    }
}