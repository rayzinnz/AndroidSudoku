package rayzinnz.sudoku

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    viewModel: SudokuViewModel
) {
    val cells = viewModel.cells
    val entryMode = viewModel.entryMode
    val isSolved = viewModel.isPuzzleSolved
    val highlightedDigit = viewModel.highlightedDigit
    val selectedDigit = viewModel.selectedDigit
    val completedDigits = viewModel.completedDigits

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
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 9x9 Sudoku Board
            SudokuGrid(
                cells = cells,
                highlightedDigit = highlightedDigit,
                onCellClick = { index -> viewModel.onCellClicked(index) },
                isDigitCorrect = { index, digit -> viewModel.isDigitCorrect(index, digit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Mode toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Solve Digit",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = entryMode == EntryMode.PLACEHOLDER,
                    onCheckedChange = { viewModel.setMode(if (it) EntryMode.PLACEHOLDER else EntryMode.SOLVE) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Placeholder",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Number pad 1-9
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (r in 0..2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (c in 0..2) {
                            val digit = r * 3 + c + 1
                            val isSelected = selectedDigit == digit
                            val isCompleted = completedDigits.contains(digit)
                            Button(
                                onClick = { viewModel.selectDigit(digit) },
                                modifier = Modifier.size(52.dp),
                                colors = when {
                                    isCompleted -> ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF800000)
                                    )
                                    isSelected -> ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                    else -> ButtonDefaults.outlinedButtonColors()
                                },
                                border = if (!isCompleted && !isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "$digit",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) Color.White else Color.Unspecified
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuGrid(
    cells: List<SudokuCellState>,
    highlightedDigit: Int?,
    onCellClick: (Int) -> Unit,
    isDigitCorrect: (Int, Int) -> Boolean,
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
                                                        highlightedDigit = highlightedDigit,
                                                        isDigitCorrect = isDigitCorrect,
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
    highlightedDigit: Int?,
    isDigitCorrect: (Int, Int) -> Boolean,
    onClick: () -> Unit
) { 
    val cellDigit = if (cellState.isPrefilled) cellState.prefilledDigit else cellState.userDigit
    val isHighlighted = highlightedDigit != null && cellDigit == highlightedDigit

    val backgroundColor = when {
        isHighlighted -> Color(0xFFFFF9C4)
        cellState.isPrefilled -> Color(0xFFC9DAFF)
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
            // Solved user digit: Blue if correct, Red if incorrect
            cellState.userDigit != null -> {
                Text(
                    text = "${cellState.userDigit}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDigitCorrect(cellState.index, cellState.userDigit!!))
                        Color(0xFF1976D2)
                    else
                        Color(0xFFD32F2F)
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


