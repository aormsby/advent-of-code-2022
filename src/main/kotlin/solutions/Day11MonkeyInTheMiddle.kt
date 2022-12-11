package solutions

import utils.Input
import utils.Maths
import utils.Solution

// run only this day
fun main() {
    Day11MonkeyInTheMiddle()
}

class Day11MonkeyInTheMiddle : Solution() {
    init {
        begin("Day 11 - Monkey in the Middle")

        val input = Input.parseLines(filename = "/d11_monkey_decisions.txt")
            .chunked(7)
            .map {
                Monkey(
                    items = it[1].substringAfter(": ").split(", ").map { n -> n.toLong() }.toMutableList(),
                    operation = parseOperation(it[2]),
                    divisor = it[3].substringAfter("by ").toInt(),
                    throwTo = Pair(it[4].substringAfter("monkey ").toInt(), it[5].substringAfter("monkey ").toInt())
                )
            }

        val sol1 = calculateMonkeyBusiness(input.map { it.copy(items = it.items.toMutableList()) })
        output("Level of Monkey Business", sol1)

        val sol2 = calculateMonkeyBusiness(input, worryReduction = false)
        output("Level of Monkey Business When Worrying More", sol2)
    }

    private fun calculateMonkeyBusiness(monkeys: List<Monkey>, worryReduction: Boolean = true): Long {
        var round = 1
        val end = if (worryReduction) 21 else 10_001
        val lcm = monkeys.map { it.divisor }.reduce { acc, n ->
            Maths.lcm(acc, n)
        }

        while (round < end) {
            monkeys.forEach { m ->
                // inspect all at once
                m.inspections += m.items.size

                while (m.items.size > 0) {
                    var item = m.items.removeFirst()

                    // operate
                    item = m.operation(item)
                    if (worryReduction)
                        item /= 3
                    else item %= lcm

                    // throw
                    monkeys[
                        if (m.test(item)) m.throwTo.first
                        else m.throwTo.second
                    ].items.add(item)
                }
            }

            round++
        }

        return monkeys.map { it.inspections }.sorted().takeLast(2).reduce { acc, n -> acc * n }
    }

    // parse the operation line, needed extra work because of the "old" string, blech
    private fun parseOperation(s: String): (Long) -> Long {
        val parts = s.substringAfter("old ")
            .split(" ")

        return when {
            parts[0] == "*" && parts[1] != "old" -> { n -> n * parts[1].toLong() }
            parts[0] == "*" && parts[1] == "old" -> { n -> n * n }
            parts[0] == "+" && parts[1] != "old" -> { n -> n + parts[1].toLong() }
            else -> { n -> n + n }
        }
    }

    data class Monkey(
        val items: MutableList<Long>,
        val operation: (Long) -> Long,
        val divisor: Int,
        val test: (Long) -> Boolean = { n -> n % divisor == 0L },
        val throwTo: Pair<Int, Int>,
        var inspections: Long = 0
    )
}