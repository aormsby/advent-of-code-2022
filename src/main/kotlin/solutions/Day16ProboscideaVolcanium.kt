package solutions

import utils.Input
import utils.Solution
import kotlin.collections.set
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
                    flowTotalAt = IntRange(1, thirty).map { it * captureGroups[1].toInt() }.reversed(),
                    connections = captureGroups[2].split(", ")
                )
            }.associateBy { v -> v.name }

        generateTravelMaps(input)

        val paths = findValidPaths(
            input.filterNot { it.value.flowTotalAt[0] == 0 },
            input["AA"]!!,
            input.values.first().flowTotalAt.size
        )

        val sol1 = paths.maxOf { e -> e.totalFlow() }
        output("Max Solo Pressure Release", sol1)

        val sol2 = pairedValving(paths)
        output("Max Elephant-Assisted Pressure Release", sol2)
    }

    /**
     * @return a map where keys = valves, values = steps where valves flowing
     */
    private fun findValidPaths(
        graph: Map<String, Valve>,
        startNode: Valve,
        timeLimit: Int,
        curPath: ArrayDeque<Valve> = ArrayDeque()
    ): Map<List<Valve>, List<Int>> {
        val pMap = mutableMapOf<List<Valve>, List<Int>>()

        curPath.add(startNode)
        val stepMap = curPath.mapSteps()

        if (stepMap.last() > timeLimit - 1) {
            curPath.removeLastOrNull()
            return emptyMap()
        }

        val nextValves = graph.values.filterNot { it in curPath }

        nextValves.forEach { next ->
            val innerFind = findValidPaths(graph, next, timeLimit, curPath)
            innerFind.forEach { item ->
                pMap[item.key.toList()] = item.value
            }
        }

        pMap[curPath.toList()] = stepMap
        curPath.removeLastOrNull()
        return pMap
    }

    private fun pairedValving(p1Paths: Map<List<Valve>, List<Int>>, timeOffset: Int = 4): Int {
        val p26 = p1Paths.filterNot { it.key.size == 1 || it.value.last() > thirty - timeOffset - 1 }
            .mapValues { it.totalFlow(timeOffset) }.entries.sortedByDescending { it.value }

        var maxPairedFlow = 0

        for (p1 in p26) {
            val p1Set = p1.key.drop(1).toSet()

            for (p2 in p26) {
                val p2Set = p2.key.drop(1).toSet()

                if ((p1Set intersect p2Set).isEmpty()) {
                    maxPairedFlow = max(p1.value + p2.value, maxPairedFlow)
                    break
                }
            }
        }

        return maxPairedFlow
    }

//    if (p.value.intersect(p2.value).isEmpty()) {
//        maxAssistedFlow = max(
//            maxAssistedFlow,
//            pathMap[p.key]!! + pathMap[p2.key]!!
//        )
//        break
//    }

    private fun List<Valve>.mapSteps(): List<Int> {
        val steps = mutableListOf<Int>()

        windowed(2).forEach { win ->
            steps.add(win[0].minutesToOpen[win[1].name]!! + (steps.lastOrNull() ?: 0) + 1)
        }

        return if (steps.isEmpty()) listOf(0) else steps
    }

    private fun Map.Entry<List<Valve>, List<Int>>.totalFlow(offset: Int = 0): Int {
        val valves = key.drop(1)
        return if (valves.isEmpty()) {
            0
        } else {
            valves.mapIndexed { i, v ->
                v.flowTotalAt[value[i] + offset]
            }.sum()
        }
    }

    private fun generateTravelMaps(vGraph: Map<String, Valve>) {
        val valveKeys = vGraph.keys

        valveKeys.forEach { k ->
            val v = vGraph[k]!!
            v.minutesToOpen[v.name] = 0

            val searchFrontier = mutableListOf(k)
            val visited = mutableListOf<String>()
            val parent = mutableMapOf<String, String>()

            // until all step counts found
            while (v.minutesToOpen.size < valveKeys.size) {
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
                        if (v.minutesToOpen.keys.contains(nextParent)) {
                            steps += v.minutesToOpen[nextParent]!!
                            break
                        }

                        nextParent = parent[nextParent]!!
                        steps++
                    }

                    v.minutesToOpen[n] = steps
                }
            }
        }
    }

    private data class Valve(
        val name: String,
        val flowTotalAt: List<Int>,
        val connections: List<String>,
        val minutesToOpen: MutableMap<String, Int> = mutableMapOf(),
    )
}


/**
 * notes
 *
 * 1 - check that the total flows list is correct
 *
 *
 * Benchmark
 * Part 1 -> Max Pressure Release = 2250
 *   -- 694 ms
 * Part 2 -> Max Elephant-Assisted Pressure Release = 3015
 *   -- 1767 ms
 */