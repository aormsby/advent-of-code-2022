package solutions

import utils.Collections
import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day5SupplyStacks()
}

class Day5SupplyStacks : Solution() {
    init {
        begin("Day 5 - Supply Stacks")

        val input = Input.parseLines(filename = "/d5_crane_procedure.txt")
        val stackRows = input.takeWhile { it.isNotEmpty() }.dropLast(1)

        val stacks = getVerticalStacks(stackRows)
        val instructions = getInstructions(input.drop(stackRows.size + 2))

        // list mapped in argument to make a copy and reuse the original stack for part 2
        val sol1 = moveAndListTopCrates(stacks.map { it.toMutableList() }, instructions)
        output("Top Crates CrateMover9000", sol1)

        val sol2 = moveAndListTopCrates(stacks, instructions, isCrateMover9001 = true)
        output("Top Crates CrateMover9001", sol2)
    }

    // return lists of stacks with top crate at end of lists (checked - top first takes same amount of time :)
    private fun getVerticalStacks(input: List<String>): List<MutableList<Char>> {
        val stackRows = input.map { line ->
            // replace blank spaces with 'x'
            line.replace(("""\s{2,}""").toRegex()) { match ->
                val xBlock = StringBuilder()
                for (i in match.range.step(5)) {
                    xBlock.append('x')
                }
                xBlock
                // remove brackets
            }.replace(("""\[(.)\]""").toRegex()) { match ->
                match.groupValues.last()
                // map crates to list of single Chars
            }.replace(" ", "")
                .split("")
                .filter { it.isNotBlank() }
                .map { it.single() }
        }

        // transpose the list from rows to vertical stacks (was read in horizontally)
        return Collections.transposeList(stackRows)
            .map { col ->
                col.filter { it != 'x' }
                    .reversed().toMutableList()
            }
    }

    // convert instructions into index values
    private fun getInstructions(input: List<String>): List<List<Int>> =
        input.map { line ->
            line.split("move ", " from ", " to ")
                .filter { it.isNotBlank() }
                .mapIndexed { i, n ->
                    // subtract index values to match 0-index lists, but amount to move stays same
                    n.toInt() - if (i == 0) 0 else 1
                }
        }

    // perform movement operations and output string of top crates
    private fun moveAndListTopCrates(
        stacks: List<MutableList<Char>>,
        instructions: List<List<Int>>,
        isCrateMover9001: Boolean = false
    ): String {
        instructions.forEach { ins ->
            val numToMove = ins[0]
            val fromStack = stacks[ins[1]]
            val toStack = stacks[ins[2]]

            val taken = fromStack.removeCrates(numToMove)

            if (isCrateMover9001) toStack.addCratesMultiple(taken)
            else toStack.addCratesSingle(taken)
        }

        return stacks.map { it.last() }.joinToString("")
    }

    private fun MutableList<Char>.removeCrates(num: Int): List<Char> {
        // these must be added to other stack
        val taken = takeLast(num)
        var n = num

        while (n > 0) {
            removeLast()
            n--
        }

        return taken
    }

    private fun MutableList<Char>.addCratesSingle(crates: List<Char>) = addAll(crates.reversed())
    private fun MutableList<Char>.addCratesMultiple(crates: List<Char>) = addAll(crates)
}