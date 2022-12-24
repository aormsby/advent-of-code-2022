package solutions

import models.Coord2d
import models.Grid
import utils.Input
import utils.Solution
import kotlin.math.max

// run only this day
fun main() {
    Day17PyroclasticFlow()
}

class Day17PyroclasticFlow : Solution() {
    init {
        begin("Day 17 - Pyroclastic Flow")

        val input = Input.parseAllText(filename = "/d17_rocks_and_jets.txt")
            .split("\n\n")

        val jets = input.last()
        val rocks = input.dropLast(1).map { it.convertToRock() }

        val sol1 = dropBlocks(rocks, jets, dropLimit = 2022)
        output("Rock Height 2022", sol1)

        val sol2 = dropBlocks(rocks, jets, dropLimit = 1_000_000_000_000)
        output("string 2", sol2)
    }

    private fun dropBlocks(rocks: List<List<Coord2d>>, jets: String, dropLimit: Long): Long {
        val row = List(size = 7) { '.' }
        val gridSize = 7
        val grid = Grid(MutableList(size = gridSize) { row.toMutableList() })

        var rocksDropped = 0
        var rockHeight = 0
        var curRock: List<Coord2d>? = null

        var rockIndex = 0
        var jetIndex = 0
        val heightChangeMap = mutableListOf<Int>()

        // pair = rocksDropped, currentHeight
        val stateMap = mutableMapOf<String, Pair<Int, Int>>()

        while (rocksDropped < dropLimit) {
            // add grid rows if necessary
            if ((grid.g.size - rockHeight) < (gridSize + 1))
                grid.g.addAll(MutableList(size = gridSize) { row.toMutableList() })

            // after last rock was dropped...
            if (curRock == null) {
                // store new start state
                val key = "$jetIndex:$rockIndex"

                if (stateMap.containsKey(key).not())
                    stateMap[key] = Pair(rocksDropped, rockHeight)
                else {  // found repeat, get cycle
                    val origKey = stateMap[key]!!
                    val cycle = (rocksDropped - origKey.first).toLong()

                    // only calculate total and return if match is actually a cycle
                    if ((rocksDropped % cycle) == (dropLimit % cycle)) {
                        val cycleHeight = rockHeight - origKey.second
                        val remainingRocks = dropLimit - rocksDropped
                        val cyclesRemaining = remainingRocks / cycle
                        return rockHeight + (cycleHeight * cyclesRemaining)
                    }
                }

                // position new rock
                curRock = rocks[rockIndex].map {
                    it + Coord2d(rockHeight + 3, 2)
                }
            }

            // push by jet
            jets[jetIndex].push(curRock, grid, gridSize)
            jetIndex = (jetIndex + 1) % jets.length

            // try fall, update info if rock couldn't fall
            if (curRock.tryFall(grid).not()) {
                rocksDropped++
                rockIndex = (rockIndex + 1) % rocks.size

                val droppedHeight = curRock.maxOf { it.x + 1 }
                heightChangeMap.add(droppedHeight - rockHeight)
                rockHeight = max(rockHeight, droppedHeight)

                curRock.forEach { grid[it] = '#' }
                curRock = null
            }

            /*            curRock?.forEach { grid[it] = '@' }
                        grid.printFlippedOnX()
                        grid.g.forEachIndexed { i, r ->
                            r.forEachIndexed { k, c -> if (c == '@') grid.g[i][k] = '.' }
                        }*/
        }

        // fallback if no pattern in simulation
        return rockHeight.toLong()
    }

    private fun String.convertToRock(): List<Coord2d> {
        val lines = split("\n")

        return lines.mapIndexed { x, line ->
            line.mapIndexedNotNull { y, char ->
                if (char == '#') Coord2d(lines.size - 1 - x, y)
                else null
            }
        }.flatten()
    }

    private fun Char.push(rock: List<Coord2d>, grid: Grid<Char>, gWidth: Int) {
        val direction = when (this) {
            '<' -> -1
            else -> 1
        }

        val tempR = rock.map { it.copy(y = it.y + direction) }

        if (tempR.any { it.y < 0 || it.y == gWidth || grid[it] == '#' })
            return
        else {
            rock.forEachIndexed { i, r ->
                r.y = tempR[i].y
            }
        }
    }

    private fun List<Coord2d>.tryFall(grid: Grid<Char>): Boolean {
        return if (any { it.x - 1 < 0 || grid.g[it.x - 1][it.y] == '#' })
            false
        else {
            forEach { c ->
                c.x -= 1
            }
            true
        }
    }
}
