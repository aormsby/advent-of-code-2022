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

    fun distanceTo(c: Coord3d): Float = sqrt(
        ((x - c.x).toFloat()).pow(2) + ((y - c.y).toFloat()).pow(2) + ((z - c.z).toFloat()).pow(2)
    )

    fun manhattanDistanceTo(c: Coord3d): Int =
        with(diffWith(c)) {
            abs(this.x) + abs(this.y) + abs(this.z)
        }

    override fun toString(): String = "($x, $y, $z)"
}