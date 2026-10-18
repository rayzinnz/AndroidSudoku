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

    var selectedCellIndex by mutableStateOf<Int?>(null)
        private set

    var entryMode by mutableStateOf(EntryMode.SOLVE)
        private set

    var highlightedDigit by mutableStateOf<Int?>(null)
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
        selectedCellIndex = null
        highlightedDigit = null
    }

    fun onCellClicked(index: Int) {
        if (index in 0..80) {
            val cell = cells[index]
            highlightedDigit = if (cell.isPrefilled) cell.prefilledDigit
            else cell.userDigit
            if (!cell.isPrefilled) {
                selectedCellIndex = index
            }
        }
    }

    fun dismissDialog() {
        selectedCellIndex = null
        highlightedDigit = null
    }

    fun setMode(mode: EntryMode) {
        entryMode = mode
    }

    fun onDigitInput(digit: Int) {
        val index = selectedCellIndex ?: return
        val currentCell = cells[index]
        if (currentCell.isPrefilled) return

        val updatedCell = if (entryMode == EntryMode.SOLVE) {
            val newDigit = if (currentCell.userDigit == digit) null else digit
            currentCell.copy(
                userDigit = newDigit,
                placeholders = emptySet()
            )
        } else {
            val newPlaceholders = currentCell.placeholders.toMutableSet().apply {
                if (contains(digit)) remove(digit) else add(digit)
            }
            currentCell.copy(
                userDigit = null,
                placeholders = newPlaceholders
            )
        }

        cells = cells.toMutableList().apply {
            this[index] = updatedCell
        }
    }

    fun onDeleteInput() {
        val index = selectedCellIndex ?: return
        val currentCell = cells[index]
        if (currentCell.isPrefilled) return

        cells = cells.toMutableList().apply {
            this[index] = currentCell.copy(
                userDigit = null,
                placeholders = emptySet()
            )
        }
    }

    fun resetBoard() {
        loadPuzzle()
    }

    fun isDigitCorrect(index: Int, digit: Int): Boolean = solvedPuzzle[index] == digit
}
