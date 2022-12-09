package solutions

import models.Coord2d
import utils.Collections
import utils.Input
import utils.Solution

// run only this day
fun main() {
    Day8TreetopTreeHouse()
}

class Day8TreetopTreeHouse : Solution() {
    init {
        begin("Day 8 - Treetop Tree House")

        val rows = Input.parseTo2dList<Int>(filename = "/d8_tree_topo.txt")
        val cols = Collections.transposeList(rows)

        val sol1 = determineVisibleTrees(rows, cols)
        output("Visible Trees", sol1.size)

        val sol2 = determineBestScenicScore(rows, cols, sol1)
        output("Highest Scenic Score", sol2)
    }

    // create 'lanes' from each edge to each tree to check what's visible, return list of visible coords
    private fun determineVisibleTrees(
        rows: List<List<Int>>,
        cols: List<List<Int>>
    ): Set<Coord2d> {
        // start with edges, subtract corners
        val width = rows[0].size
        val height = rows.size
        val vTrees = mutableSetOf<Coord2d>().apply {
            addAll(List(rows[0].size) { i -> Coord2d(0, i) })
            addAll(List(rows[height - 1].size) { i -> Coord2d(height - 1, i) })
            addAll(List(cols[0].size) { i -> Coord2d(i, 0) })
            addAll(List(cols[width - 1].size) { i -> Coord2d(i, width - 1) })
        }

        for (r in 1 until width - 1) {
            for (c in 1 until height - 1)
                if (isVisible(rows[r].take(c + 1)) ||        // from left
                    isVisible(rows[r].drop(c).reversed()) ||    // from right
                    isVisible(cols[c].take(r + 1)) ||        // from top
                    isVisible(cols[c].drop(r).reversed())       // from bottom
                ) vTrees.add(Coord2d(r, c))
        }

        return vTrees
    }

    // each 'lane' passed is from one edge until the tree being checked (tree is always last)
    private fun isVisible(lane: List<Int>): Boolean =
        if (lane.first() == 9) false    // quickest out
        else lane.last() > lane.dropLast(1).max()

    // take all visible trees from part 1, do basically the same checks with different visibility logic
    private fun determineBestScenicScore(
        rows: List<List<Int>>,
        cols: List<List<Int>>,
        vTrees: Set<Coord2d>
    ): Int =
        vTrees.mapNotNull { t ->
            if (t.x == 0 || t.y == 0 || t.x == rows.size - 1 || t.y == rows[0].size - 1) null
            else treesVisible(rows[t.x].take(t.y + 1).reversed()) * treesVisible(rows[t.x].drop(t.y)) *
                    treesVisible(cols[t.y].take(t.x + 1).reversed()) * treesVisible(cols[t.y].drop(t.x))
        }.max()


    // each 'lane' passed is from the tree until an edge (tree is always first),
    // end conditional accounts for if a blocker was found or if the edge was hit
    private fun treesVisible(lane: List<Int>): Int {
        val t = lane.drop(1).takeWhile { it < lane.first() }.size
        return if (t == lane.size - 1) t else t + 1
    }
}