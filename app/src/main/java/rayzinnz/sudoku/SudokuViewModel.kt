package rayzinnz.sudoku

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

enum class EntryMode {
    SOLVE,
    PLACEHOLDER
}

class SudokuViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences =
        application.getSharedPreferences("sudoku_game", Context.MODE_PRIVATE)

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

    val completedDigits: Set<Int>
        get() {
            if (cells.size != 81) return emptySet()
            val correctCounts = IntArray(10)
            for (i in 0 until 81) {
                val current = if (cells[i].isPrefilled) cells[i].prefilledDigit else cells[i].userDigit
                if (current != null && current == solvedPuzzle[i]) {
                    correctCounts[current]++
                }
            }
            return (1..9).filter { correctCounts[it] == 9 }.toSet()
        }

    init {
        if (!restoreState()) {
            loadPuzzle()
        }
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
//        highlightedDigit = null
        selectDigit(1)
    }

    fun onCellClicked(index: Int) {
        if (index in 0..80) {
            val cell = cells[index]
            if (cell.isPrefilled) {
//                highlightedDigit = cell.prefilledDigit
                selectDigit(cell.prefilledDigit)
                return
            }
            if (cell.userDigit != null && highlightedDigit!=cell.userDigit) {
//                highlightedDigit = cell.userDigit
                selectDigit(cell.userDigit)
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
            highlightedDigit = selectedDigit
            saveState()
        }
    }

    fun setMode(mode: EntryMode) {
        entryMode = mode
        saveState()
    }

    fun selectDigit(digit: Int) {
        selectedDigit = digit
        highlightedDigit = digit
        saveState()
    }

    fun resetBoard() {
        loadPuzzle()
    }

    private fun saveState() {
        prefs.edit().apply {
            putString("solved_puzzle", solvedPuzzle.joinToString(","))
            putString("unsolved_puzzle", cells.joinToString(",") { it.prefilledDigit })
            putString("user_digits", cells.joinToString(",") { it.userDigit ?: 0 })
            putString("placeholders", cells.joinToString("|") { it.placeholders.joinToString(",") })
            putString("entry_mode", entryMode.name)
            putInt("selected_digit", selectedDigit)
            putInt("highlighted_digit", highlightedDigit ?: 0)
            apply()
        }
    }

    private fun restoreState(): Boolean {
        val solvedStr = prefs.getString("solved_puzzle", null) ?: return false
        val unsolvedStr = prefs.getString("unsolved_puzzle", null) ?: return false
        val userDigitsStr = prefs.getString("user_digits", null) ?: return false
        val placeholdersStr = prefs.getString("placeholders", null) ?: return false
        val entryModeStr = prefs.getString("entry_mode", null) ?: return false
        val selectedDigitVal = prefs.getInt("selected_digit", -1)
        if (selectedDigitVal < 1) return false
        val highlightedDigitVal = prefs.getInt("highlighted_digit", 0)

        solvedPuzzle = solvedStr.split(",").map { it.toInt() }.toIntArray()
        val unsolved = unsolvedStr.split(",").map { it.toInt() }.toIntArray()
        val userDigits = userDigitsStr.split(",").map { it.toInt() }
        val placeholderGroups = placeholdersStr.split("|")

        cells = List(81) { index ->
            val row = index / 9
            val col = index % 9
            val placeholders = if (placeholderGroups[index].isBlank()) {
                emptySet()
            } else {
                placeholderGroups[index].split(",").map { it.toInt() }.toSet()
            }
            SudokuCellState(
                index = index,
                row = row,
                col = col,
                prefilledDigit = unsolved[index],
                userDigit = if (userDigits[index] == 0) null else userDigits[index],
                placeholders = placeholders,
            )
        }
        entryMode = EntryMode.valueOf(entryModeStr)
        selectedDigit = selectedDigitVal
        highlightedDigit = if (highlightedDigitVal == 0) null else highlightedDigitVal
        return true
    }

    fun isDigitCorrect(index: Int, digit: Int): Boolean = solvedPuzzle[index] == digit
}
