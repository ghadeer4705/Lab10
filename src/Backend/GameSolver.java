package Backend;

import java.util.ArrayList;
import java.util.List;

public class GameSolver {

    public int[][] solve(SudokuBoard board) {
        List<int[]> emptyCells = getEmptyCells(board);

        if (emptyCells.size() != 5) {
            return null;
        }

        PermutationIterator iterator = new PermutationIterator(0, (int) Math.pow(9, 5) - 1, 5);

        while (iterator.hasNext()) {
            int[] guess = iterator.next();


            for (int i = 0; i < emptyCells.size(); i++) {
                int r = emptyCells.get(i)[0];
                int c = emptyCells.get(i)[1];
                board.setIndex(r, c, guess[i]);
            }

            if (board.isValid()) {

                int[][] solution = new int[5][3];
                for (int i = 0; i < 5; i++) {
                    int r = emptyCells.get(i)[0];
                    int c = emptyCells.get(i)[1];
                    solution[i][0] = r;
                    solution[i][1] = c;
                    solution[i][2] = board.getIndex(r, c);
                }
                return solution;
            }


            for (int i = 0; i < emptyCells.size(); i++) {
                int r = emptyCells.get(i)[0];
                int c = emptyCells.get(i)[1];
                board.setIndex(r, c, 0);
            }
        }

        return null;
    }

    private List<int[]> getEmptyCells(SudokuBoard board) {
        List<int[]> cells = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.getIndex(r, c) == 0) {
                    cells.add(new int[]{r, c});
                }
            }
        }
        return cells;
    }
}
