package solutions

import models.Coord2d
import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day14RegolithReservoir()
}

class Day14RegolithReservoir : Solution() {
    private val rock = '#'
    private val sand = 'o'
    private val sandSpout = '+'
    private val air = ' '

    init {
        begin("Day 14 - Regolith Reservoir")

        // TODO: replace parse with parseToGrid (make)
        val input = Input.parseTo2dList<Coord2d>(filename = "/d14_cave_scan.txt", delimiter = " -> ")
        val cave = buildCave(input)

        val sol1 = simulateSandToResting(cave, hasFloor = false)
        output("Resting Sand", sol1)

        val sol2 = simulateSandToResting(cave, hasFloor = true)
        output("Sand Spout Blocked", sol2 + sol1)
    }

    // TODO: remove mutability for speed?
    private fun simulateSandToResting(cave: MutableList<MutableList<Char>>, hasFloor: Boolean): Int {
        val sandSpoutPos = Coord2d(0, cave.first().indexOf(sandSpout))
        var grainsDropped = 0
        var infiniteSpill = false

        while (infiniteSpill.not()) {
            val grain = sandSpoutPos.copy()
            var fell = true

            // single grain falling
            while (fell) {
                fell = grain.fallIn(cave)

                //part 1 end
                if (fell && hasFloor.not() && grain.x == cave.size - 2) {
                    infiniteSpill = true
                    break
                } else if (!fell) {
                    cave[grain] = sand
                    grainsDropped++

                    // part 2 end
                    if (hasFloor && grain == sandSpoutPos) {
                        infiniteSpill = true
                        break
                    }
                }
            }
        }

        return grainsDropped
    }

    // check 3 points beneath grain and fall into first open, return false if resting
    private fun Coord2d.fallIn(cave: List<List<Char>>): Boolean {
        val beneath = listOf(
            Coord2d(x + 1, y),
            Coord2d(x + 1, y - 1),
            Coord2d(x + 1, y + 1)
        )

        // check positions and fall
        beneath.forEach { c ->
            if (cave[c] == air) {
                x = c.x
                y = c.y
                return true
            }
        }

        // didn't fall
        return false
    }

    private fun buildCave(scan: List<List<Coord2d>>): MutableList<MutableList<Char>> {
        // arbitrary size, position normalizer so I don't have to grow list
        val caveSize = Coord2d(360, 360)
        val caveNorm = Coord2d(0, 500 - ((caveSize.x / 2) + 1))

        // empty cave
        val cave = MutableList(size = caveSize.y) { MutableList(size = caveSize.x) { air } }

        // set sand point
        cave[Coord2d(0, 500) - caveNorm] = sandSpout

        // add rocks
        scan.forEach { line ->
            line.windowed(2).forEach { pair ->
                val start = pair.first().reversed() - caveNorm
                val end = pair.last().reversed() - caveNorm

                cave[start.rangeTo(end)] = rock
            }
        }

        //trim
        var trimSize = cave.takeLastWhile { line -> line.filterNot { it == air }.isEmpty() }.size
        while (trimSize > 1) {
            cave.removeLast()
            trimSize--
        }

        // add floor
        cave.add(MutableList(size = cave.last().size - 1) { rock })

        return cave
    }

    // TODO: make a simple Grid class and add these to it
    operator fun <T> List<List<T>>.get(coord: Coord2d): T = this[coord.x][coord.y]
    operator fun <T> MutableList<MutableList<T>>.set(coord: Coord2d, value: T) {
        this[coord.x][coord.y] = value
    }

    operator fun <T> MutableList<MutableList<T>>.set(coords: List<Coord2d>, value: T) {
        coords.forEach { c ->
            this[c.x][c.y] = value
        }
    }
}
