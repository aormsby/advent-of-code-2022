package solutions

import models.Coord2d
import models.Grid
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

        val input = Input.parseTo2dList<Coord2d>(filename = "/d14_cave_scan.txt", delimiter = " -> ")
        val cave = buildCave(input)

        val sol1 = simulateSandToResting(cave, hasFloor = false)
        output("Resting Sand", sol1)

        val sol2 = simulateSandToResting(cave, hasFloor = true)
        output("Sand Spout Blocked", sol2 + sol1)
    }

    // system for simulating seeping sand
    private fun simulateSandToResting(cave: Grid<Char>, hasFloor: Boolean): Int {
        val sandSpoutPos = Coord2d(0, cave.g.first().indexOf(sandSpout))
        var grainsDropped = 0
        var infiniteSpill = false

        while (infiniteSpill.not()) {
            val grain = sandSpoutPos.copy()
            var fell = true

            // single grain falling
            while (fell) {
                fell = grain.fallIn(cave)

                //part 1 end
                if (fell && hasFloor.not() && grain.x == cave.g.size - 2) {
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
    private fun Coord2d.fallIn(cave: Grid<Char>): Boolean {
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

    private fun buildCave(scan: List<List<Coord2d>>): Grid<Char> {
        // arbitrary size, position normalizer so I don't have to grow list
        val caveSize = Coord2d(360, 360)
        val caveNorm = Coord2d(0, 500 - ((caveSize.x / 2) + 1))

        // empty cave
        val cave = Grid(g = MutableList(size = caveSize.y) { MutableList(size = caveSize.x) { air } })

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
        var trimSize = cave.g.takeLastWhile { line -> line.filterNot { it == air }.isEmpty() }.size
        while (trimSize > 1) {
            cave.g.removeLast()
            trimSize--
        }

        // add floor
        cave.g.add(MutableList(size = cave.g.last().size - 1) { rock })

        return cave
    }
}
