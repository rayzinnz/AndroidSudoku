```sh
cd C:\Users\hrag\Sync\AndroidstudioProjects\AndroidSudoku
cd /home/ray/MEGA/Rays/AndroidstudioProjects/AndroidSudoku
aider --chat-mode ask --edit-format diff
aider --edit-format diff
aider --edit-format diff --yes-always
aider --edit-format diff --no-verify-ssl




/add app\src\main\java\rayzinnz\sudoku\PuzzleGenerator.kt

/run ./gradlew :app:assembleDebug
```

====

In this sudoku app, currently pushing a square brings up a popup with "Solve Digit" or "Placeholder" and digits 1-9.
I want this changed. I want the numbers 1-9 to be always visible under the puzzle grid, and a toggle switch to toggle between Solve Digit and Placeholder. One of the digits 1-9 can be selected at any time, and when clicking a solvable square it sets the selected digit in that square. Default digit selected will be 1.