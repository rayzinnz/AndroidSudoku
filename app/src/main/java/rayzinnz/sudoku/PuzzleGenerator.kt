package rayzinnz.sudoku

import kotlin.random.Random

private val GRID = arrayOf(
    intArrayOf(0, 1, 2, 9, 10, 11, 18, 19, 20),
    intArrayOf(3, 4, 5, 12, 13, 14, 21, 22, 23),
    intArrayOf(6, 7, 8, 15, 16, 17, 24, 25, 26),
    intArrayOf(27, 28, 29, 36, 37, 38, 45, 46, 47),
    intArrayOf(30, 31, 32, 39, 40, 41, 48, 49, 50),
    intArrayOf(33, 34, 35, 42, 43, 44, 51, 52, 53),
    intArrayOf(54, 55, 56, 63, 64, 65, 72, 73, 74),
    intArrayOf(57, 58, 59, 66, 67, 68, 75, 76, 77),
    intArrayOf(60, 61, 62, 69, 70, 71, 78, 79, 80),
)

/**
 * Generates the puzzle numbers for the Sudoku game.
 *
 * @return A [Pair] containing:
 *   - first: IntArray of length 81 representing the unsolved puzzle (0 means unknown digit).
 *   - second: IntArray of length 81 representing the solved puzzle solution.
 */
fun generatePuzzleNumbers(): Pair<IntArray, IntArray> {
    val solvedPuzzle = getCompleteGrid()
    val unsolvedPuzzle = generateSolvablePuzzle(solvedPuzzle)
    return Pair(unsolvedPuzzle, solvedPuzzle)
}

private fun getCompleteGrid(): IntArray {
    val rng = Random.Default
    val nums = IntArray(81)
    var i = 0
    var numTries = 0

    while (true) {
        val r = i / 9
        val possibleOptions = getPossibleOptions(nums, i)

        if (possibleOptions.isEmpty()) {
            for (x in (r * 9) until (r * 9 + 9)) {
                nums[x] = 0
            }
            i = r * 9
        } else {
            nums[i] = possibleOptions[rng.nextInt(possibleOptions.size)]
            i += 1
        }

        if (i == 200) {
            break
        }
        if (i > 80) {
            break
        }

        numTries += 1
        if (numTries == 1000) {
            numTries = 0
            nums.fill(0)
            i = 0
        }
    }

    return nums
}

private fun getPossibleOptions(puzzle: IntArray, i: Int): List<Int> {
    if (puzzle[i] != 0) {
        return listOf(puzzle[i])
    }

    val row = i / 9
    val col = i % 9
    val square = (row / 3) * 3 + (col / 3)
    val used = BooleanArray(10)

    for (j in 0 until 9) {
        used[puzzle[row * 9 + j]] = true
        used[puzzle[col + 9 * j]] = true
        used[puzzle[GRID[square][j]]] = true
    }

    return (1..9).filter { !used[it] }
}

private fun generateSolvablePuzzle(puzzleComplete: IntArray): IntArray {
    val puzzle = puzzleComplete.copyOf()
    val rng = Random.Default
    val idxsToKeep = (0 until 81).toMutableList()
    var iattempt = 0

    while (idxsToKeep.isNotEmpty()) {
        val keepIdx = idxsToKeep[rng.nextInt(idxsToKeep.size)]
        val removedDigit = puzzle[keepIdx]
        puzzle[keepIdx] = 0

        val solved = solvePuzzle(puzzle, null)
        if (!isSolved(solved)) {
            puzzle[keepIdx] = removedDigit
            if (iattempt > 20) {
                break
            }
            iattempt += 1
        } else {
            idxsToKeep.remove(keepIdx)
        }
    }

    return puzzle
}

