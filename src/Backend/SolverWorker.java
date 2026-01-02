package Backend;

import java.util.List;

public class SolverWorker implements Runnable {

    private final SudokuBoard board;              // Flyweight (read-only)
    private final List<int[]> emptyCells;
    private final PermutationIterator iterator;
    private final SolverObserver observer;

    public SolverWorker(SudokuBoard board,
                        List<int[]> emptyCells,
                        PermutationIterator iterator,
                        SolverObserver observer) {
        this.board = board;
        this.emptyCells = emptyCells;
        this.iterator = iterator;
        this.observer = observer;
    }

    @Override
    public void run() {
        while (iterator.hasNext() &&
                !Thread.currentThread().isInterrupted()) {

            int[] guess = iterator.next();

            if (isCombinationValid(guess)) {
                observer.onSolutionFound(buildSolution(guess));
                return;
            }
        }
    }

    private boolean isCombinationValid(int[] guess) {

        int[][] temp = new int[9][9];


        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                temp[r][c] = board.getIndex(r, c);

        // apply guesses
        for (int i = 0; i < emptyCells.size(); i++) {
            int r = emptyCells.get(i)[0];
            int c = emptyCells.get(i)[1];
            temp[r][c] = guess[i];
        }

        // validate full board constraints
        for (int i = 0; i < emptyCells.size(); i++) {
            int r = emptyCells.get(i)[0];
            int c = emptyCells.get(i)[1];
            int val = temp[r][c];

            // row
            for (int col = 0; col < 9; col++)
                if (col != c && temp[r][col] == val)
                    return false;

            // column
            for (int row = 0; row < 9; row++)
                if (row != r && temp[row][c] == val)
                    return false;

            // box
            int br = (r / 3) * 3;
            int bc = (c / 3) * 3;
            for (int rr = br; rr < br + 3; rr++)
                for (int cc = bc; cc < bc + 3; cc++)
                    if ((rr != r || cc != c) && temp[rr][cc] == val)
                        return false;
        }

        return true;
    }


    private int[][] buildSolution(int[] guess) {
        int[][] result = new int[emptyCells.size()][3];
        for (int i = 0; i < emptyCells.size(); i++) {
            result[i][0] = emptyCells.get(i)[0];
            result[i][1] = emptyCells.get(i)[1];
            result[i][2] = guess[i];
        }
        return result;
    }
}
