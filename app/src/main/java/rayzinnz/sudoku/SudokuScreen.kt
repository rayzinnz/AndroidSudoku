package rayzinnz.sudoku

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    viewModel: SudokuViewModel
) {
    val cells = viewModel.cells
    val selectedIndex = viewModel.selectedCellIndex
    val entryMode = viewModel.entryMode
    val isSolved = viewModel.isPuzzleSolved
    val highlightedDigit = viewModel.highlightedDigit

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sudoku",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.resetBoard() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Board"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isSolved) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "🎉 Congratulations! Puzzle Solved!",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                Text(
                    text = "Tap any empty or editable square to enter numbers or placeholders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 9x9 Sudoku Board
            SudokuGrid(
                cells = cells,
                selectedIndex = selectedIndex,
                highlightedDigit = highlightedDigit,
                onCellClick = { index -> viewModel.onCellClicked(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Popup Input Dialog when a cell is selected
        if (selectedIndex != null && selectedIndex in cells.indices) {
            val cell = cells[selectedIndex]
            CellInputDialog(
                selectedCell = cell,
                entryMode = entryMode,
                onModeChange = { viewModel.setMode(it) },
                onDigitSelect = { digit -> viewModel.onDigitInput(digit) },
                onDelete = { viewModel.onDeleteInput() },
                onDismiss = { viewModel.dismissDialog() }
            )
        }
    }
}

@Composable
fun SudokuGrid(
    cells: List<SudokuCellState>,
    selectedIndex: Int?,
    highlightedDigit: Int?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color.White)
            .border(BorderStroke(3.dp, Color.Black))
    ) {
        if (cells.size == 81) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (boxRow in 0..2) {
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        for (boxCol in 0..2) {
                            // 3x3 subgrid box with bold border
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(BorderStroke(2.dp, Color.Black))
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    for (cellRow in 0..2) {
                                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                            for (cellCol in 0..2) {
                                                val row = boxRow * 3 + cellRow
                                                val col = boxCol * 3 + cellCol
                                                val index = row * 9 + col
                                                val cellState = cells[index]

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                        .border(BorderStroke(0.5.dp, Color.LightGray))
                                                ) {
                                                    SudokuCell(
                                                        cellState = cellState,
                                                        isSelected = selectedIndex == index,
                                                        highlightedDigit = highlightedDigit,
                                                        onClick = { onCellClick(index) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuCell(
    cellState: SudokuCellState,
    isSelected: Boolean,
    highlightedDigit: Int?,
    onClick: () -> Unit
) {
    val cellDigit = if (cellState.isPrefilled) cellState.prefilledDigit else cellState.userDigit
    val isHighlighted = highlightedDigit != null && cellDigit == highlightedDigit && !isSelected

    val backgroundColor = when {
        isSelected -> Color(0xFFBBDEFB)
        isHighlighted -> Color(0xFFFFF9C4)
        cellState.isPrefilled -> Color(0xFFF5F5F5)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            // Pre-known digit: Black font color
            cellState.isPrefilled -> {
                Text(
                    text = "${cellState.prefilledDigit}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            // Solved user digit: Blue font color
            cellState.userDigit != null -> {
                Text(
                    text = "${cellState.userDigit}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            // Placeholder digits: 3x3 mini grid
            cellState.placeholders.isNotEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (r in 0..2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (c in 0..2) {
                                val digit = r * 3 + c + 1
                                val isPresent = cellState.placeholders.contains(digit)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isPresent) {
                                        Text(
                                            text = "$digit",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1565C0)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellInputDialog(
    selectedCell: SudokuCellState,
    entryMode: EntryMode,
    onModeChange: (EntryMode) -> Unit,
    onDigitSelect: (Int) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Square R${selectedCell.row + 1}, C${selectedCell.col + 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mode selector
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = entryMode == EntryMode.SOLVE,
                        onClick = { onModeChange(EntryMode.SOLVE) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Solve Digit")
                    }
                    SegmentedButton(
                        selected = entryMode == EntryMode.PLACEHOLDER,
                        onClick = { onModeChange(EntryMode.PLACEHOLDER) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Placeholder")
                    }
                }

                Text(
                    text = if (entryMode == EntryMode.SOLVE)
                        "Tap a digit to fill as solved (blue)"
                    else
                        "Tap digits to toggle as placeholders",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 3x3 Keypad 1..9
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (r in 0..2) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (c in 0..2) {
                                val digit = r * 3 + c + 1
                                val isActive = if (entryMode == EntryMode.SOLVE) {
                                    selectedCell.userDigit == digit
                                } else {
                                    selectedCell.placeholders.contains(digit)
                                }

                                Button(
                                    onClick = { onDigitSelect(digit) },
                                    modifier = Modifier.size(52.dp),
                                    colors = if (isActive) {
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        ButtonDefaults.outlinedButtonColors()
                                    },
                                    border = if (!isActive) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "$digit",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Delete Button
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete / Clear Square")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SudokuScreenPreview() {
    MaterialTheme {
        SudokuScreen(viewModel = SudokuViewModel())
    }
}

