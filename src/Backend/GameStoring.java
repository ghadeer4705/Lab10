package Backend;

import Backend.Validator.CSVFileReader;
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
    public void saveGameToDifficultyFolder(String difficulty, SudokuBoard board, String fileName) throws IOException {
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

        int[][] grid = CSVFileReader.readFromFile(filepath);
        return new SudokuBoard(grid);
    }

    public SudokuBoard loadCurrentGame(String difficulty) throws NotFoundException {
        File file = new File(CURRENT_GAME_FILE);
        if (!file.exists()) {
            throw new NotFoundException("Current game file not found: " + CURRENT_GAME_FILE);
        }

        int[][] grid = CSVFileReader.readFromFile(CURRENT_GAME_FILE);
        return new SudokuBoard(grid);

    }

    public void SaveCurrentGame(SudokuBoard board) throws IOException {
        saveToCSV(CURRENT_GAME_FILE, board);
    }

    public void deleteGameFromDifficultyFolder(String difficulty, String filename) throws NotFoundException {
        String dir = getDifficultyDirectory(difficulty);
        String filepath = dir + "/" + filename + ".csv";

        File file = new File(filepath);

        if (file.exists()) {
            file.delete();

        }
    }

    public void deleteCurrentGame(String difficulty) throws NotFoundException {
        File currentGame = new File(CURRENT_GAME_FILE);
        File logGame = new File(LOG_FILE);
        if (currentGame.exists()) {
            currentGame.delete();
        }
        if (logGame.exists()) {
            logGame.delete();
        }
    }

    public Catalog catalog() {
        boolean hasCurrentGame = new File(CURRENT_GAME_FILE).exists();
        boolean hasEasyGames = new File(EASY_DIR).listFiles().length > 0;
        boolean hasMediumGames = new File(MEDIUM_DIR).listFiles().length > 0;
        boolean hasHardGames = new File(HARD_DIR).listFiles().length > 0;
        boolean allModesExist = hasEasyGames && hasMediumGames && hasHardGames;
        return new Catalog(hasCurrentGame, allModesExist);
    }

    private boolean hasGamesInDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".csv"));
        return files != null && files.length > 0;
    }

    public String getRandomGameFilePath(String difficulty) throws NotFoundException {
        String dir = getDifficultyDirectory(difficulty);
        File folder = new File(dir);
        File[] files = folder.listFiles((d, name) -> name.toLowerCase().endsWith(".csv"));

        if (files == null || files.length == 0) {
            throw new NotFoundException("No games found in directory: " + dir);
        }

        int randomIndex = (int) (Math.random() * files.length);
        return files[randomIndex].getAbsolutePath();
    }

}
