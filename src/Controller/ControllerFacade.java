package Controller;
import Backend.*;
import Backend.Validator.Mode0Validator;
import Backend.Validator.ValidationResult;
import Exceptions.*;

import java.io.File;
import java.io.IOException;

public class ControllerFacade implements Viewable {
    private DifficultyEnum currentDifficulty;
    private GameStoring storing;
    private GameGenerator generator;
    private GameSolver solver;
    private GameLogger logger;
    private String currentGameFileName;
    private SudokuBoard currentBoard; // Cache for current game board

    public ControllerFacade() {
      //  storing = GameStoring.getInstance();
        storing = new GameStoring();
        generator = new GameGenerator();
        solver = new GameSolver();
        //logger = GameLogger.getInstance();
        logger = new GameLogger();
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
       currentDifficulty = level;
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
                throw new NotFoundException("Invalid difficulty level: " + level);
       }
      // Get random game file path
      String filePath = storing.getRandomGameFilePath(diffFolder);
      File file = new File(filePath);
      currentGameFileName = file.getName().replace(".csv", "");
       SudokuBoard board = storing.loadGame(diffFolder, "game");
     //  SudokuBoard board = new Loader().loadGameByDifficulty(diffFolder);
       currentBoard = board;
      //save as current game
       try {
           storing.SaveCurrentGame(board);
           //clear log for new game
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
       //generate difficulty levels
      SudokuBoard[] levels = generator.generateDifficultyLevels(board);
       try {
           storing.saveGameToDifficultyFolder("easy", levels[0], "game");
           storing.saveGameToDifficultyFolder("medium", levels[1], "game");
           storing.saveGameToDifficultyFolder("hard", levels[2], "game");
       } catch (IOException e) {
           throw new SolutionInvalidException("Error saving generated games");
       }
   /*  for (int i = 1; i <= 3; i++) {
           SudokuBoard[] levels = generator.generateDifficultyLevels(board);
           try {
               storing.saveGameToDifficultyFolder("easy", levels[0], "game" + i);
               storing.saveGameToDifficultyFolder("medium", levels[1], "game" + i);
               storing.saveGameToDifficultyFolder("hard", levels[2], "game" + i);
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
       }*/
   }
    //checks game status
   /* @Override
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
                    if (currentDifficulty != null && currentGameFileName != null) {
                        String diff = currentDifficulty.name().toLowerCase();
                        storing.deleteGameFromDifficultyFolder(diff, currentGameFileName);
                    }
                    storing.deleteCurrentGame();
                    logger.clearLog();
                } catch (IOException | NotFoundException e) {
                    //log but don't fail
                    System.err.println("Failed to delete completed game: " + e.getMessage());
                }
            }
        }
        return state;
    }*/
    @Override
    public String verifyGame(Game game) {
        SudokuBoard board = new SudokuBoard(game.getBoard());
        ValidationResult result = new Mode0Validator().validate(board);
        String state = result.getState(board);

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
                    // Delete from difficulty folders
                    storing.deleteCompletedGame(game.getBoard());

                    // Delete current game
                    storing.deleteCurrentGame();
                    logger.clearLog();
                } catch (IOException | NotFoundException e) {
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
        //check if exactly 5 cells are empty
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
        // Check if solution is null (board has duplicates or unsolvable)
        if (solution == null) {
            throw new InvalidGame("No solution found. The board contains errors or duplicates in the filled cells.");
        }
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
        // Update the board in storage
        try {
            // Update board if available, otherwise load
            if (currentBoard == null) {
                currentBoard = new Loader().loadIncompleteGame();
            }
            // Apply the change
            currentBoard.setIndex(row, col, newVal);
            // Save the updated board
            storing.SaveCurrentGame(currentBoard);
        } catch (NotFoundException e) {
            // If load fails, try to create new board from scratch
            throw new IOException("Cannot load current game: " + e.getMessage());
        }
    }

    private void performUndo() throws IOException {
        try {
            //Get last move
            int[] lastMove = logger.getLastMove();
            if (lastMove == null) {
                throw new IOException("No action to undo");
            }
            int row = lastMove[0];
            int col = lastMove[1];
            int prevVal = lastMove[3]; // Previous value

            if (currentBoard == null) {
                currentBoard = new Loader().loadIncompleteGame();
            }
            // Revert the change
            currentBoard.setIndex(row, col, prevVal);
            //Save the reverted board
            storing.SaveCurrentGame(currentBoard);
            //remove from log (after successful save)
            logger.removeLastMove();
        } catch (NotFoundException e) {
            throw new IOException("Undo failed - cannot load game: " + e.getMessage());
        } catch (Exception e) {
            throw new IOException("Undo failed: " + e.getMessage());
        }
    }
}