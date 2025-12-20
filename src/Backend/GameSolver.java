package Backend;

import Exceptions.InvalidGame;

import java.util.ArrayList;
import java.util.List;

public class GameSolver {

    public int[][] solve(SudokuBoard board) throws InvalidGame {

        List<int[]> emptyCells = getEmptyCells(board);

        if (emptyCells.size() != 5) {
            throw new InvalidGame("Solver works only when exactly 5 cells are empty.");
        }

        PermutationIterator it = new PermutationIterator(5);
        BoardFlyweight fw = new BoardFlyweight(board, emptyCells);

        while (it.hasNext()) {
            int[] perm = it.next();

            if (fw.isValidWithPermutation(perm)) {

                int[][] solution = new int[5][3];
                for (int i = 0; i < 5; i++) {
                    solution[i][0] = emptyCells.get(i)[0];
                    solution[i][1] = emptyCells.get(i)[1];
                    solution[i][2] = perm[i];
                }
                return solution;
            }
        }

        throw new InvalidGame("No valid solution found.");
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
