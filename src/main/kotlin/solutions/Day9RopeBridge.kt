package solutions

import models.Coord2d
import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day9RopeBridge()
}

class Day9RopeBridge : Solution() {
    init {
        begin("Day 9 - Rope Bridge")

        val input = Input.parseToPairList<Char, Int>(
            filename = "/d9_rope_motion.txt",
            pairDelimiter = " ",
            groupDelimiter = "\n"
        )

        val sol1 = trackTailPositions(input, knots = MutableList(size = 2) { Coord2d(0, 0) })
        output("2-Knot Tail Trail Count", sol1.size)

        val sol2 = trackTailPositions(input, knots = MutableList(size = 10) { Coord2d(0, 0) })
        output("10-Knot Tail TrailCount", sol2.size)
    }

    // simulate the motions using the coordinates of each knot
    private fun trackTailPositions(input: List<Pair<Char, Int>>, knots: MutableList<Coord2d>): Set<Coord2d> {
        val tailVisited = mutableSetOf<Coord2d>()

        // step through instructions
        input.forEach { instruction ->
            val move = instruction.motion()
            var numSteps = instruction.second

            // while there are steps left in the move...
            while (numSteps > 0) {
                var cur = 0         // knot index
                knots[0] += move    // move only the head knot

                // looping over knots, stop at the last knot
                while (cur < knots.size - 1) {
                    val next = cur + 1

                    // if knots not adjacent, add the 'signs' of the diff (e.g. [4,-2] becomes [1,-1])
                    if (knots[next] != knots[cur] && knots[next] !in knots[cur].allNeighbors()) {
                        knots[next] += (knots[cur] - knots[next]).signs()
                    }

                    // add tail position to set
                    if (next == knots.size - 1)
                        tailVisited.add(knots[next])

                    cur++
                }

                numSteps--
            }
        }

        return tailVisited
    }

    // parse instruction to directional vector
    private fun Pair<Char, Int>.motion(): Coord2d =
        when (first) {
            'R' -> Coord2d(1, 0)
            'L' -> Coord2d(-1, 0)
            'U' -> Coord2d(0, 1)
            'D' -> Coord2d(0, -1)
            else -> throw Exception("something's wrong")
        }
}