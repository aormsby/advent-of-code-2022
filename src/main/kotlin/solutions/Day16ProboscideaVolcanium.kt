package solutions

import utils.Input
import utils.Solution
import kotlin.math.max

// run only this day
fun main() {
    Day16ProboscideaVolcanium()
}

class Day16ProboscideaVolcanium : Solution() {
    private val thirty = 30

    init {
        begin("Day 16 - Proboscidea Volcanium")

        val input = Input.parseLines(filename = "/d16_valve_map.txt")
            .map { line ->
                val captureGroups = """Valve (\S{2}).*=(\d+);.*(?:valve|valves) (.+)""".toRegex()
                    .find(line)!!.groupValues.drop(1)

                Valve(
                    name = captureGroups[0],
                    flowRate = captureGroups[1].toInt(),
                    flowTotalFrom = IntRange(1, thirty).map { it * captureGroups[1].toInt() }.reversed(),
                    connections = captureGroups[2].split(", ")
                )
            }

        val fullGraph = input.associateBy { v -> v.name }
        generateTravelMaps(fullGraph)

        val sol1 = releaseMostPressure(fullGraph)
        output("Max Pressure Release", sol1)

//        val sol2 = releasePressureElephantStyle(fullGraph)
//        output("Max Elephant-Assisted Pressure Release", sol2)
    }

    private fun releaseMostPressure(graph: Map<String, Valve>): Int {
        val x = findAllPaths(
            graph.filter { it.value.flowRate != 0 || it.value.name == "AA" },
            start = graph["AA"]!!,
        )
        return x.keys.max()
    }

//    private fun releasePressureElephantStyle(graph: Map<String, Valve>): Int {
//        val pathMap = findAllPaths(
//            graph.filter { it.value.flowRate != 0 || it.value.name == "AA" },
//            start = "AA",
//            startMinute = 4
//        ).mapKeys { it.key.removePrefix("AA,") }
//
//        val paths = with(pathMap.entries.sortedByDescending { it.value }.map { it.key }) {
//            associateWith { it.split(',').toSet() }
//        }
//        var maxAssistedFlow = 0
//
//        for (p in paths) {
//            for (p2 in paths) {
//                if (p.value.intersect(p2.value).isEmpty()) {
//                    maxAssistedFlow = max(
//                        maxAssistedFlow,
//                        pathMap[p.key]!! + pathMap[p2.key]!!
//                    )
//                    break
//                }
//            }
//        }
//
//        return maxAssistedFlow
//    }

    private fun findAllPaths(
        graph: Map<String, Valve>,
        start: Valve,
        curPath: MutableList<Valve> = mutableListOf(),
        startMinute: Int = 1
    ): Map<Int, List<Valve>> {
        // local var init
        val scoreMap = mutableMapOf<Int, List<Valve>>()

        // add current node to path
        curPath.add(start)

        val curSteps = mapSteps(curPath, startMinute)
        if (curSteps.last() > thirty - startMinute) {
            return scoreMap
        }

        // get connected valves
        val next = graph.values.filter { it !in curPath }

        // if at the end of a path, record and break out
        if (next.isEmpty()) {
            scoreMap[calculateTotalFlow(curPath, curSteps, startMinute - 1)] = curPath
            return scoreMap
        } else {

            for (n in next) {
                val result = findAllPaths(graph, n, curPath, startMinute)

                if (result.isNotEmpty()) {
                    result.forEach {
                        scoreMap[it.key] = it.value
                    }
                } else {
                    scoreMap[calculateTotalFlow(curPath, curSteps, startMinute - 1)] = curPath
                }

                curPath.removeLast()
            }
        }

        return scoreMap
    }

    // +1 to each for open valve action
    private fun mapSteps(path: List<Valve>, startMinute: Int): List<Int> =
        path.windowed(2).runningFold(initial = startMinute) { acc, w ->
            acc + w.first().stepsTo[w.last().name]!! + 1
        }

    // calculates flow of open valves across time
    private fun calculateTotalFlow(
        path: List<Valve>,
        steps: List<Int>,
        minuteOffset: Int
    ): Int {
        val x = steps.foldIndexed(initial = 0) { i, acc, s ->
            acc + path[i].flowTotalFrom[s - 1 + minuteOffset]
        }

        return x
    }

    private fun generateTravelMaps(vGraph: Map<String, Valve>) {
        val valveKeys = vGraph.keys

        valveKeys.forEach { k ->
            val v = vGraph[k]!!
            v.stepsTo[v.name] = 0

            val searchFrontier = mutableListOf(k)
            val visited = mutableListOf<String>()
            val parent = mutableMapOf<String, String>()

            // until all step counts found
            while (v.stepsTo.size < valveKeys.size) {
                val curCon = searchFrontier.removeFirst()
                visited.add(curCon)

                val newCons = vGraph[curCon]!!.connections.filterNot { it in visited }
                searchFrontier.addAll(newCons)

                newCons.forEach { n ->
                    // add connection parent
                    parent[n] = curCon

                    // add steps from current valve
                    var nextParent = n
                    var steps = 0

                    while (nextParent != k) {

                        // use existing data to short-circuit parent search
                        // (seems like it doesn't have much effect...)
                        if (v.stepsTo.keys.contains(nextParent)) {
                            steps += v.stepsTo[nextParent]!!
                            break
                        }

                        nextParent = parent[nextParent]!!
                        steps++
                    }

                    v.stepsTo[n] = steps
                }
            }
        }
    }

    private data class Valve(
        val name: String,
        val flowRate: Int,
        val flowTotalFrom: List<Int>,
        val connections: List<String>,
        val stepsTo: MutableMap<String, Int> = mutableMapOf(),
    )
}


/**
 * optimize notes - maybe no need to do dfs twice
 *
 * √ - try using valves instead of string lists to reduce conversions during dfs (may not help)
 * ? - pass 30-minute search into part 2
 * ? - don't to dfs again, but loop over map of valves and check if over time (at the same time as intersecting?)
 * ? - remove time offset from dfs since it's only needed for p2 calculations
 * x - address todos/cleanup, comment nicely
 *
 * Benchmark
 * Part 1 -> Max Pressure Release = 2250
 *   -- 490 ms!!
 * Part 2 -> Max Elephant-Assisted Pressure Release = 3015
 *   -- 1767 ms
 */