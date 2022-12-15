package solutions

import utils.Input
import utils.Solution
import java.util.*

// run only this day
fun main() {
    Day13DistressSignal()
}

class Day13DistressSignal : Solution() {
    private val ten = ':'

    init {
        begin("Day 13 - Distress Signal")

        // remove blank lines, replace 10s with colon -> next highest ASCII code after 9
        val input = Input.parseLines(filename = "/d13_packets.txt")
            .mapNotNull {
                if (it.isBlank()) null
                else it.replace("10", ten.toString())
            }

        // chunking and pairing for part 1 only
        val pairs = input.chunked(2)
            .map {
                Pair(it.first(), it.last())
            }

        val sol1 = getCorrectlyOrderedPairs(pairs)
        output("Sum of Correctly Ordered Pair Indices", sol1)

        val sol2 = findDecoderKey(input)
        output("Decoder Key", sol2)
    }

    // check each packet pair and aggregate the indices of correctly ordered pairs
    private fun getCorrectlyOrderedPairs(
        packets: List<Pair<String, String>>
    ): Int =
        packets.foldIndexed(initial = 0) { i, acc, p ->
            acc + checkPackets(p, i + 1)    // +1 since lists start at 0
        }

    // create stacks of strings (reversed for popping) and make comparisons
    private fun checkPackets(packet: Pair<String, String>, index: Int): Int {
        val left = Stack<Char>().apply { addAll(packet.first.map { it }.reversed()) }
        val right = Stack<Char>().apply { addAll(packet.second.map { it }.reversed()) }

        while (left.isNotEmpty() && right.isNotEmpty()) {
            val lChar = left.pop()
            val rChar = right.pop()

            when {
                // don't care if same, next
                lChar == rChar -> continue

                // when only one side is a comma,
                // push the other side's current char to its stack to replay,
                // move to next
                lChar == ',' -> {
                    right.push(rChar)
                    continue
                }

                rChar == ',' -> {
                    left.push(lChar)
                    continue
                }

                // when only one side is a digit (or :) compared to a list,
                // push this side's current char to its stack and an end bracket to simulate list,
                // move to next
                // (this treats the current char as an open bracket)
                (lChar.isDigit() || lChar == ten) && rChar == '[' -> {
                    left.addAll(listOf(']', lChar))
                    continue
                }

                (rChar.isDigit() || rChar == ten) && lChar == '[' -> {
                    right.addAll(listOf(']', rChar))
                    continue
                }

                // direct comparisons...
                // if left list ends first, break and return current index
                // else right list is short, return 0 (not current index)
                lChar == ']' -> break
                rChar == ']' -> return 0

                // if left greater, return 0 (not current index),
                // else break and return current index
                lChar > rChar -> return 0
                lChar < rChar -> break
            }
        }

        return index
    }

    // add the new packets, sort, find, multiply
    private fun findDecoderKey(input: List<String>): Int {
        val two = "[[2]]"
        val six = "[[6]]"

        return with(
            (input + listOf(two, six))
                .sortedWith(PacketSorter())
        ) {
            (indexOf(two) + 1) * (indexOf(six) + 1)
        }
    }

    // quick sorter using packet checker from part 1!
    private inner class PacketSorter : Comparator<String> {
        override fun compare(s1: String, s2: String): Int =
            if (checkPackets(Pair(s1, s2), 1) == 1) -1
            else 1
    }
}