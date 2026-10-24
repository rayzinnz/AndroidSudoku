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

In this sudoku app, it should save the game state automatically. When the app is opened up, it should restore the in-play game to the same state (if any). So the unsolved game and the solved game are loaded back into memory, any already guessed numbers and placeholders are restored.