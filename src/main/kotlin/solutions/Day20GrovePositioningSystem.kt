package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day20GrovePositioningSystem()
}

class Day20GrovePositioningSystem : Solution() {
    init {
        begin("Day 20 - Grove Positioning System")

        val input = Input.parseLines(filename = "/d20_encrypted_file.txt")
            .mapIndexed { i, n -> Pair(i, n.toLong()) }

        val sol1 = findGroveCoordinateSum(input)
        output("Sum of Grove Coordinates", sol1)

        val sol2 = findGroveCoordinateSum(input.map { Pair(it.first, it.second * 811_589_153) }, cycles = 10)
        output("Sum of Decrypted Grove Coordinates", sol2)
    }

    private fun findGroveCoordinateSum(input: List<Pair<Int, Long>>, cycles: Int = 1): Long {
        val switchList = input.toMutableList()
        val size = switchList.size

        for( c in 0 until cycles) {
            input.forEachIndexed { i, pair ->
                val oldI = switchList.indexOf(switchList.find { it.first == i })
                var newI = (oldI + pair.second) % (size - 1)

                if (newI <= 0) {
                    newI += size - 1
                }

                switchList.removeAt(oldI)
                switchList.add(newI.toInt(), pair)
            }
        }

        val z = switchList.indexOf(switchList.find { it.second == 0L })
        val a = switchList[(z + 1000) % size].second
        val b = switchList[(z + 2000) % size].second
        val c = switchList[(z + 3000) % size].second

        return a + b + c
    }
}