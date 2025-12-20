package Backend;

import Backend.Validator.*;
import Exceptions.*;

import java.io.IOException;

public class ControllerFacade {

    private GameStoring storage;
    private GameGenerator generator;
    private GameSolver solver;
    private Validator validator;

    public ControllerFacade() {
        storage = new GameStoring();
        generator = new GameGenerator();
        solver = new GameSolver();
        validator = new Mode0Validator();
    }

    //fel bedaya to check available games
    public Catalog getCatalog() {
        return storage.getCatalog();
    }

    //load a random game based on difficulty
    public Game loadGame(DifficultyEnum level) throws NotFoundException {
        String path = storage.getRandomGameFilePath(level.name().toLowerCase());
        int[][] board = CSVFileReader.readFromFile(path);
        return new Game(board);
    }

    //generate easy, medium and hard games from a solved sudoku
    public void generateGames(Game solvedGame) throws SolutionInvalidException {
        SudokuBoard board = new SudokuBoard(solvedGame.getBoard());
        generator.generateDifficultyLevels(board);
    }

    //verify current game state
    public String verifyGame(Game game) {
        SudokuBoard board = new SudokuBoard(game.getBoard());
        ValidationResult result = validator.validate(board);
        return result.getState(board);
    }

    //solve the game when 5 cells are empty
    public int[][] solveGame(Game game) throws InvalidGame {
        SudokuBoard board = new SudokuBoard(game.getBoard());
        return solver.solve(board);
    }

    //save current game progress
    public void saveCurrentGame(Game game) throws IOException {
        SudokuBoard board = new SudokuBoard(game.getBoard());
        storage.SaveCurrentGame(board);
    }
}
