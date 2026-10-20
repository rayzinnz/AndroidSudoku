package rayzinnz.sudoku

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class EntryMode {
    SOLVE,
    PLACEHOLDER
}

class SudokuViewModel : ViewModel() {

    var cells by mutableStateOf<List<SudokuCellState>>(emptyList())
        private set

    private var solvedPuzzle = IntArray(81)

    var entryMode by mutableStateOf(EntryMode.PLACEHOLDER)
        private set

    var highlightedDigit by mutableStateOf<Int?>(null)
        private set

    var selectedDigit by mutableStateOf(1)
        private set

    val isPuzzleSolved: Boolean
        get() {
            if (cells.size != 81) return false
            for (i in 0 until 81) {
                val current = if (cells[i].isPrefilled) cells[i].prefilledDigit else cells[i].userDigit
                if ((current == null) || (current != solvedPuzzle[i])) return false
            }
            return true
        }

    init {
        loadPuzzle()
    }

    fun loadPuzzle() {
        val (unsolved, solved) = generatePuzzleNumbers()
        solvedPuzzle = solved
        cells = List(81) { index ->
            val row = index / 9
            val col = index % 9
            val valAtStart = unsolved[index]
            SudokuCellState(
                index = index,
                row = row,
                col = col,
                prefilledDigit = valAtStart,
            )
        }
        highlightedDigit = null
    }

    fun onCellClicked(index: Int) {
        if (index in 0..80) {
            val cell = cells[index]
            if (cell.isPrefilled) {
                highlightedDigit = cell.prefilledDigit
                return
            }

            val updatedCell = if (entryMode == EntryMode.SOLVE) {
                val newDigit = if (cell.userDigit == selectedDigit) null else selectedDigit
                cell.copy(
                    userDigit = newDigit,
                    placeholders = emptySet()
                )
            } else {
                val newPlaceholders = cell.placeholders.toMutableSet().apply {
                    if (contains(selectedDigit)) remove(selectedDigit) else add(selectedDigit)
                }
                cell.copy(
                    userDigit = null,
                    placeholders = newPlaceholders
                )
            }

            cells = cells.toMutableList().apply {
                this[index] = updatedCell
            }
            highlightedDigit = null
        }
    }

    fun setMode(mode: EntryMode) {
        entryMode = mode
    }

    fun setSelectedDigit(digit: Int) {
        selectedDigit = digit
    }

    fun resetBoard() {
        loadPuzzle()
    }

    fun isDigitCorrect(index: Int, digit: Int): Boolean = solvedPuzzle[index] == digit
}
