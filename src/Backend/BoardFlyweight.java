package Backend;

import java.util.List;

public class BoardFlyweight {

    private SudokuBoard board;
    private List<int[]> emptyCells;
    private Validator validator;

    public BoardFlyweight(SudokuBoard board, List<int[]> emptyCells) {
        this.board = board;
        this.emptyCells = emptyCells;
        this.validator = new Mode0Validator();
    }

    public boolean isValidWithPermutation(int[] values) {


        for (int i = 0; i < emptyCells.size(); i++) {
            int r = emptyCells.get(i)[0];
            int c = emptyCells.get(i)[1];
            board.setIndex(r, c, values[i]);
        }

        ValidationResult result = validator.validate(board);
        boolean valid = result.isValid();


        for (int i = 0; i < emptyCells.size(); i++) {
            int r = emptyCells.get(i)[0];
            int c = emptyCells.get(i)[1];
            board.setIndex(r, c, 0);
        }

        return valid;
    }
}
