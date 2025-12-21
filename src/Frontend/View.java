package Frontend;

import Backend.DifficultyEnum;
import Controller.ControllerFacade;
import Exceptions.InvalidGame;
import Exceptions.NotFoundException;
import Exceptions.SolutionInvalidException;

import java.io.IOException;

public class View implements Controllable {
    private ControllerFacade controller;

    public View() {
        controller = new ControllerFacade();
    }

    @Override
    public boolean[] getCatalog() {
        boolean[] catalogArray = new boolean[2];
        var catalog = controller.getCatalog();
        catalogArray[0] = catalog.isCurrent();
        catalogArray[1] = catalog.isAllModesExist();
        return catalogArray;
    }

    @Override
    public int[][] getGame(char level) throws NotFoundException {
        DifficultyEnum difficulty;
        switch (Character.toUpperCase(level)) {
            case 'E':
                difficulty = DifficultyEnum.EASY;
                break;
            case 'M':
                difficulty = DifficultyEnum.MEDIUM;
                break;
            case 'H':
                difficulty = DifficultyEnum.HARD;
                break;
            case 'C': // Continue current game
                return new Backend.Loader().loadIncompleteGame().getBoard();
            default:
                throw new IllegalArgumentException("Invalid level: " + level);
        }
        return controller.getGame(difficulty).getBoard();
    }

    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        try {
            int[][] board = Backend.Validator.CSVFileReader.readFromFile(sourcePath);
            controller.driveGames(new Backend.Game(board));
        } catch (Exception e) {
            throw new SolutionInvalidException("Cannot load source game: " + e.getMessage());
        }
    }

    @Override
    public boolean[][] verifyGame(int[][] game) {
        Backend.Game g = new Backend.Game(game);
        String state = controller.verifyGame(g);

        boolean[][] result = new boolean[9][9];

        if (state.equals("VALID")) {
            // All cells are valid
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    result[i][j] = true;
                }
            }
        }
        else if (state.equals("INCOMPLETE")) {
            // Mark only filled cells as valid, empty cells as incomplete
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    result[i][j] = (game[i][j] != 0);
                }
            }
        }
        else if (state.equals("INVALID")) {
            // Check each cell manually for duplicates
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    result[i][j] = !hasDuplicate(game, i, j);
                }
            }
        }

        return result;
    }

    /**
     * Check if cell at (row, col) has a duplicate in row/column/box
     */
    private boolean hasDuplicate(int[][] board, int row, int col) {
        int value = board[row][col];
        if (value == 0) return false; // Empty cells are not duplicates

        // Check row for duplicate
        for (int c = 0; c < 9; c++) {
            if (c != col && board[row][c] == value) {
                return true;
            }
        }

        // Check column for duplicate
        for (int r = 0; r < 9; r++) {
            if (r != row && board[r][col] == value) {
                return true;
            }
        }

        // Check 3x3 box for duplicate
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if ((r != row || c != col) && board[r][c] == value) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int[][] solveGame(int[][] game) throws InvalidGame {
        Backend.Game g = new Backend.Game(game);
        int[] solution = controller.solveGame(g);

        // Convert flat array to 2D array
        int[][] solvedCells = new int[solution.length / 3][3];
        for (int i = 0; i < solution.length / 3; i++) {
            solvedCells[i][0] = solution[i * 3];
            solvedCells[i][1] = solution[i * 3 + 1];
            solvedCells[i][2] = solution[i * 3 + 2];
        }
        return solvedCells;
    }

    @Override
    public void logUserAction(UserAction userAction) throws IOException {
        String action = userAction.row + "," + userAction.col + "," +
                userAction.newValue + "," + userAction.previousValue;
        controller.logUserAction(action);
    }

    // Helper method for undo
    public void performUndo() throws IOException {
        controller.logUserAction("-1,-1,-1,-1");
    }
}