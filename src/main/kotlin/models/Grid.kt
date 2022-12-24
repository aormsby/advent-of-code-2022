package models

data class Grid<T>(
    val g: MutableList<MutableList<T>>
) {
    /**
     * get single coord
     */
    operator fun get(c: Coord2d): T = g[c.x][c.y]

    /**
     * set single coord
     */
    operator fun set(c: Coord2d, value: T) {
        g[c.x][c.y] = value
    }

    /**
     * set multiple coords at once
     */
    operator fun set(cList: List<Coord2d>, value: T) {
        cList.forEach { c -> g[c.x][c.y] = value }
    }

    /**
     * remove multiple rows from the grid
     */
    fun clearRows(range: IntRange) {
        for (r in range)
            g.removeAt(r)
    }

    fun print() {
        g.forEachIndexed { i, r ->
            print("$i ")
            r.forEach { c ->
                print("$c ")
            }
            println()
        }
        println()
    }

    fun printFlippedOnX() {
        g.reversed().forEachIndexed { i, r ->
            print("${g.size - i - 1} ")
            r.forEach { c ->
                print("$c ")
            }
            println()
        }
        println()
    }
}
