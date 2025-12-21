package Backend;

import java.util.List;

public class SolverWorker implements Runnable {

    private final SudokuBoard board;
    private final List<int[]> emptyCells;
    private final PermutationIterator iterator;
    private final SolverObserver observer;
    private volatile boolean stopRequested = false;

    public SolverWorker(SudokuBoard board, List<int[]> emptyCells, PermutationIterator iterator, SolverObserver observer) {
        this.board = board;
        this.emptyCells = emptyCells;
        this.iterator = iterator;
        this.observer = observer;
    }

    public void requestStop() {
        this.stopRequested = true;
    }

    @Override
    public void run() {
        while (iterator.hasNext() && !stopRequested) {
            int[] guess = iterator.next();

            // apply guess only on empty cells
            for (int i = 0; i < emptyCells.size(); i++) {
                int r = emptyCells.get(i)[0];
                int c = emptyCells.get(i)[1];
                board.setIndex(r, c, guess[i]);
            }

            if (board.isValid()) {  // check full board validity
                observer.onSolutionFound(buildSolution());
                return;
            }

            // rollback
            for (int i = 0; i < emptyCells.size(); i++) {
                int r = emptyCells.get(i)[0];
                int c = emptyCells.get(i)[1];
                board.setIndex(r, c, 0);
            }
        }
    }

    private int[][] buildSolution() {
        int[][] result = new int[emptyCells.size()][3];
        for (int i = 0; i < emptyCells.size(); i++) {
            int r = emptyCells.get(i)[0];
            int c = emptyCells.get(i)[1];
            result[i][0] = r;
            result[i][1] = c;
            result[i][2] = board.getIndex(r, c);
        }
        return result;
    }
}
