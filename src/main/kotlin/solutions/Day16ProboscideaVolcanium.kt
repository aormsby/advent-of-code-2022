package solutions

import utils.Input
import utils.Solution
import kotlin.math.max

// run only this day
fun main() {
    Day16ProboscideaVolcanium()
}

class Day16ProboscideaVolcanium : Solution() {
    init {
        begin("Day 16 - Proboscidea Volcanium")

        val input = Input.parseLines(filename = "/d16_valve_map.txt")
            .map { line ->
                val captureGroups = """Valve (\S{2}).*=(\d+);.*(?:valve|valves) (.+)""".toRegex()
                    .find(line)!!.groupValues.drop(1)

                Valve(
                    name = captureGroups[0],
                    flowRate = captureGroups[1].toInt(),
                    flowTotalAt = IntRange(1, 30).map { it * captureGroups[1].toInt() }.reversed(),
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
            start = "AA"
        )

        return x.values.max()
    }

    private fun releasePressureElephantStyle(graph: Map<String, Valve>): Int {
        val me = findAllPaths(
            graph.filter { it.value.flowRate != 0 || it.value.name == "AA" },
            start = "AA",
            maxTime = 26
        )

//        val elephant =
        return -1
    }

    private fun findAllPaths(
        graph: Map<String, Valve>,
        start: String,
        maxFlow: Int = 0,
        curPath: MutableList<String> = mutableListOf(),
        maxTime: Int = 30
    ): Map<String, Int> {
        // local var init
        var mflow = maxFlow
        val scoreMap = mutableMapOf<String, Int>()

        // add current node to path
        curPath.add(start)

        // if current steps are over the max time, just break out of this path - else maybe add a max flow
        if (overTime(curPath.map { graph[it]!! }, maxTime)) {
            return scoreMap
        } else scoreMap[curPath.joinToString(",")] = calculateTotalFlow(curPath.map { graph[it]!! }, maxTime)

        // TODO: if this works, try with actual valves, not strings, should reduce conversions
        val next = graph.map { it.value.name }.filter { it !in curPath }

        // when neighbors available
        for (n in next) {
            val result = findAllPaths(graph, n, mflow, curPath)
            if (result.isNotEmpty()) {
                result.forEach {
                    mflow = max(mflow, it.value)
                    scoreMap[it.key] = it.value
                }
            }
            curPath.removeLast()
        }

        return scoreMap
    }

    // the additional 'path.size - 1' adds a step for the 'open valve' action
    private fun overTime(path: List<Valve>, maxTime: Int = 30): Boolean =
        path.mapIndexed { i, v ->
            if (i < path.size - 1) {
                v.stepsTo[path[i + 1].name]!!
            } else 0
        }.sum() + (path.size) > maxTime


    // returns -1 is steps over max time
    private fun calculateTotalFlow(pathItems: List<Valve>, maxTime: Int = 30): Int {
        var minute = 1
        var totalFlow = 0

        pathItems.windowed(2).forEach { w ->
            // +1 for opening valve action
            minute += (w.first().stepsTo[w.last().name]!! + 1)

            // add to total flow for remaining minutes
            totalFlow += w.last().flowTotalAt[minute - 1]
        }

        return totalFlow
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

    // tODO: strip unneeded properties
    private data class Valve(
        val name: String,
        val flowRate: Int,
        val flowTotalAt: List<Int>,
        val connections: List<String>,
        var isOpen: Boolean = false,
        val stepsTo: MutableMap<String, Int> = mutableMapOf(),
    )
}

//private fun optimalFlowRelease(
//    graph: Map<String, Day16ProboscideaVolcanium.Valve>,
//    start: String,
//    maxFlow: Int = 0,
//    curPath: MutableList<String> = mutableListOf(),
//    maxTime: Int = 30
//): Int {
//    // local var init
//    var m = maxFlow
//
//    // add current to path and find neighbors
//    curPath.add(start)
//
//    // if current steps are over the max time, just break out of this path - else check max flow
//    val timeAndFlowCheck = calculateTotalFlow(curPath.map { graph[it]!! }, maxTime)
//    if (timeAndFlowCheck == -1) {
//        return m
//    } else m = max(m, timeAndFlowCheck)
//
//    val next = graph.map { it.value.name }.filter { it !in curPath }
//
//    // when neighbors available
//    for (n in next) {
//        m = max(m, optimalFlowRelease(graph, n, m, curPath))
//        curPath.removeLast()
//    }
//
//    return m
//}