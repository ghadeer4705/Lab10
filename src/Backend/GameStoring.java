package Backend;

import Backend.Validator.CSVFileReader;
import Exceptions.NotFoundException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

//for storing and loading games
public class GameStoring {
    //singleton instance
   // private static final GameStoring instance = new GameStoring();
    private static final String BASE_DIR = "sudoku";
    private static final String EASY_DIR = BASE_DIR + "/easy";
    private static final String MEDIUM_DIR = BASE_DIR + "/medium";
    private static final String HARD_DIR = BASE_DIR + "/hard";
    private static final String CURRENT_GAME_DIR = BASE_DIR + "/current_game";
    private static final String CURRENT_GAME_FILE = CURRENT_GAME_DIR + "/game.csv";
    private static final String LOG_FILE = CURRENT_GAME_DIR + "/game.log";

    public GameStoring() {
        createFolders();
    }

 /*   public static GameStoring getInstance() {
        return instance;
    }*/

    //create hierarchy folders
    public void createFolders() {
        try {
            Files.createDirectories(Paths.get(EASY_DIR));
            Files.createDirectories(Paths.get(MEDIUM_DIR));
            Files.createDirectories(Paths.get(HARD_DIR));
            Files.createDirectories(Paths.get(CURRENT_GAME_DIR));

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
    //save a gameboard to csv file
    public void saveToCSV(String filePath, SudokuBoard board) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(filePath))) {
            int[][] grid = board.getBoard();
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    printWriter.print(grid[row][col]);
                    if (col < 8) printWriter.print(",");
                }
                    printWriter.println();
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

    public SudokuBoard loadCurrentGame() throws NotFoundException {
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

    /*public void deleteGameFromDifficultyFolder(String difficulty, String filename) throws NotFoundException {
        String dir = getDifficultyDirectory(difficulty);
        String filepath = dir + "/" + filename + ".csv";

        File file = new File(filepath);

        if (file.exists()) {
            file.delete();

        }
    }*/
    public void deleteGameFromDifficultyFolder(String difficulty, String filename) throws NotFoundException {
        // Handle null inputs gracefully
        if (difficulty == null || filename == null) {
            return; // Just return, don't throw exception
        }

        try {
            String dir = getDifficultyDirectory(difficulty);
            String filepath = dir + "/" + filename + ".csv";

            File file = new File(filepath);

            if (file.exists()) {
                file.delete();
            }
        } catch (IllegalArgumentException e) {
            // Invalid difficulty, just ignore
            return;
        }
    }

    public void deleteCurrentGame() throws NotFoundException {
       new File(CURRENT_GAME_FILE).delete();
       new  File(LOG_FILE).delete();
    }
    public void deleteCompletedGame(int[][] board) throws IOException {
        // Try to find and delete the game from all difficulty folders
        String[] difficulties = {"easy",
                "medium", "hard"};

        for (String difficulty : difficulties) {
            try {
                String dir = getDifficultyDirectory(difficulty);
                File folder = new File(dir);
                File[] files = folder.listFiles((d, name) -> name.endsWith(".csv"));

                if (files != null) {
                    for (File file : files) {
                        try {
                            // Read the file and compare with current board
                            int[][] fileBoard = CSVFileReader.readFromFile(file.getAbsolutePath());
                            // Compare only original cells
                            if (boardsMatchOriginal(fileBoard, board)) {
                                file.delete();
                                System.out.println("Deleted game from " + difficulty + " folder: " + file.getName());
                                return;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                // Skip this difficulty if error
                continue;
            }
        }
    }

    // Helper method to check if boards match (comparing original cells only)
    private boolean boardsMatchOriginal(int[][] originalGame, int[][] completedBoard) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // If original game had a value (not 0), it must match completed board
                if (originalGame[i][j] != 0) {
                    if (originalGame[i][j] != completedBoard[i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Catalog getCatalog() {
        boolean hasCurrentGame = new File(CURRENT_GAME_FILE).exists();
        boolean hasEasyGames = hasGames(EASY_DIR);
        boolean hasMediumGames =hasGames(MEDIUM_DIR);
        boolean hasHardGames = hasGames(HARD_DIR);
        boolean allModesExist = hasEasyGames && hasMediumGames && hasHardGames;
        return new Catalog(hasCurrentGame, allModesExist);
    }

    private boolean hasGames(String directoryPath) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".csv"));
        return files != null && files.length > 0;
    }
    public String getRandomGameFilePath(String difficulty) throws NotFoundException {
        String dirPath = getDifficultyDirectory(difficulty);
        File dir = new File(dirPath);

        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));

        if (files == null || files.length == 0) {
            throw new NotFoundException("No games found for difficulty: " + difficulty);
        }

        Random random = new Random();
        File selectedFile = files[random.nextInt(files.length)];

        return selectedFile.getAbsolutePath();
    }



}
