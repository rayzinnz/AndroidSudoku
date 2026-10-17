package rayzinnz.sudoku

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SudokuGameTest {

    @Test
    fun generatePuzzleNumbers_returnsArraysOfLength81() {
        val (unsolved, solved) = generatePuzzleNumbers()
        assertEquals(81, unsolved.size)
        assertEquals(81, solved.size)
    }

    @Test
    fun generatePuzzleNumbers_unsolvedMatchesExpectedValues() {
        val (unsolved, _) = generatePuzzleNumbers()
        val expectedUnsolved = intArrayOf(
            0,0,0,2,0,9,0,7,5,
            0,0,0,3,4,0,0,9,0,
            4,0,0,5,0,0,0,0,0,
            2,3,0,0,0,0,1,0,6,
            0,0,1,0,0,0,7,0,0,
            5,0,6,0,0,0,0,4,3,
            0,0,0,0,0,6,0,0,8,
            0,8,0,0,5,2,0,0,0,
            6,2,0,9,0,3,0,0,0
        )
        assertArrayEquals(expectedUnsolved, unsolved)
    }

    @Test
    fun generatePuzzleNumbers_solvedMatchesExpectedValues() {
        val (_, solved) = generatePuzzleNumbers()
        val expectedSolved = intArrayOf(
            3,1,8,2,6,9,4,7,5,
            7,5,2,3,4,8,6,9,1,
            4,6,9,5,7,1,3,8,2,
            2,3,7,8,9,4,1,5,6,
            8,4,1,6,3,5,7,2,9,
            5,9,6,1,2,7,8,4,3,
            9,7,5,4,1,6,2,3,8,
            1,8,3,7,5,2,9,6,4,
            6,2,4,9,8,3,5,1,7
        )
        assertArrayEquals(expectedSolved, solved)
    }

    @Test
    fun viewModel_initialStateCorrect() {
        val vm = SudokuViewModel()
        assertEquals(81, vm.cells.size)

        // Check index 3 (prefilled with 2)
        val cell3 = vm.cells[3]
        assertTrue(cell3.isPrefilled)
        assertEquals(2, cell3.prefilledDigit)

        // Check index 0 (unknown/empty at start)
        val cell0 = vm.cells[0]
        assertFalse(cell0.isPrefilled)
        assertEquals(0, cell0.prefilledDigit)
        assertNull(cell0.userDigit)
        assertTrue(cell0.placeholders.isEmpty())
    }

    @Test
    fun viewModel_prefilledCellsCannotBeModified() {
        val vm = SudokuViewModel()
        vm.onCellClicked(3) // Index 3 is prefilled with 2
        assertNull(vm.selectedCellIndex) // Tapping prefilled cell does not select it
    }

    @Test
    fun viewModel_solveDigitInputWorks() {
        val vm = SudokuViewModel()
        vm.onCellClicked(0) // Index 0 is editable
        assertEquals(0, vm.selectedCellIndex)

        vm.setMode(EntryMode.SOLVE)
        vm.onDigitInput(3)

        val updatedCell = vm.cells[0]
        assertEquals(3, updatedCell.userDigit)
        assertTrue(updatedCell.placeholders.isEmpty())
    }

    @Test
    fun viewModel_placeholderInputWorks() {
        val vm = SudokuViewModel()
        vm.onCellClicked(0)
        vm.setMode(EntryMode.PLACEHOLDER)

        vm.onDigitInput(1)
        vm.onDigitInput(8)

        val updatedCell = vm.cells[0]
        assertNull(updatedCell.userDigit)
        assertEquals(setOf(1, 8), updatedCell.placeholders)

        // Toggle candidate 1 off
        vm.onDigitInput(1)
        assertEquals(setOf(8), vm.cells[0].placeholders)
    }

    @Test
    fun viewModel_deleteInputClearsCell() {
        val vm = SudokuViewModel()
        vm.onCellClicked(0)

        // Set digit
        vm.setMode(EntryMode.SOLVE)
        vm.onDigitInput(3)
        assertEquals(3, vm.cells[0].userDigit)

        // Delete
        vm.onDeleteInput()
        assertNull(vm.cells[0].userDigit)
        assertTrue(vm.cells[0].placeholders.isEmpty())
    }

    @Test
    fun viewModel_detectsPuzzleSolved() {
        val vm = SudokuViewModel()
        val (_, solved) = generatePuzzleNumbers()

        // Fill all non-prefilled cells with solution
        for (i in 0 until 81) {
            if (!vm.cells[i].isPrefilled) {
                vm.onCellClicked(i)
                vm.setMode(EntryMode.SOLVE)
                vm.onDigitInput(solved[i])
            }
        }

        assertTrue(vm.isPuzzleSolved)
    }
}
