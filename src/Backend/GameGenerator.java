package Backend;


import Backend.Validator.Mode0Validator;
import Backend.Validator.ValidationResult;
import Backend.Validator.Validator;
import Exceptions.SolutionInvalidException;

import java.util.List;
//to generate Sudoku game boards with varying difficulty levels
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

    //generate game by removing random cells
    public SudokuBoard generateGame(SudokuBoard board, int CellsToRemove) {
        SudokuBoard GameBoard = board.copy();

        //generate random pairs of indices to remove

        List<int[]> pairs = randomPairs.generateDistinctPairs(CellsToRemove);

        for(int [] pair : pairs) {
            int row = pair[0] ;
            int col = pair[1] ;
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