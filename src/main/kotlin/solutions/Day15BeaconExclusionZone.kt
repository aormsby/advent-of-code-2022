package solutions

import models.Coord2d
import utils.Input
import utils.Solution
import java.text.NumberFormat
import kotlin.math.abs

// run only this day
fun main() {
    Day15BeaconExclusionZone()
}

class Day15BeaconExclusionZone : Solution() {
    init {
        begin("Day 15 - Beacon Exclusion Zone")

        val input = Input.parseLines(filename = "/d15_sensors.txt")
            .map { line ->
                val match = """x=(.+), y=(.+):.*x=(.+), y=(.+)""".toRegex()
                    .find(line)!!.groupValues.drop(1)

                // reversed coordinates to match list index order
                val sensor = Coord2d(match[1].toInt(), match[0].toInt())
                val beacon = Coord2d(match[3].toInt(), match[2].toInt())

                Pair(sensor, beacon)
            }

        val beaconList = input.map { it.second }

        val targetRow = 2_000_000
        val beaconTargetY = beaconList.filter { it.x == targetRow }.map { it.y }.toSet()
        val targetRowScanSet = scanRow(targetRow, input).first().toSet()

        val sol1 = targetRowScanSet.subtract(beaconTargetY).size
        output(String.format("Empty Points at Row ${NumberFormat.getNumberInstance().format(targetRow)}"), sol1)

        val sol2 = findDistressBeaconFrequency(input)
        output("Distress Beacon Frequency", sol2)
    }

    // returns a merged IntRange of all spaces in this row scanned by all beacons
    private fun scanRow(curRow: Int, input: List<Pair<Coord2d, Coord2d>>): List<IntRange> {
        val scanIntervals = mutableListOf<IntRange>()

        for ((sensor, beacon) in input) {
            val mhDist = sensor.manhattanDistanceTo(beacon)

            // if the current row being checked was inside the scan
            val rowDist = abs(curRow - sensor.x)

            if (rowDist <= mhDist) {
                val spread = abs(mhDist - rowDist)
                scanIntervals.mergeUpdate((sensor.y - spread)..(sensor.y + spread))
            }
        }
        return scanIntervals
    }

    // runs scanRow() on all rows until the beacon is founr
    private fun findDistressBeaconFrequency(input: List<Pair<Coord2d, Coord2d>>): Long {
        val fourMil = 4_000_000
        (0..fourMil).forEach { y ->
            val result = scanRow(y, input)

            // if the row scan has a gap, that gap is the missing beacon!
            if (result.size > 1) {
                val x = result.first().last + 1
                return (x.toLong() * fourMil) + y
            }
        }

        return -1
    }

    // tries to merge the latest IntRange with the range(s) already scanned in a row, adds range if no merge
    private fun MutableList<IntRange>.mergeUpdate(inRange: IntRange) {

        // find the first range in the list where the incoming interval may overlap an existing one
        val lowIndex = binarySearch { stored ->
            stored.last.compareTo(inRange.first - 1)
        }.let { it shr 31 xor it }


        // find the last range in the list where the incoming interval may overlap an existing one
        val highIndex = this.binarySearch(fromIndex = lowIndex) { stored ->
            stored.first.compareTo(inRange.last + 1)
        }.let { it shr 31 xor it }

        // note - the 'it xor it' is a neat bit shift trick to keep a positive index or convert a negative one to a matching index,
        // great combo with the binary search for merging these intervals

        // if start and end overlaps are different indices, we can merge some intervals
        val mergedRange =
            if (lowIndex < highIndex)
                minOf(this[lowIndex].first, inRange.first)..maxOf(this[highIndex - 1].last, inRange.last)
            else inRange

        // clear any overlapped ranges
        subList(lowIndex, highIndex).clear()

        // add merged range or inRange
        add(index = lowIndex, element = mergedRange)
    }
}