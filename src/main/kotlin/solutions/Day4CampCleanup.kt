package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day4CampCleanup()
}

class Day4CampCleanup : Solution() {
    init {
        begin("Day 4 - Camp Cleanup")

        val input = Input.parseLines(filename = "/d4_section_assignments.txt")
            .map { it.split(',', '-') }
            .map { Pair(IntRange(it[0].toInt(), it[1].toInt()), IntRange(it[2].toInt(), it[3].toInt())) }

        val sol1 = findFullOverlaps(input)
        output("Fully Overlapping Ranges", sol1)

        val sol2 = findPartialOverlaps(input)
        output("Partially Overlapping Ranges", sol2)
    }

    private fun findFullOverlaps(input: List<Pair<IntRange, IntRange>>): Int =
        input.fold(initial = 0) { acc, pair ->
            when {
                (pair.first subtract pair.second).isEmpty() || (pair.second subtract pair.first).isEmpty() -> acc + 1
                else -> acc
            }
        }

    private fun findPartialOverlaps(input: List<Pair<IntRange, IntRange>>): Int =
        input.fold(initial = 0) { acc, pair ->
            when {
                (pair.first intersect pair.second).isNotEmpty() -> acc + 1
                else -> acc
            }
        }
}