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

        val sol1 = releaseMostPressure(input)
        output("Max Pressure Release", sol1)

        val sol2 = "2222"
        output("string 2", sol2)
    }

    private fun releaseMostPressure(input: List<Valve>): Int {
        // TODO: probably extract and use with part 2
        val fullGraph = input.associateBy { v -> v.name }
        generateTravelMaps(fullGraph)
        return optimalPath(fullGraph.filter { it.value.flowRate != 0 || it.value.name == "AA" }, start = "AA")
    }

    private fun optimalPath(
        valves: Map<String, Valve>,
        start: String,
        maxFlow: Int = 0,
        path: MutableList<String> = mutableListOf()
    ): Int {
        var max = maxFlow
        path.add(start)

        val v = valves[start]!!
        val nextOpen = valves.map { it.value.name }.filter { it !in path }

        if (nextOpen.isEmpty()) {
            max = max(max, calculateTotalFlow(path.map { valves[it]!! }))
        } else {
            nextOpen.forEach {
                max = max(max, optimalPath(valves, start = it, max, path))
                path.removeLast()
            }
        }

        return max
    }

    /** try DFS
     *      - need to find the highest flow rate based on order of opening valves
     *      - how to short circuit?
     *       ?? + if totalFlow + (remaining valves * remaining time) < highest recorded flow, skip
     */

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

    private fun calculateTotalFlow(pathItems: List<Valve>): Int {
        var minute = 1
        var totalFlow = 0

        pathItems.windowed(2).forEach { w ->
            // +1 for opening valve action
            minute += (w.first().stepsTo[w.last().name]!! + 1)

            // TODO: remove if if this doesn't happen
            // early out
            if (minute > 30)
                return -1

            // add to total flow for remaining minutes
            totalFlow += w.last().flowTotalAt[minute - 1]
        }

        return totalFlow
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

/**
 * idea!
 *
 * generate a map of possible flow accumulation by each minute on - for each valve
 *      - 30 minutes open produces x
 *      - 29 minutes open produces x
 *      - and so on
 *
 * then generate a graph of steps from each point to each other point
 *      - start with AA (or if it makes sense, find the valve most connected to others?)
 *      - BFS and store the least number of steps to each other valve
 *      - mark AA as 'solved'
 *      - when BFSing from other valves, check each connected valve for a solved number of steps
 *          + this should reduce the number of iterations more with each solve
 *      - when all solved, go save the elephants
 *
 * then during each round
 *      - make a list of remaining open valves paired with the remaining flow rate available
 *          once you can open them (+ 2 minutes for move and open)
 *      - move to the best one
 */


//var curValve = vGraph["AA"]!!
//
//var openFlowRate = 0
//var hasFlown = 0
//
//// time progression includes 1
//for (t in 30 downTo 1) {
//
//    // track flow each round
//    hasFlown += openFlowRate
//
//    println("${31 - t}, $openFlowRate")
//
//    with(curValve) {
//        // connected valves, sorted by highest flow rate
//        val next = connections.map { vGraph[it]!! }
//            .sortedByDescending { it.flowRate }
//
//        // valves with higher flow rates that aren't open yet
//        val better = next.filter { it.flowRate > flowRate && it.isOpen.not() }
//
//        when {
//            // open, find better options
//            isOpen -> {
//                // move to better
//                curValve =
//                    if (better.isNotEmpty())
//                        better.first()
//                    else next.first { it.isOpen.not() }
//                // if none better... this is the unsure bit
//            }
//
//            // not open, but there are better options
//            better.isNotEmpty() -> {
//                curValve = better.first()
//            }
//
//            // not open, best option
//            else -> {
//                isOpen = true
//                openFlowRate += flowRate
//            }
//        }
//    }
//}
//
//return hasFlown