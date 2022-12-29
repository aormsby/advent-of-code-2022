package solutions

import models.Coord2d
import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day22MonkeyMap()
}

class Day22MonkeyMap : Solution() {
    init {
        begin("Day 22 - Monkey Map")

        val input = Input.parseLines("/d22_monkey_notes.txt")

        // map coord -> isOpenSpace
        val map = mutableMapOf<Coord2d, Boolean>()
        input.dropLast(2).forEachIndexed { l, line ->
            line.forEachIndexed { c, char ->
                when (char) {
                    '.' -> map[Coord2d(l, c)] = true
                    '#' -> map[Coord2d(l, c)] = false
                }
            }
        }

        val steps = """(\d+)|(\S)""".toRegex()
            .findAll(input.takeLast(1).first(), 0)

        val sol1 = walkPath(map, steps)
        output("Password", sol1.toPassword())

        val sol2 = "2222"
        output("string 2", sol2)
    }

    private fun walkPath(
        map: MutableMap<Coord2d, Boolean>,
        steps: Sequence<MatchResult>
    ): Pair<Coord2d, Direction> {
        val curPos = map.entries
            .filter { it.key.x == 0 }
            .sortedBy { it.key.y }
            .first { it.value }.key

        var curDir = Direction.RIGHT
        val dirVals = Direction.values()

        val iter = steps.iterator()
        while (iter.hasNext()) {
            val next = iter.next().value

            // try treating 'next' as a number, fallback to rotation if it fails
            try {
                curPos.tryStep(curDir, next.toInt(), map)
            } catch (_: Exception) {
                curDir = when (next) {
                    "R" -> dirVals[(curDir.ordinal + 1) % dirVals.size]
                    else -> dirVals[(curDir.ordinal - 1 + dirVals.size) % dirVals.size]
                }
            }
        }

        return (curPos + Coord2d(1, 1)) to curDir
    }

    private fun Coord2d.tryStep(
        cDir: Direction,
        dist: Int,
        map: MutableMap<Coord2d, Boolean>
    ) {
        for (i in 0 until dist) {
            when (cDir) {
                Direction.RIGHT -> {
                    map[this.copy(y = y + 1)]?.let {
                        if (it) y++
                        else return
                    } ?: run {
                        val cWrap = map.entries.filter { it.key.x == x }.minBy { it.key.y }
                        if (cWrap.value) y = cWrap.key.y
                        else return
                    }
                }

                Direction.LEFT -> {
                    map[this.copy(y = y - 1)]?.let {
                        if (it) y--
                        else return
                    } ?: run {
                        val cWrap = map.entries.filter { it.key.x == x }.maxBy { it.key.y }
                        if (cWrap.value) y = cWrap.key.y
                        else return
                    }
                }

                Direction.UP -> {
                    map[this.copy(x = x - 1)]?.let {
                        if (it) x--
                        else return
                    } ?: run {
                        val cWrap = map.entries.filter { it.key.y == y }.maxBy { it.key.x }
                        if (cWrap.value) x = cWrap.key.x
                        else return
                    }
                }

                Direction.DOWN -> {
                    map[this.copy(x = x + 1)]?.let {
                        if (it) x++
                        else return
                    } ?: run {
                        val cWrap = map.entries.filter { it.key.y == y }.minBy { it.key.x }
                        if (cWrap.value) x = cWrap.key.x
                        else return
                    }
                }
            }
        }
    }

    private fun Pair<Coord2d, Direction>.toPassword(): Int =
        (first.x * 1000) + (first.y * 4) + second.ordinal

    private enum class Direction {
        RIGHT, DOWN, LEFT, UP
    }
}
