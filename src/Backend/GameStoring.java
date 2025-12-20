package Backend;

import Exceptions.NotFoundException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameStoring {
    private static final String BASE_DIR = "sudoku";
    private static final String EASY_DIR = BASE_DIR + "/easy";
    private static final String MEDIUM_DIR = BASE_DIR + "/medium";
    private static final String HARD_DIR = BASE_DIR + "/hard";
    private static final String CURRENT_GAME_FILE = BASE_DIR + "/current_game";
    private static final String LOG_FILE = CURRENT_GAME_FILE + "/game.log";

    public GameStoring() {
        createFoldersHierarchy();

    }

    //create hierarchy folders
    public void createFoldersHierarchy() {
        try {
            Files.createDirectories(Paths.get(EASY_DIR));
            Files.createDirectories(Paths.get(MEDIUM_DIR));
            Files.createDirectories(Paths.get(HARD_DIR));
            Files.createDirectories(Paths.get(CURRENT_GAME_FILE));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDifficultyDirectory(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return EASY_DIR;
            case "medium":
                return MEDIUM_DIR;
            case "hard":
                return HARD_DIR;
            default:
                throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        }
    }

    public void saveToCSV(String filePath, SudokuBoard board) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(filePath))) {
            int[][] grid = board.getBoard();
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    printWriter.print(grid[row][col]);
                    if (col < 8) {
                        printWriter.print(",");
                    }
                    printWriter.println();
                }

            }
        }
    }

    //save a game to its difficulty folder
    public void saveagameToDifficultyFolder(String difficulty, SudokuBoard board, String fileName) throws IOException {
        String dir = getDifficultyDirectory(difficulty);
        String filePath = dir + "/" + fileName + ".csv";
        saveToCSV(filePath, board);
    }

    public SudokuBoard loadGame(String difficulty, String filename) throws NotFoundException {
        String dir = getDifficultyDirectory(difficulty);
        String filepath = dir + "/" + filename + ".csv";

        File file = new File(filepath);

        if (!file.exists()) {
            throw new NotFoundException("File not found: " + filepath);
        }

        int [][] grid = CSVFileReader.readFromFile(filepath);
        return new SudokuBoard(grid);
    }

    public SudokuBoard loadCurrentGame(String difficulty) throws NotFoundException {


    }

}
