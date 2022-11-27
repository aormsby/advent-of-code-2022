package utils

abstract class Solution {
    private var startTime: Long = 0
    private var activePart = 1

    // print day and title of challenge
    protected fun begin(title: String) {
        println("\n*** $title ***")
        setStartTime()
    }

    // print result of a part's calculation
    protected fun output(descriptor: String, result: Any) {
        println("Part $activePart -> $descriptor = $result")
        activePart++
        printTime()
    }

    // print execution time for a part, reset start time for next part
    private fun printTime() {
        println("-- ${System.currentTimeMillis() - startTime} ms")
        setStartTime()
    }

    // updates start time
    private fun setStartTime() {
        startTime = System.currentTimeMillis()
    }
}