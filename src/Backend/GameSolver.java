package Backend;

import java.util.ArrayList;
import java.util.List;

public class GameSolver implements SolverObserver {

    private int[][] solution = null;

    public int[][] solve(SudokuBoard board) {//Facade
        // Validate board first before solving
        if (!isBoardSolvable(board)) {
            return null; // Board has duplicates, no solution possible
        }
        List<int[]> emptyCells = getEmptyCells(board);
        if (emptyCells.size() != 5) return null;

        int totalPermutations = (int) Math.pow(9, 5);
        int threadsCount = 3;
        Thread[] threads = new Thread[threadsCount];

        int rangeSize = totalPermutations / threadsCount;


        for (int i = 0; i < threadsCount; i++) {
            int start = i * rangeSize;
            int end = (i == threadsCount - 1) ? totalPermutations - 1 : (start + rangeSize - 1);

            SudokuBoard boardCopy = board.copy(); //Flyweight
            PermutationIterator iterator = new PermutationIterator(start, end, 5);

            SolverWorker worker = new SolverWorker(boardCopy, emptyCells, iterator, this);
            threads[i] = new Thread(worker);
            threads[i].start();
        }


        while (solution == null) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }


        for (Thread t : threads) {
            t.interrupt();
        }

        return solution;
    }

    @Override
    public void onSolutionFound(int[][] solution) {
        if (this.solution == null) {
            this.solution = solution;
        }
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
    private boolean isBoardSolvable(SudokuBoard board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = board.getIndex(i, j);
                if (value == 0) continue;

                // Check if this filled cell creates a conflict
                if (hasDuplicates(board, i, j, value)) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean hasDuplicates(SudokuBoard board, int row, int col, int value) {
        // Check row for duplicates
        for (int c = 0; c < 9; c++) {
            if (c != col && board.getIndex(row, c) == value) {
                return true;
            }
        }

        // Check column for duplicates
        for (int r = 0; r < 9; r++) {
            if (r != row && board.getIndex(r, col) == value) {
                return true;
            }
        }

        // Check 3x3 box for duplicates
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if (r != row && c != col && board.getIndex(r, c) == value) {
                    return true;
                }
            }
        }

        return false;
    }
}
