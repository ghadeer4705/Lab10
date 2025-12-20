package Controller;

import Backend.*;
import Backend.Validator.Mode0Validator;
import Backend.Validator.ValidationResult;
import Exceptions.*;
import java.io.IOException;
import java.util.List;

public class ControllerFacade implements Viewable {

    private GameStoring storing;
    private GameGenerator generator;
    private GameSolver solver;

    public ControllerFacade() {
        storing = new GameStoring();
        generator = new GameGenerator();
        solver = new GameSolver();
    }

    //returns info about games
    @Override
    public Catalog getCatalog() {
        return storing.getCatalog();
    }

    //loads a game by difficulty
    @Override
    public Game getGame(DifficultyEnum level) throws NotFoundException {
        String diffFolder;
        switch(level) {
            case EASY:
                diffFolder = "easy";
                break;
            case MEDIUM:
                diffFolder = "medium";
                break;
            case HARD:
                diffFolder = "hard";
                break;
            default:
                diffFolder = "easy"; // default, just in case
        }
        SudokuBoard board = storing.loadGame(diffFolder, "game1");
        return new Game(board.getBoard());
    }

    //generates games from a source solution
    @Override
    public void driveGames(Game sourceGame) throws SolutionInvalidException {
        SudokuBoard board = new SudokuBoard(sourceGame.getBoard());
        SudokuBoard[] levels = generator.generateDifficultyLevels(board);

        try {
            storing.saveGameToDifficultyFolder("easy", levels[0], "game1");
            storing.saveGameToDifficultyFolder("medium", levels[1], "game1");
            storing.saveGameToDifficultyFolder("hard", levels[2], "game1");
        } catch (IOException e) {
            throw new SolutionInvalidException("Error saving generated games");
        }
    }

    //checks game status
    @Override
    public String verifyGame(Game game) {
        SudokuBoard board = new SudokuBoard(game.getBoard());
        ValidationResult result = new Mode0Validator().validate(board);
        return result.getState(board); // returns "VALID", "INCOMPLETE", or "INVALID"
    }

    //solve the game when 5 cells are empty
    @Override
    public int[] solveGame(Game game) throws InvalidGame {
        SudokuBoard board = new SudokuBoard(game.getBoard());
        int[][] solution = solver.solve(board);

        int[] flat = new int[solution.length * 3];
        for (int i = 0; i < solution.length; i++) {
            flat[i * 3] = solution[i][0];
            flat[i * 3 + 1] = solution[i][1];
            flat[i * 3 + 2] = solution[i][2];
        }
        return flat;
    }

    //log a user action
    @Override
    public void logUserAction(String userAction) throws IOException {
        String[] parts = userAction.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        int newVal = Integer.parseInt(parts[2]);
        int prev = Integer.parseInt(parts[3]);

        GameLogger logger = new GameLogger();
        logger.logMove(row, col, newVal, prev);
    }
}
