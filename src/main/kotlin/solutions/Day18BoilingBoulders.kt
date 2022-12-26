package solutions

import models.Coord3d
import utils.Input
import utils.Solution
import java.util.*
import kotlin.math.abs

// run only this day
fun main() {
    Day18BoilingBoulders()
}

class Day18BoilingBoulders : Solution() {
    init {
        begin("Day 18 - Boiling Boulders")

        val input = Input.parseToListOf<Coord3d>(filename = "/d18_lava_cubes.txt", delimiter = "\n")
            .map { Cube(it) }

        val sol1 = mapDroplet(input)
        output("Droplet Surface Area", sol1)

        val sol2 = mapDropletShell(input.filter { it.openSides != 0 })
        output("Outer Surface Area Only", sol2)
    }

    private fun mapDroplet(cubes: List<Cube>): Int {
        val cList = Stack<Cube>().apply { addAll(cubes) }

        while (cList.empty().not()) {
            // take next cube
            val curCube = cList.pop()

            cList.forEach { next ->
                val cPairs = curCube.c.pairWith(next.c)
                    .filterNot { it.first - it.second == 0 }

                if (cPairs.size == 1 && (abs(cPairs.first().first - cPairs.first().second)) == 1) {
                    curCube.openSides--
                    next.openSides--
                }
            }

            // remove proven inner cubes
            cList.removeAll { it.openSides == 0 }
        }

        return cubes.sumOf { it.openSides }
    }

    private fun mapDropletShell(keyCubes: List<Cube>): Int {
        val keyCoords = keyCubes.map { it.c }
        var outerSurface = 0

        val bounds = Triple(
            (keyCoords.minOf { it.x } - 1)..(keyCoords.maxOf { it.x } + 1),
            (keyCoords.minOf { it.y } - 1)..(keyCoords.maxOf { it.y } + 1),
            (keyCoords.minOf { it.z } - 1)..(keyCoords.maxOf { it.z } + 1),
        )

        val frontier = Stack<Coord3d>().apply {
            add(Coord3d(bounds.first.first, bounds.second.first, bounds.third.first))
        }
        val visited = mutableListOf<Coord3d>()

        while (frontier.isNotEmpty()) {
            val cur = frontier.pop()
            visited.add(cur)

            val neighbors = cur.adjacentNeighbors().filter {
                it.x in bounds.first && it.y in bounds.second && it.z in bounds.third
                        && it !in visited && it !in frontier
            }

            neighbors.forEach { n ->
                if (n in keyCoords) {
                    outerSurface++
                }
            }

            frontier.addAll(neighbors.filter { it !in keyCoords })
        }

        return outerSurface
    }

    data class Cube(
        val c: Coord3d,
        var openSides: Int = 6
    )
}