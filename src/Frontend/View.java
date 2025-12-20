package Frontend;

import Backend.DifficultyEnum;
import Controller.ControllerFacade;
import Exceptions.InvalidGame;
import Exceptions.NotFoundException;
import Exceptions.SolutionInvalidException;

import java.io.IOException;

public class View implements Controllable{
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
            default:
                throw new IllegalArgumentException("Invalid level: " + level);
        }
        return controller.getGame(difficulty).getBoard();
    }

    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        // bt3ml Load l source game mn CSV file
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
        boolean valid = state.equals("VALID");
        boolean incomplete = state.equals("INCOMPLETE");

        // Mark all cells true if valid, false if incomplete or invalid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                result[i][j] = valid;
            }
        }
        return result;
    }

    @Override
    public int[][] solveGame(int[][] game) throws InvalidGame {
        Backend.Game g = new Backend.Game(game);
        int[] solution = controller.solveGame(g);

        // Convert array to row,col format
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
}
