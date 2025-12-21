package Backend;

import java.util.ArrayList;
import java.util.List;

public class GameSolver implements SolverObserver {

    private volatile int[][] partialSolution; // الخانات الفاضية اللي اتحلت
    private List<SolverWorker> workers;

    // هاي الطريقة هترجع board كامل
    public int[][] solve(SudokuBoard board) {

        List<int[]> emptyCells = getEmptyCells(board);

        if (emptyCells.size() != 5) return null;

        int total = (int) Math.pow(9, emptyCells.size());
        int threads = 3;
        int range = total / threads;

        SolverThreadFactory factory = new SolverThreadFactory();
        workers = new ArrayList<>();
        List<Thread> running = new ArrayList<>();

        for (int i = 0; i < threads; i++) {

            int start = i * range;
            int end = (i == threads - 1) ? total - 1 : start + range - 1;

            SudokuBoard copy = board.copy();
            PermutationIterator iterator = new PermutationIterator(start, end, emptyCells.size());

            SolverWorker worker = new SolverWorker(copy, emptyCells, iterator, this);
            workers.add(worker);

            Thread t = factory.createThread(worker, i);
            running.add(t);
            t.start();
        }

        for (Thread t : running) {
            try {
                t.join();
            } catch (InterruptedException ignored) {}
        }

        if (partialSolution == null) return null;

        // دمج partial solution مع board الأصلي
        int[][] solvedBoard = board.copy().getBoard();
        for (int i = 0; i < partialSolution.length; i++) {
            int r = partialSolution[i][0];
            int c = partialSolution[i][1];
            int val = partialSolution[i][2];
            solvedBoard[r][c] = val;
        }

        return solvedBoard;
    }

    @Override
    public void onSolutionFound(int[][] solution) {
        if (this.partialSolution == null) {
            this.partialSolution = solution;

            // فورًا اطلب من كل worker يوقف نفسه
            for (SolverWorker w : workers) {
                w.requestStop();
            }
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
}