private fun solvePuzzle(puzzle: IntArray, presolved: IntArray? = null): IntArray {
    val possibles: Array<MutableList<Int>> = Array(81) { mutableListOf<Int>() }
    val solved = puzzle.copyOf()

    while (true) {
        var numSolvedByOnlyOption = 0
        for (i in 0 until 81) {
            if (solved[i] == 0) {
                if (possibles[i].isEmpty()) {
                    possibles[i] = getPossibleOptions(solved, i).toMutableList()
                }

                if (possibles[i].size == 1) {
                    solved[i] = possibles[i][0]
                    numSolvedByOnlyOption++
                    checkPresolved(solved, presolved, i)
                } else {
                    val possibleOptions = getPossibleOptions(solved, i)
                    if (possibleOptions.size < possibles[i].size) {
                        possibles[i] = possibleOptions.toMutableList()
                    }
                }
            } else {
                if (possibles[i].size != 1) {
                    possibles[i] = mutableListOf(solved[i])
                }
            }
        }

        removePossibleMatchesFromLineups(possibles)

        var numSingleOptionInSq = 0
        for (sq in 0 until 9) {
            for (i in GRID[sq]) {
                val possibleOptions = possibles[i]
                if (possibleOptions.size > 1) {
                    for (op in possibleOptions.toList()) {
                        var foundOp = false
                        for (ichk in GRID[sq]) {
                            if (ichk != i && possibles[ichk].contains(op)) {
                                foundOp = true
                                break
                            }
                        }
                        if (!foundOp) {
                            solved[i] = op
                            possibles[i] = mutableListOf(op)
                            numSingleOptionInSq++
                            checkPresolved(solved, presolved, i)
                            break
                        }
                    }
                }
            }
        }

        var numSingleOptionInRow = 0
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                val i = r * 9 + c
                val possibleOptions = possibles[i]
                if (possibleOptions.size > 1) {
                    for (op in possibleOptions.toList()) {
                        var foundOp = false
                        for (cchk in 0 until 9) {
                            val ichk = r * 9 + cchk
                            if (ichk != i && possibles[ichk].contains(op)) {
                                foundOp = true
                                break
                            }
                        }
                        if (!foundOp) {
                            solved[i] = op
                            possibles[i] = mutableListOf(op)
                            numSingleOptionInRow++
                            checkPresolved(solved, presolved, i)
                            break
                        }
                    }
                }
            }
        }

        var numSingleOptionInCol = 0
        for (c in 0 until 9) {
            for (r in 0 until 9) {
                val i = r * 9 + c
                val possibleOptions = possibles[i]
                if (possibleOptions.size > 1) {
                    for (op in possibleOptions.toList()) {
                        var foundOp = false
                        for (rchk in 0 until 9) {
                            val ichk = rchk * 9 + c
                            if (ichk != i && possibles[ichk].contains(op)) {
                                foundOp = true
                                break
                            }
                        }
                        if (!foundOp) {
                            solved[i] = op
                            possibles[i] = mutableListOf(op)
                            numSingleOptionInCol++
                            checkPresolved(solved, presolved, i)
                            break
                        }
                    }
                }
            }
        }

        var numVisiblePairInSq = 0
        for (sq in 0 until 9) {
            var foundMatches = intArrayOf(0, 0)
            var foundIn = intArrayOf(0, 0)

            for (i in GRID[sq]) {
                val possibleOptions = possibles[i]
                if (possibleOptions.size == 2) {
                    for (ichk in GRID[sq]) {
                        if (ichk != i && possibles[ichk] == possibleOptions) {
                            foundIn[0] = i
                            foundIn[1] = ichk
                            foundMatches[0] = possibleOptions[0]
                            foundMatches[1] = possibleOptions[1]
                            break
                        }
                    }
                }
                if (foundMatches[0] != 0) {
                    break
                }
            }

            if (foundMatches[0] != 0) {
                for (iremove in GRID[sq]) {
                    if (iremove != foundIn[0] && iremove != foundIn[1]) {
                        if (possibles[iremove].contains(foundMatches[0]) ||
                            possibles[iremove].contains(foundMatches[1])
                        ) {
                            numVisiblePairInSq++
                            possibles[iremove].remove(foundMatches[0])
                            possibles[iremove].remove(foundMatches[1])
                        }
                    }
                }
            }
        }

        var numVisiblePairInRow = 0
        for (r in 0 until 9) {
            var foundMatches = intArrayOf(0, 0)
            var foundIn = intArrayOf(0, 0)

            for (c in 0 until 9) {
                val i = r * 9 + c
                val possibleOptions = possibles[i]
                if (possibleOptions.size == 2) {
                    for (cchk in 0 until 9) {
                        val ichk = r * 9 + cchk
                        if (ichk != i && possibles[ichk] == possibleOptions) {
                            foundIn[0] = i
                            foundIn[1] = ichk
                            foundMatches[0] = possibleOptions[0]
                            foundMatches[1] = possibleOptions[1]
                            break
                        }
                    }
                }
                if (foundMatches[0] != 0) {
                    break
                }
            }

            if (foundMatches[0] != 0) {
                for (cremove in 0 until 9) {
                    val iremove = r * 9 + cremove
                    if (iremove != foundIn[0] && iremove != foundIn[1]) {
                        if (possibles[iremove].contains(foundMatches[0]) ||
                            possibles[iremove].contains(foundMatches[1])
                        ) {
                            numVisiblePairInRow++
                            possibles[iremove].remove(foundMatches[0])
                            possibles[iremove].remove(foundMatches[1])
                        }
                    }
                }
            }
        }

        var numVisiblePairInCol = 0
        for (c in 0 until 9) {
            var foundMatches = intArrayOf(0, 0)
            var foundIn = intArrayOf(0, 0)

            for (r in 0 until 9) {
                val i = r * 9 + c
                val possibleOptions = possibles[i]
                if (possibleOptions.size == 2) {
                    for (rchk in 0 until 9) {
                        val ichk = rchk * 9 + c
                        if (ichk != i && possibles[ichk] == possibleOptions) {
                            foundIn[0] = i
                            foundIn[1] = ichk
                            foundMatches[0] = possibleOptions[0]
                            foundMatches[1] = possibleOptions[1]
                            break
                        }
                    }
                }
                if (foundMatches[0] != 0) {
                    break
                }
            }

            if (foundMatches[0] != 0) {
                for (rremove in 0 until 9) {
                    val iremove = rremove * 9 + c
                    if (iremove != foundIn[0] && iremove != foundIn[1]) {
                        if (possibles[iremove].contains(foundMatches[0]) ||
                            possibles[iremove].contains(foundMatches[1])
                        ) {
                            numVisiblePairInCol++
                            possibles[iremove].remove(foundMatches[0])
                            possibles[iremove].remove(foundMatches[1])
                        }
                    }
                }
            }
        }

        if (numSolvedByOnlyOption == 0 &&
            numSingleOptionInSq == 0 &&
            numSingleOptionInRow == 0 &&
            numSingleOptionInCol == 0 &&
            numVisiblePairInSq == 0 &&
            numVisiblePairInRow == 0 &&
            numVisiblePairInCol == 0
        ) {
            break
        }
    }

    return solved
}

private fun removePossibleMatchesFromLineups(possibles: Array<MutableList<Int>>) {
    for (i in 0 until 81) {
        val digit = possibles[i].singleOrNull() ?: continue
        val row = i / 9
        val col = i % 9
        val square = (row / 3) * 3 + (col / 3)

        for (j in 0 until 9) {
            val rowIdx = row * 9 + j
            if (rowIdx != i) {
                possibles[rowIdx].remove(digit)
            }

            val colIdx = col + 9 * j
            if (colIdx != i) {
                possibles[colIdx].remove(digit)
            }

            val squareIdx = GRID[square][j]
            if (squareIdx != i) {
                possibles[squareIdx].remove(digit)
            }
        }
    }
}

private fun isSolved(puzzle: IntArray): Boolean {
    return puzzle.none { it == 0 }
}

private fun checkPresolved(solved: IntArray, presolved: IntArray?, i: Int) {
    if (presolved != null && solved[i] != presolved[i]) {
        throw IllegalStateException(
            "${i / 9}:${i % 9} solved to ${solved[i]}, but should be ${presolved[i]}"
        )
    }
}
