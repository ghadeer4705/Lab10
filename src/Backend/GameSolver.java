package Backend;

import java.util.ArrayList;
import java.util.List;

public class GameSolver implements SolverObserver {

    private volatile int[][] solution;

    public int[][] solve(SudokuBoard board) {

        List<int[]> emptyCells = getEmptyCells(board);

        // ثابت على 5 خلايا فقط
        if (emptyCells.size() != 5) {
            return null;
        }

        int total = (int) Math.pow(9, 5); // ثابت على 5 خلايا
        int threads = 3;
        int range = total / threads;

        SolverThreadFactory factory = new SolverThreadFactory();
        List<SolverWorker> workers = new ArrayList<>();
        List<Thread> running = new ArrayList<>();

        for (int i = 0; i < threads; i++) {

            int start = i * range;
            int end = (i == threads - 1) ? total - 1 : start + range - 1;

            SudokuBoard copy = new SudokuBoard(board.getBoard());
            PermutationIterator iterator = new PermutationIterator(start, end, 5);

            SolverWorker worker = new SolverWorker(
                    copy,
                    emptyCells,
                    iterator,
                    this
            );

            workers.add(worker);
            Thread t = factory.createThread(worker, i);
            running.add(t);
            t.start();
        }

        // انتظار جميع threads
        for (Thread t : running) {
            try {
                t.join();
            } catch (InterruptedException ignored) {}
        }

        // طلب إيقاف باقي workers لو في حل موجود
        if (solution != null) {
            for (SolverWorker w : workers) {
                w.requestStop();
            }
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
}
