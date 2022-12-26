package models

data class Grid3d<T>(
    val g: MutableList<MutableList<MutableList<T>>>
) {
    /**
     * get single coord
     */
    operator fun get(c: Coord3d): T = g[c.x][c.y][c.z]

    /**
     * set single coord
     */
    operator fun set(c: Coord3d, value: T) {
        g[c.x][c.y][c.z] = value
    }

    /**
     * set multiple coords at once
     */
    operator fun set(cList: List<Coord3d>, value: T) {
        cList.forEach { c -> g[c.x][c.y][c.z] = value }
    }
}
