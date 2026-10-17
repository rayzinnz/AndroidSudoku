package rayzinnz.sudoku

/**
 * Generates the puzzle numbers for the Sudoku game.
 *
 * @return A [Pair] containing:
 *   - first: IntArray of length 81 representing the unsolved puzzle (0 means unknown digit).
 *   - second: IntArray of length 81 representing the solved puzzle solution.
 */
fun generatePuzzleNumbers(): Pair<IntArray, IntArray> {
    val unsolvedPuzzle = intArrayOf(
        0,0,0,2,0,9,0,7,5,
        0,0,0,3,4,0,0,9,0,
        4,0,0,5,0,0,0,0,0,
        2,3,0,0,0,0,1,0,6,
        0,0,1,0,0,0,7,0,0,
        5,0,6,0,0,0,0,4,3,
        0,0,0,0,0,6,0,0,8,
        0,8,0,0,5,2,0,0,0,
        6,2,0,9,0,3,0,0,0,
    )

    val solvedPuzzle = intArrayOf(
        3,1,8,2,6,9,4,7,5,
        7,5,2,3,4,8,6,9,1,
        4,6,9,5,7,1,3,8,2,
        2,3,7,8,9,4,1,5,6,
        8,4,1,6,3,5,7,2,9,
        5,9,6,1,2,7,8,4,3,
        9,7,5,4,1,6,2,3,8,
        1,8,3,7,5,2,9,6,4,
        6,2,4,9,8,3,5,1,7,
    )

    return Pair(unsolvedPuzzle, solvedPuzzle)
}
