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
}
