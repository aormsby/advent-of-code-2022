package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day10CathodeRayTube()
}

class Day10CathodeRayTube : Solution() {
    init {
        begin("Day 10 - Cathode-Ray Tube")

        val input = Input.parseLines(filename = "/d10_cpu_instructions.txt")
        val sols = calculateSignalAndRender(input)

        output("Sum Signal Strengths", sols.first.sum())
        outputGrid("CRT Image", sols.second)
    }

    private fun calculateSignalAndRender(input: List<String>): Pair<List<Int>, List<List<String>>> {
        var cycle = 1
        var x = 1

        val signalAndScreen = Pair(
            mutableListOf<Int>(),       // recorded signals
            List(size = 6) { _ ->       // rendered screen
                MutableList(40) { "  " }
            }
        )

        fun step() {
            cycle++
        }

        fun alterX(n: Int) {
            x += n
        }

        fun checkSignal() {
            if ((cycle + 20) % 40 == 0) {
                signalAndScreen.first.add(cycle * x)
            }
        }

        fun draw() {
            if (((cycle - 1) % 40) in (x - 1)..(x + 1))
                signalAndScreen.second[(cycle - 1) / 40][(cycle - 1) % 40] = "W "
        }

        input.forEach { command ->
            when (command) {
                "noop" -> {
                    draw()
                    step()
                    checkSignal()
                }

                else -> {
                    draw()
                    step()
                    checkSignal()
                    draw()
                    alterX(command.substringAfter("addx ").toInt())
                    step()
                    checkSignal()
                }
            }
        }

        return signalAndScreen
    }
}