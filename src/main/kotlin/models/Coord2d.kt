package models

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Coord2d(
    var x: Int,
    var y: Int
) {
    operator fun plus(c: Coord2d) = Coord2d(x = x + c.x, y = y + c.y)
    operator fun minus(c: Coord2d) = Coord2d(x = x - c.x, y = y - c.y)

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

    fun distanceTo(c: Coord2d): Float = sqrt(
        ((x - c.x).toFloat()).pow(2) + ((y - c.y).toFloat()).pow(2)
    )

    fun manhattanDistanceTo(c: Coord2d): Int =
        with(diffWith(c)) {
            abs(this.x) + abs(this.y)
        }

    fun diffWith(c: Coord2d): Coord2d = Coord2d(c.x - x, c.y - y)
    fun opposite(): Coord2d = Coord2d(x * -1, y * -1)

    override fun toString(): String = "($x, $y)"
}