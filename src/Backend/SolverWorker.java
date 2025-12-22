package Backend;

import java.util.List;

public class SolverWorker implements Runnable {//Subject

    private final SudokuBoard board;
    private final List<int[]> emptyCells;
    private final PermutationIterator iterator;
    private final SolverObserver observer;//Notify

    public SolverWorker(SudokuBoard board, List<int[]> emptyCells, PermutationIterator iterator, SolverObserver observer) {
        this.board = board;
        this.emptyCells = emptyCells;
        this.iterator = iterator;
        this.observer = observer;
    }

    @Override
    public void run() {
        while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
            int[] guess = iterator.next();

            //Only apply guess on empty cells
            for (int i = 0; i < emptyCells.size(); i++) {
                int r = emptyCells.get(i)[0];
                int c = emptyCells.get(i)[1];
                board.setIndex(r, c, guess[i]);
            }

            if (board.isValid()) { //Checkk if full board is valid
                observer.onSolutionFound(buildSolution());//Notify observer
                return; //Stop el thread
            }

            //Rollback is applied if guess was invalid
            for (int i = 0; i < emptyCells.size(); i++) {
                int r = emptyCells.get(i)[0];
                int c = emptyCells.get(i)[1];
                board.setIndex(r, c, 0);//Reset cell
            }
        }
    }

    private int[][] buildSolution() {//Build a 2-D Array representing the solution of empty cells
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
