package utils

import java.util.*

object Collections {
    inline fun <reified T> transposeList(list: List<List<T>>): List<MutableList<T>> {
        val transposed = mutableListOf<MutableList<T>>()

        list.forEach { line ->
            line.forEachIndexed { i, value ->
                if (transposed.size <= i) transposed.add(i, mutableListOf(value))
                else transposed[i].add(value)
            }
        }

        return transposed
    }
}

// like takeWhile(), but it pops.
fun <T> Stack<T>.popWhile(predicate: (T) -> Boolean): List<T> {
    val popped = mutableListOf<T>()

    while (isNotEmpty() && predicate(peek())) {
        popped.add(pop())
    }

    return popped
}

// pops one at a time until you get what you want
fun <T> Stack<T>.popUntil(predicate: (T) -> Boolean): T {
    while (!predicate(peek())) {
        pop()
    }

    return pop()
}