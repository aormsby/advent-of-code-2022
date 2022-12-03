package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day3RucksackReorganization()
}

class Day3RucksackReorganization : Solution() {
    private val a = 'a'
    private val A = 'A'

    init {
        begin("Day 3 - Rucksack eorganization")

        val input = Input.parseLines("/d3_rucksack_items.txt")

        val sol1 = getSumMismatchedPriorities(input)
        output("Mismatched Item Priority Sum", sol1)

        val sol2 = getSumBadgePriorities(input)
        output("Badge Item Priority Sum", sol2)
    }

    private fun getSumMismatchedPriorities(input: List<String>): Int =
        input.fold(initial = 0) { acc, str ->
            val mid = str.length / 2
            val halves = Pair(str.substring(0, mid), str.substring(mid))
            val match = halves.first
                .dropWhile { it !in halves.second }
                .take(1).single()
            acc + match.priority()
        }

    private fun getSumBadgePriorities(input: List<String>): Int =
        input.chunked(3).fold(initial = 0) { acc, group ->
            val match = group[0]
                .dropWhile { it !in group[1] || it !in group[2] }
                .take(1).single()
            acc + match.priority()
        }

    private fun Char.priority(): Int =
        if (this >= a) code - a.code + 1    // is lowercase
        else code - A.code + 26 + 1
}