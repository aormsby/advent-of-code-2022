package models

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sign

data class Coord2d(
    var x: Int,
    var y: Int
) {
    operator fun plus(c: Coord2d) = Coord2d(x = x + c.x, y = y + c.y)
    operator fun minus(c: Coord2d) = Coord2d(x = x - c.x, y = y - c.y)
    operator fun times(c: Coord2d) = Coord2d(x = x * c.x, y = y * c.y)
    operator fun times(n: Int) = Coord2d(x = x * n, y = y * n)

    operator fun rangeTo(c: Coord2d): List<Coord2d> {
        var cur = this
        val cList = mutableListOf<Coord2d>()
        val diff = diffWith(c)

        while(cur != c) {
            cList.add(cur)
            cur += diff.signs()
        }
        cList += c

        return cList
    }

    fun signs() = Coord2d(x.sign, y.sign)

    fun adjacentNeighbors(xLimit: Int = -1, yLimit: Int = -1): List<Coord2d> {
        var adjs = listOf(
            Coord2d(x, y + 1),
            Coord2d(x, y - 1),
            Coord2d(x + 1, y),
            Coord2d(x - 1, y)
        )

        if (xLimit > -1)
            adjs = adjs.filter { it.x in 0..xLimit }

        if (yLimit > -1)
            adjs = adjs.filter { it.y in 0..yLimit }

        return adjs
    }

    fun diagonalNeighbors(xLimit: Int = -1, yLimit: Int = -1): List<Coord2d> {
        var diags = listOf(
            Coord2d(x - 1, y - 1),
            Coord2d(x - 1, y + 1),
            Coord2d(x + 1, y - 1),
            Coord2d(x + 1, y + 1)
        )

        if (xLimit > -1)
            diags = diags.filter { it.x in 0..xLimit }

        if (yLimit > -1)
            diags = diags.filter { it.y in 0..yLimit }

        return diags
    }

    fun allNeighbors(xLimit: Int = -1, yLimit: Int = -1): List<Coord2d> {
        return adjacentNeighbors(xLimit, yLimit) + diagonalNeighbors(xLimit, yLimit)
    }

    fun euclideanDistanceTo(c: Coord2d): Float = hypot((x - c.x).toFloat(), (y - c.y).toFloat())

    fun manhattanDistanceTo(c: Coord2d): Int =
        with(diffWith(c)) {
            abs(this.x) + abs(this.y)
        }

    fun diffWith(c: Coord2d): Coord2d = Coord2d(c.x - x, c.y - y)
    fun opposite(): Coord2d = Coord2d(x * -1, y * -1)
    fun reversed(): Coord2d = Coord2d(y, x)

    fun stepTowardZeros() {
        x -= x.sign
        y -= y.sign
    }

    override fun toString(): String = "($x, $y)"
}