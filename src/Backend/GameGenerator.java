package Backend;


import Exceptions.SolutionInvalidException;

import java.util.List;

public class GameGenerator {

    private Validator validator;
    private RandomPairs randomPairs;

    public GameGenerator() {
        this.validator = new Mode0Validator();
        this.randomPairs = new RandomPairs();
    }

    //to verify whether the Solution is valid and complete
    public void VerifySolution(SudokuBoard source) throws SolutionInvalidException {
        ValidationResult result = validator.validate(source);
        String state = result.getState(source);

        if (!state.equals("VALID")) {
            throw new SolutionInvalidException("Solution is invalid or incomplete.");
        }
    }

    //to generate the game board by removing numbers from the solution
    public SudokuBoard generateGame(SudokuBoard board, int CellsToRemove) {
        SudokuBoard GameBoard = board.copy();

        //generate random pairs of indices to remove

        List<int[]> pairs = randomPairs.generateDistinctPairs(CellsToRemove);

        for(int [] pair : pairs) {
            int row = pair[0] % 9;
            int col = pair[1] % 9;
            GameBoard.setIndex(row, col, 0);
        }

        return GameBoard;

    }

    //generate difficulty levels
    public SudokuBoard[] generateDifficultyLevels(SudokuBoard board) throws SolutionInvalidException {
       VerifySolution(board);
       SudokuBoard[] levels = new SudokuBoard[3];
       levels[0] = generateGame(board, DifficultyEnum.EASY.getCellsToRemove());
         levels[1] = generateGame(board, DifficultyEnum.MEDIUM.getCellsToRemove());
            levels[2] = generateGame(board, DifficultyEnum.HARD.getCellsToRemove());
         return levels;
    }
}