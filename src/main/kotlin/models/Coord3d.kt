package models

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Coord3d(
    var x: Int,
    var y: Int,
    var z: Int
) {
    operator fun plus(c: Coord3d) = Coord3d(x = x + c.x, y = y + c.y, z = z + c.z)
    operator fun minus(c: Coord3d) = Coord3d(x = x - c.x, y = y - c.y, z = z - c.z)

    fun opposite(): Coord3d = Coord3d(x * -1, y * -1, z * -1)
    fun diffWith(c: Coord3d): Coord3d = Coord3d(c.x - x, c.y - y, c.z - z)

    fun euclideanDistanceTo(c: Coord3d): Float = sqrt(
        ((x - c.x).toFloat()).pow(2) + ((y - c.y).toFloat()).pow(2) + ((z - c.z).toFloat()).pow(2)
    )

    fun manhattanDistanceTo(c: Coord3d): Int =
        with(diffWith(c)) {
            abs(this.x) + abs(this.y) + abs(this.z)
        }

    fun toInts(): List<Int> = listOf(x, y, z)

    fun pairWith(c: Coord3d) = listOf(
        x to c.x,
        y to c.y,
        z to c.z,
    )

    fun adjacentNeighbors(xLimit: Int = -1, yLimit: Int = -1, zLimit: Int = -1): List<Coord3d> {
        var adjs = listOf(
            Coord3d(x, y + 1, z),
            Coord3d(x, y - 1, z),
            Coord3d(x + 1, y, z),
            Coord3d(x - 1, y, z),
            Coord3d(x, y, z + 1),
            Coord3d(x, y, z - 1),
        )

        if (xLimit > -1)
            adjs = adjs.filter { it.x in 0..xLimit }

        if (yLimit > -1)
            adjs = adjs.filter { it.y in 0..yLimit }

        if (zLimit > -1)
            adjs = adjs.filter { it.z in 0..zLimit }

        return adjs
    }

    fun diagonalNeighbors(xLimit: Int = -1, yLimit: Int = -1, zLimit: Int = -1): List<Coord3d> {
        var diags = listOf(
            Coord3d(x - 1, y - 1, z),
            Coord3d(x - 1, y + 1, z),
            Coord3d(x + 1, y - 1, z),
            Coord3d(x + 1, y + 1, z),
            Coord3d(x + 1, y, z + 1),
            Coord3d(x + 1, y, z - 1),
        )

        if (xLimit > -1)
            diags = diags.filter { it.x in 0..xLimit }

        if (yLimit > -1)
            diags = diags.filter { it.y in 0..yLimit }

        if (zLimit > -1)
            diags = diags.filter { it.z in 0..zLimit }

        return diags
    }

    fun allNeighbors(xLimit: Int = -1, yLimit: Int = -1, zLimit: Int = -1): List<Coord3d> {
        return adjacentNeighbors(xLimit, yLimit) + diagonalNeighbors(xLimit, yLimit)
    }

    override fun toString(): String = "($x, $y, $z)"
}