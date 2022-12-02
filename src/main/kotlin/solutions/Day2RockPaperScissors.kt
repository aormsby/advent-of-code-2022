package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day2RockPaperScissors()
}

class Day2RockPaperScissors : Solution() {
    private val diff = 'X' - 'A'
    private val win = 'w'
    private val loss = 'l'
    private val draw = 'd'

    private val points = mapOf(
        'A' to 1, 'B' to 2, 'C' to 3,
        'X' to 1, 'Y' to 2, 'Z' to 3,
        loss to 0, draw to 3, win to 6,
    )

    init {
        begin("Day 2 - Rock Paper Scissors")

        val gamePairs = Input.parseToPairList<Char, Char>(
            filename = "/d2_strategy_guide.txt",
            pairDelimiter = " ",
            groupDelimiter = "\n"
        )

        val sol1 = calculateTotalScore1(gamePairs)
        output("Total Score 1", sol1)

        val sol2 = calculateTotalScore2(gamePairs)
        output("Total Score 2", sol2)
    }

    // subtract their char from mine, normalize with 'diff', convert to wins/losses, sum points
    private fun calculateTotalScore1(input: List<Pair<Char, Char>>): Int =
        input.fold(initial = 0) { acc, pair ->
            val resultPoints = when (pair.second - pair.first - diff) {
                0 -> points[draw]!!
                1, -2 -> points[win]!!
                else -> points[loss]!!
            }

            acc + points[pair.second]!! + resultPoints
        }


    // check result, convert to result to points of the correct selection, sum points
    private fun calculateTotalScore2(input: List<Pair<Char, Char>>): Int =
        input.fold(initial = 0) { acc, pair ->
            val result = when (points[pair.second]) {
                3 -> win
                2 -> draw
                else -> loss
            }

            val selectionPoints = when (result) {
                win -> points[pair.first + 1] ?: points[pair.first - 2]!!
                loss -> points[pair.first - 1] ?: points[pair.first + 2]!!
                else -> points[pair.first]!!
            }

            acc + points[result]!! + selectionPoints
        }
}