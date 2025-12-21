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
    private GameLogger logger;
    private SudokuBoard currentBoard; // Cache for current game board

    public ControllerFacade() {
        storing = GameStoring.getInstance();
        generator = new GameGenerator();
        solver = new GameSolver();
        logger = GameLogger.getInstance();
        currentBoard = null;

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
        //SudokuBoard board = storing.loadGame(diffFolder, "game1");
        SudokuBoard board = new Loader().loadGameByDifficulty(diffFolder);

        // Cache the board
        currentBoard = board;

       //Save as current game
        try {
            storing.SaveCurrentGame(board);
            // Clear log for new game
            logger.clearLog();
        } catch (IOException e) {
            throw new NotFoundException("Error saving current game: " + e.getMessage());
        }

        return new Game(board.getBoard());
    }


    //generates games from a source solution
    @Override
    public void driveGames(Game sourceGame) throws SolutionInvalidException {
        SudokuBoard board = new SudokuBoard(sourceGame.getBoard());
        // Verify source solution first (as per lab requirements)
        ValidationResult result = new Mode0Validator().validate(board);
        String state = result.getState(board);

        if (!state.equals("VALID")) {
            throw new SolutionInvalidException("Source solution is " + state + ". Must be VALID.");
        }

        // Generate difficulty levels
       /*SudokuBoard[] levels = generator.generateDifficultyLevels(board);

        try {
            storing.saveGameToDifficultyFolder("easy", levels[0], "game1");
            storing.saveGameToDifficultyFolder("medium", levels[1], "game1");
            storing.saveGameToDifficultyFolder("hard", levels[2], "game1");
        } catch (IOException e) {
            throw new SolutionInvalidException("Error saving generated games");
        }*/
      for (int i = 1; i <= 3; i++) {
            SudokuBoard[] levels = generator.generateDifficultyLevels(board);
            try {
                storing.saveGameToDifficultyFolder("easy", levels[0], "game" + i);
                storing.saveGameToDifficultyFolder("medium", levels[1], "game" + i);
                storing.saveGameToDifficultyFolder("hard", levels[2], "game" + i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    //checks game status
    @Override
    public String verifyGame(Game game) {
        SudokuBoard board = new SudokuBoard(game.getBoard());
        ValidationResult result = new Mode0Validator().validate(board);
        String state = result.getState(board);

        // Check if board is complete and valid - delete if so
        if (state.equals("VALID")) {
            boolean isComplete = true;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (game.getBoard()[i][j] == 0) {
                        isComplete = false;
                        break;
                    }
                }
                if (!isComplete) break;
            }

            if (isComplete) {
                try {
                    storing.deleteCurrentGame();
                    logger.clearLog();
                } catch (IOException | NotFoundException e) {
                    // Log but don't fail
                    System.err.println("Failed to delete completed game: " + e.getMessage());
                }
            }
        }

        return state;
    }

    //solve the game when 5 cells are empty
    @Override
    public int[] solveGame(Game game) throws InvalidGame {
        SudokuBoard board = new SudokuBoard(game.getBoard());

        // Check if exactly 5 cells are empty (lab requirement)
        int emptyCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoard()[i][j] == 0) emptyCount++;
            }
        }

        if (emptyCount != 5) {
            throw new InvalidGame("Solver requires exactly 5 empty cells. Found: " + emptyCount);
        }

        int[][] solution = solver.solve(board);
//nehawel mn 2D array le flat 1D array 3shan el frontend yesta3mlha b sohoula
        int[] flat = new int[solution.length * 3];
        for (int i = 0; i < solution.length; i++) {
            flat[i * 3] = solution[i][0];
            flat[i * 3 + 1] = solution[i][1];
            flat[i * 3 + 2] = solution[i][2];
        }
        return flat;
    }

    //log a user action
   /* @Override
    public void logUserAction(String userAction) throws IOException {
        String[] parts = userAction.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        int newVal = Integer.parseInt(parts[2]);
        int prev = Integer.parseInt(parts[3]);

        GameLogger logger = GameLogger.getInstance();
        logger.logMove(row, col, newVal, prev);
    }
    */

    @Override
    public void logUserAction(String userAction) throws IOException {
        String[] parts = userAction.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        int newVal = Integer.parseInt(parts[2]);
        int prev = Integer.parseInt(parts[3]);

        // UNDO SIGNAL: if all values are -1, perform undo
        if (row == -1 && col == -1 && newVal == -1 && prev == -1) {
            performUndo();
            return;
        }

        // Normal logging
        logger.logMove(row, col, newVal, prev);

        // Update the cache board in storage

        try {
            // Update cached board if available, otherwise load
            if (currentBoard == null) {
                currentBoard = new Loader().loadIncompleteGame();
            }

            // Apply the change
            currentBoard.setIndex(row, col, newVal);

            // Save to disk
            storing.SaveCurrentGame(currentBoard);

        } catch (NotFoundException e) {
            // If load fails, try to create new board from scratch
            throw new IOException("Cannot load current game: " + e.getMessage());
        }
    }



    private void performUndo() throws IOException {
        try {
            // 1. Get last move
            int[] lastMove = logger.getLastMove();
            if (lastMove == null) {
                throw new IOException("No action to undo");
            }

            int row = lastMove[0];
            int col = lastMove[1];
            int prevVal = lastMove[3]; // Previous value

            // 2. Load or use cached board
            if (currentBoard == null) {
                currentBoard = new Loader().loadIncompleteGame();
            }

            // 3. Revert the change
            currentBoard.setIndex(row, col, prevVal);

            // 4. Save the reverted board
            storing.SaveCurrentGame(currentBoard);

            // 5. ONLY NOW remove from log (after successful save)
            logger.removeLastMove();

        } catch (NotFoundException e) {
            throw new IOException("Undo failed - cannot load game: " + e.getMessage());
        } catch (Exception e) {
            throw new IOException("Undo failed: " + e.getMessage());
        }
    }

}