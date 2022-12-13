package solutions

import models.Coord2d
import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day12HillClimbingAlgorithm()
}

class Day12HillClimbingAlgorithm : Solution() {

    init {
        begin("Day 12 - Hill Climbing Algorithm")

        var start = Coord2d(0, 0)
        var end = Coord2d(0, 0)
        val input = Input.parseLines(filename = "/d12_heightmap.txt")
            .mapIndexed { i, s ->
                s.mapIndexed char@{ j, c ->
                    when (c) {
                        'S' -> {
                            start = Coord2d(i, j)
                            return@char 'a'
                        }

                        'E' -> {
                            end = Coord2d(i, j)
                            return@char 'z'
                        }
                    }
                    return@char c
                }
            }

        val sol1 = findPath(input, start, end)
        output("Shortest Path", sol1.size - 1)

        val sol2 = findHikingPath(input, end)
        output("Shortest Hiking Path", sol2.size - 1)
    }

    // breadth first search, then retrace steps from end to start
    private fun findPath(graph: List<List<Char>>, start: Coord2d, end: Coord2d): List<Coord2d> {
        val frontier = mutableListOf(start)
        val parentOf = mutableMapOf(start to start)

        val xLimit = graph.size - 1
        val yLimit = graph.first().size - 1

        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            if (current == end) break

            current.adjacentNeighbors(xLimit, yLimit)
                .filter { graph[it.x][it.y] <= (graph[current.x][current.y] + 1) && it !in parentOf.keys }
                .forEach {
                    frontier.add(it)
                    parentOf[it] = current
                }
        }

        val path = mutableListOf(end)
        while (path.first() != start) {
            val cur = path.first()
            path.add(0, parentOf[cur]!!)
        }

        return path
    }

    // backwards breadth first search from end to first 'a', then retrace steps from 'a' to end
    private fun findHikingPath(graph: List<List<Char>>, end: Coord2d): List<Coord2d> {
        val frontier = mutableListOf(end)
        val childOf = mutableMapOf(end to end)

        val xLimit = graph.size - 1
        val yLimit = graph.first().size - 1

        val path = mutableListOf<Coord2d>()

        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            if (graph[current.x][current.y] == 'a') {
                path.add(current)
                break
            }

            current.adjacentNeighbors(xLimit, yLimit)
                .filter { graph[it.x][it.y] >= (graph[current.x][current.y] - 1) && it !in childOf.keys }
                .forEach {
                    frontier.add(it)
                    childOf[it] = current
                }
        }

        while (path.last() != end) {
            val cur = path.last()
            path.add(childOf[cur]!!)
        }

        return path
    }
}