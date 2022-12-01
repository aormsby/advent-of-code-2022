package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day1CalorieCounting()
}

class Day1CalorieCounting : Solution() {
    init {
        begin("Day 1 - Calorie Counting")

        val cumulativeCaloriesList = listCumulativeCalories(
            Input.parseLines(filename = "/d1_calories.txt")
        )

        val sol1 = cumulativeCaloriesList.max()
        output("Highest Calorie Count", sol1)

        val sol2 = cumulativeCaloriesList.sorted().takeLast(3).sum()
        output("Sum Top 3 Calorie Counts", sol2)
    }

    // generate accumulating list of values (reset to 0 for blanks), return max value
    private fun listCumulativeCalories(input: List<String>): List<Int> =
        input.runningFold(initial = 0) { num, str ->
            str.toIntOrNull()?.let {
                num + it
            } ?: 0
        }
}