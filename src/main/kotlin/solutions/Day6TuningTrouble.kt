package solutions

import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day6TuningTrouble()
}

class Day6TuningTrouble : Solution() {
    init {
        begin("Day 6 - Tuning Trouble")

        val input = Input.parseAllText(filename = "/d6_datastream.txt")
        val dataStream = ArrayDeque<Char>(input.map { it })

        val sol1 = getPacketStart(dataStream)
        output("Packet Start Index", sol1)

        // start sol2 with sol1 packet start position
        val sol2 = getMessageStart(dataStream, sol1 - 4)
        output("Message Start Index", sol2)
    }

    // step through char queue until the first 4 are unique, return end index of packet
    private fun getPacketStart(input: ArrayDeque<Char>): Int {
        var bufferIndex = 0
        val packetSize = 4

        while (input.take(packetSize).distinct().size < packetSize) {
            input.removeFirst()
            bufferIndex++
        }

        // buffer stops adding at packet start, add packet size
        return bufferIndex + packetSize
    }

    // same as part 1, looking for first 14 unique
    private fun getMessageStart(input: ArrayDeque<Char>, packetStart: Int): Int {
        var bufferIndex = packetStart
        val messageSize = 14

        while (input.take(messageSize).distinct().size < messageSize) {
            input.removeFirst()
            bufferIndex++
        }

        // buffer stops adding at message start, add message size
        return bufferIndex + messageSize
    }
}