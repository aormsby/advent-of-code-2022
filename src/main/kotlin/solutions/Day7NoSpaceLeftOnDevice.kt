package solutions

import utils.Input
import utils.Solution
import java.util.*

// run only this day
fun main() {
    Day7NoSpaceLeftOnDevice()
}

class Day7NoSpaceLeftOnDevice : Solution() {
    init {
        begin("Day 7 - No Space Left On Device")

        val input = Input.parseLines(filename = "/d7_commands_output.txt")
        val stack = Stack<String>().apply { addAll(input.reversed()) }
        val dirMap = getDirMap(stack)
        val recursiveDirMap = getSumOfSmallDirectories(dirMap)

        val sol1 = recursiveDirMap.values.filter { it <= 100000L }.sum()
        output("Sum of Small Directory Sizes", sol1)

        val sol2 = findSmallestDirToDelete(recursiveDirMap)
        output("Smallest Directory To Delete", sol2)
    }

    // map out the directories with direct file sizes > 0
    private fun getDirMap(stack: Stack<String>): Map<String, Int> {
        val dirMap = mutableMapOf<String, Int>()
        val currentDir = StringBuilder()

        while (stack.size > 0) {
            val command = stack.pop().removePrefix("$ ")

            if (command.first() == 'c') {   // cd
                when (val newDir = command.split(" ").last()) {
                    "/" -> currentDir.clear().append("/")
                    ".." -> {
                        val lastDirInd = currentDir.dropLast(1).lastIndexOf('/') + 1
                        currentDir.delete(lastDirInd, currentDir.length)
                    }

                    else -> currentDir.append("$newDir/")
                }
            } else {    // ls
                val files = stack.popWhile { !it.startsWith('$') }

                dirMap[currentDir.toString()] = files
                    .filter { it.startsWith("d").not() }
                    .fold(initial = 0) { acc, str -> acc + str.split(" ").first().toInt() }
            }
        }

        return dirMap
    }

    // add sizes from directory paths and subdirectories
    private fun getSumOfSmallDirectories(dirMap: Map<String, Int>): Map<String, Int> {
        val recursiveSizeMap = mutableMapOf<String, Int>()

        dirMap.entries.forEach { entry ->
            var dir = entry.key

            while (dir.isNotEmpty()) {
                dir = dir.dropLast(1)
                val key = dir.ifBlank { "/" }    // prevents empty key

                recursiveSizeMap.merge(key, entry.value) { a, b -> a + b }
                dir = dir.dropLastWhile { it != '/' }
            }
        }

        return recursiveSizeMap
    }

    // compare sizes to find the single smallest directory to delete that provides enough disk space
    private fun findSmallestDirToDelete(dirs: Map<String, Int>): Int {
        val totalDiskSpace = 70000000
        val neededDiskSpace = 30000000
        val usedDiskSpace = dirs["/"]!!
        val targetFreeSpace = neededDiskSpace - (totalDiskSpace - usedDiskSpace)

        return dirs.values.groupBy { it > targetFreeSpace }[true]!!.min()
    }

    // like takeWhile(), but it pops.
    private fun Stack<String>.popWhile(predicate: (String) -> Boolean): List<String> {
        val popped = mutableListOf<String>()

        while (isNotEmpty() && predicate(peek())) {
            popped.add(pop())
        }

        return popped
    }
}