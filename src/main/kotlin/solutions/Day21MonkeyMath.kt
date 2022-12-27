package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day21MonkeyMath()
}

class Day21MonkeyMath : Solution() {
    init {
        begin("Day 21 - Monkey Math")

        val input = Input.parseLines(filename = "/d21_monkey_shouts.txt")
        val numYellers = mutableMapOf<String, Long>()
        val resultYellers = mutableMapOf<String, MutableList<String>>()
        val numYellers2 = mutableMapOf<String, Long>()
        val resultYellers2 = mutableMapOf<String, MutableList<String>>()

        input.forEach {
            val (name, yell) = it.split(": ")
            try {
                val n = yell.toLong()
                numYellers[name] = n
                numYellers2[name] = n
            } catch (e: Exception) {
                resultYellers[name] = yell.split(" ").toMutableList()
                resultYellers2[name] = yell.split(" ").toMutableList()
            }
        }

        val sol1 = findRootNumber(numYellers, resultYellers)
        output("root's Number", sol1)

        val sol2 = findRootNumber2(numYellers2, resultYellers2)
        output("root's Correct Number", sol2)
    }

    private fun findRootNumber(
        numYellers: MutableMap<String, Long>,
        resultYellers: MutableMap<String, MutableList<String>>
    ): Long {
        while (resultYellers.isNotEmpty()) {
            val converted = mutableMapOf<String, Long>()

            numYellers.forEach { ny ->
                val matches = resultYellers.filter { it.value.contains(ny.key) }

                matches.forEach { m ->
                    if (m.value[0] == ny.key)
                        m.value[0] = ny.value.toString()
                    else m.value[2] = ny.value.toString()

                    try {
                        val a = m.value[0].toLong()
                        val b = m.value[2].toLong()
                        val c = when (m.value[1]) {
                            "+" -> a + b
                            "-" -> a - b
                            "/" -> a / b
                            else -> a * b
                        }

                        converted[m.key] = c
                    } catch (_: Exception) {
                    }
                }
            }

            converted.forEach {
                numYellers[it.key] = it.value
                resultYellers.remove(it.key)
            }
        }

        return numYellers["root"]!!
    }

    private fun findRootNumber2(
        numYellers: MutableMap<String, Long>,
        resultYellers: MutableMap<String, MutableList<String>>
    ): Long {

        resultYellers["root"]!![1] = "="
        resultYellers["humn"] = mutableListOf("x")
        numYellers.remove("humn")

        var filtered = resultYellers.filterNot { it.value.contains("humn") }

        while (filtered.isNotEmpty()) {
            val converted = mutableMapOf<String, Long>()

            numYellers.forEach { ny ->
                val matches = resultYellers.filter { it.value.contains(ny.key) }

                matches.forEach { m ->
                    if (m.value[0] == ny.key)
                        m.value[0] = ny.value.toString()
                    else m.value[2] = ny.value.toString()

                    try {
                        val a = m.value[0].toLong()
                        val b = m.value[2].toLong()
                        val c = when (m.value[1]) {
                            "+" -> a + b
                            "-" -> a - b
                            "/" -> a / b
                            else -> a * b
                        }

                        converted[m.key] = c
                    } catch (_: Exception) {
                    }
                }
            }

            converted.forEach {
                numYellers[it.key] = it.value
                resultYellers.remove(it.key)
            }

            val newFilter = resultYellers.filterNot { it.value.contains("humn") }

            if (newFilter == filtered)
                break
            else filtered = newFilter
        }

        val equation = writeEquation(resultYellers["root"]!!, resultYellers, numYellers).dropLast(1).drop(1)
        val humn = findHumn(equation)

        return humn
    }

    private fun writeEquation(
        start: List<String>,
        resultYellers: Map<String, MutableList<String>>,
        numYellers: MutableMap<String, Long>
    ): List<String> {

        val eq = start.toMutableList()

        start.forEach { item ->
            val i = eq.indexOf(item)

            try {
                if (item == "humn")
                    eq[i] = "x"
                else if (item != "+" && item != "-" && item != "*" && item != "/" && item != "=")
                    item.toLong()
            } catch (_: Exception) {
                numYellers[item]?.let {
                    eq[i] = it.toString()
                } ?: run {
                    val sub = writeEquation(resultYellers[item]!!, resultYellers, numYellers)
                    eq.removeAt(i)
                    eq.addAll(i, sub)
                }
            }
        }

        return eq.apply {
            add(0, "(")
            add(")")
        }
    }

    private fun findHumn(eq: List<String>): Long {
        val i = eq.indexOf("=")
        val sLeft = eq.subList(0, i).toMutableList()
        val sRight = eq.subList(i + 1, eq.size).toMutableList()

        var root =
            if ("x" in sLeft) sRight.first().toLong()
            else sLeft.first().toLong()

        val exp = if ("x" in sLeft) sLeft else sRight

        while (exp.size > 1) {
            // remove parentheses
            exp.apply {
                removeFirst()
                removeLast()
            }

            val doFront = exp.first() != "(" && exp.first() != "x"
            if (doFront) {
                val op = exp.take(2)
                when (op.last()) {
                    "+" -> root -= op.first().toLong()
                    "-" -> root = -1 * (root - op.first().toLong())
                    "/" -> root = op.first().toLong() / root
                    else -> root /= op.first().toLong()
                }

                exp.removeFirst()
                exp.removeFirst()
            } else {
                val op = exp.takeLast(2)
                when (op.first()) {
                    "+" -> root -= op.last().toLong()
                    "-" -> root += op.last().toLong()
                    "/" -> root *= op.last().toLong()
                    else -> root /= op.last().toLong()
                }

                exp.removeLast()
                exp.removeLast()
            }
        }

        return root
    }
}