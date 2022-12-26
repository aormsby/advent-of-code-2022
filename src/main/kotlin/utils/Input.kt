package utils

import models.Coord2d
import models.Coord3d
import models.Grid
import java.io.IOException

object Input {
    fun parseAllText(filename: String): String =
        this.javaClass.getResourceAsStream(filename)?.bufferedReader()?.readText()
            ?: throw IOException("read input failed")

    fun parseLines(filename: String): List<String> =
        this.javaClass.getResourceAsStream(filename)?.bufferedReader()?.readLines()
            ?: throw IOException("read input failed")

    inline fun <reified T> parseToListOf(
        filename: String? = null,
        rawData: String? = null,
        delimiter: String = ""
    ): List<T> =
        filename?.let {
            parseLines(filename).map {
                when (T::class) {
                    Int::class -> it.toInt() as T
                    Char::class -> it.single() as T
                    Coord3d::class -> parseToCoord3d(it, delimiter = ",") as T
                    else -> it as T     //String
                }
            }
        } ?: rawData?.let {
            it.split(delimiter).filter { s -> s.isNotBlank() }.map { str ->
                when (T::class) {
                    Int::class -> str.toInt() as T
                    Char::class -> str.single() as T
                    Coord3d::class -> parseToCoord3d(it, delimiter = ",") as T
                    else -> it as T     //String
                }
            }
        } ?: throw IllegalArgumentException("no param provided for parse")

    inline fun <reified T, reified R> parseToPairList(
        filename: String,
        pairDelimiter: String = "",
        groupDelimiter: String = ""
    ): List<Pair<T, R>> =
        parseLines(filename).map {
            val l = it.split(pairDelimiter)
            Pair(
                when (T::class) {
                    Int::class -> l[0].toInt() as T
                    Char::class -> l[0].single() as T
                    Coord2d::class -> parseToCoord2d(l[0], delimiter = groupDelimiter) as T
                    else -> l[0] as T   // String
                },
                when (R::class) {
                    Int::class -> l[1].toInt() as R
                    Char::class -> l[1].single() as R
                    Coord2d::class -> parseToCoord2d(l[1], delimiter = groupDelimiter) as R
                    else -> l[1] as R   // String
                }
            )
        }

    /**
     * @param c current [Coord2d] to parse
     * @return [Coord2d]
     */
    fun parseToCoord2d(c: String, delimiter: String = ""): Coord2d =
        with(c.split(delimiter)) {
            Coord2d(x = this[0].toInt(), y = this[1].toInt())
        }

    /**
     * @param c current [Coord3d] to parse
     * @return [Coord3d]
     */
    fun parseToCoord3d(c: String, delimiter: String = ""): Coord3d =
        with(c.split(delimiter)) {
            Coord3d(x = this[0].toInt(), y = this[1].toInt(), z = this[2].toInt())
        }

    /**
     * Better parser for simple 2d lists
     */
    inline fun <reified T> parseTo2dList(
        filename: String,
        delimiter: String = ""
    ): MutableList<MutableList<T>> =
        parseLines(filename).mapIndexed { i, line ->
            line.split(delimiter).filter { s -> s.isNotBlank() }.mapIndexed { j, str ->
                when (T::class) {
                    Int::class -> str.toInt() as T
                    Char::class -> str.single() as T
                    Coord2d::class -> with(str.split(',')) {
                        Coord2d(first().toInt(), last().toInt()) as T
                    }

                    else -> str as T     //String
                }
            } as MutableList
        } as MutableList

    /**
     * Uses [parseTo2dList] results in [Grid] constructor.
     * Perhaps next year I'll consolidate these funcs.
     */
    inline fun <reified T> parseToGrid(
        filename: String,
        delimiter: String = ""
    ): Grid<T> = Grid(parseTo2dList<T>(filename, delimiter))
}