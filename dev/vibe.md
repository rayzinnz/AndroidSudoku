# Sudoku

This is to be a sudoku game app.

Make a function to generate the puzzle numbers. This will return 2 integer arrays of 81 in length each: an unsolved puzzle and the solved puzzle.
I will fill in the logic of the function later, for now hard code the return of these values:
unsolved puzzle (0 means unknown digit):
`intArrayOf(0,0,0,2,0,9,0,7,5,0,0,0,3,4,0,0,9,0,4,0,0,5,0,0,0,0,0,2,3,0,0,0,0,1,0,6,0,0,1,0,0,0,7,0,0,5,0,6,0,0,0,0,4,3,0,0,0,0,0,6,0,0,8,0,8,0,0,5,2,0,0,0,6,2,0,9,0,3,0,0,0)`
solved puzzle: 
`intArrayOf(3,1,8,2,6,9,4,7,5,7,5,2,3,4,8,6,9,1,4,6,9,5,7,1,3,8,2,2,3,7,8,9,4,1,5,6,8,4,1,6,3,5,7,2,9,5,9,6,1,2,7,8,4,3,9,7,5,4,1,6,2,3,8,1,8,3,7,5,2,9,6,4,6,2,4,9,8,3,5,1,7)`

This 81 digit array is a 9 by 9 grid, with column first then row order, so index 0-8 is the first row, 9-17 is the second row, etc...

**UI**

It should display a grid 9x9.
The borders around each 3x3 square should be bolder than the borders around each of the 81 9x9 squares. This is so it is easier to recognise the 3x3 sections that need to have unique digits.
Pre-known digits should be filled in and font color black. Unkown digits should just be an empty square.
Pushing an usolved square (or a previously solved square) should pop up a set of digits 1-9 and a "delete" option to clear the number in the square, and an option to fill in the square as solved with one of the digits, or an option to put the digit as a placeholder. Prefilled squares with known numbers from the start of the puzzle cannot be modified.
Placeholders can be 1 - 9, shown as small digits inside the square. I.e. a 3x3 grid inside each square that can hold placeholder digits 1-9, as they are entered. If a solved digit is entered, the digit will be full size, placeholder digits will disappear, and the font color will be blue.

