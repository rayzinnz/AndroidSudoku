package rayzinnz.sudoku

/**
 * State representing a single square in the 9x9 Sudoku grid.
 */
data class SudokuCellState(
    val index: Int,
    val row: Int,
    val col: Int,
    val prefilledDigit: Int = 0,
    val userDigit: Int? = null,
    val placeholders: Set<Int> = emptySet(),
) {
    /**
     * Whether this cell was given as part of the initial puzzle (cannot be modified).
     */
    val isPrefilled: Boolean get() = prefilledDigit != 0
}
