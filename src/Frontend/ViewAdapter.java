package Frontend;

import Backend.Catalog;
import Backend.DifficultyEnum;
import Backend.Game;
import Backend.Loader;
import Controller.Viewable;
import Exceptions.InvalidGame;
import Exceptions.NotFoundException;
import Exceptions.SolutionInvalidException;

import java.io.IOException;

public class ViewAdapter implements Controllable{

    private Viewable controller;
    public ViewAdapter(Viewable controller) {

        this.controller = controller;
    }


    @Override
    public boolean[] getCatalog() {
        boolean[] result = new boolean[2];
        Catalog c = controller.getCatalog();
        result[0] = c.isCurrent();
        result[1] = c.isAllModesExist();
        return result;
    }

    @Override
    public int[][] getGame(char level) throws NotFoundException {
        switch (Character.toUpperCase(level)) {
            case 'E':
                return controller.getGame(DifficultyEnum.EASY).getBoard();
            case 'M':
                return controller.getGame(DifficultyEnum.MEDIUM).getBoard();
            case 'H':
                return controller.getGame(DifficultyEnum.HARD).getBoard();
            case 'C':
                return new Loader().loadIncompleteGame().getBoard();
            default:
                throw new IllegalArgumentException("Invalid level");
        }
    }

    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        try {
            int[][] board = Backend.Validator.CSVFileReader.readFromFile(sourcePath);
            controller.driveGames(new Game(board));
        } catch (Exception e) {
            throw new SolutionInvalidException(e.getMessage());
        }

    }

   /* @Override
    public boolean[][] verifyGame(int[][] game) {
        String state = controller.verifyGame(new Game(game));
        boolean[][] result = new boolean[9][9];

        if (state.equals("VALID")) {
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    result[i][j] = true;
        } else {
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    result[i][j] = game[i][j] != 0;
        }
        return result;
    }*/
   public boolean[][] verifyGame(int[][] game) {
       String state = controller.verifyGame(new Game(game));
       boolean[][] result = new boolean[9][9];

       if (state.equals("VALID")) {
           // All cells are valid
           for (int i = 0; i < 9; i++)
               for (int j = 0; j < 9; j++)
                   result[i][j] = true;
       } else {
           // Check each cell individually
           for (int i = 0; i < 9; i++) {
               for (int j = 0; j < 9; j++) {
                   if (game[i][j] == 0) {
                       result[i][j] = true; // Empty cells are "valid" (not wrong)
                   } else {
                       // Check if this specific cell violates Sudoku rules
                       result[i][j] = isCellValid(game, i, j);
                   }
               }
           }
       }
       return result;
   }

    private boolean isCellValid(int[][] board, int row, int col) {
        int value = board[row][col];
        if (value == 0) return true;

        // Check row for duplicates
        for (int c = 0; c < 9; c++) {
            if (c != col && board[row][c] == value) {
                return false;
            }
        }

        // Check column for duplicates
        for (int r = 0; r < 9; r++) {
            if (r != row && board[r][col] == value) {
                return false;
            }
        }

        // Check 3x3 box for duplicates
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if (r != row && c != col && board[r][c] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int[][] solveGame(int[][] game) throws InvalidGame {
        int[] flat = controller.solveGame(new Game(game));
        int[][] result = new int[flat.length / 3][3];

        for (int i = 0; i < result.length; i++) {
            result[i][0] = flat[i * 3];
            result[i][1] = flat[i * 3 + 1];
            result[i][2] = flat[i * 3 + 2];
        }
        return result;
    }

    @Override
    public void logUserAction(UserAction userAction) throws IOException {
        String s = userAction.row + "," + userAction.col + "," +
                userAction.newValue + "," + userAction.previousValue;
        controller.logUserAction(s);

    }
    public void undo() throws IOException {
        controller.logUserAction("-1,-1,-1,-1");
    }
}
