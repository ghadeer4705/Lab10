package Backend;

import java.util.ArrayList;
import java.util.List;

public class GameSolver implements SolverObserver {

    private volatile int[][] solution;
    private final SharedStopFlag stopFlag = new SharedStopFlag();

    public int[][] solve(SudokuBoard board) {

        // 1️⃣ get empty cells (من غير الاعتماد على SudokuBoard)
        List<int[]> emptyCells = getEmptyCells(board);

        int total = (int) Math.pow(9, emptyCells.size());
        int threads = 3;
        int range = total / threads;

        SolverThreadFactory factory = new SolverThreadFactory();
        List<Thread> running = new ArrayList<>();

        // 2️⃣ create & start worker threads
        for (int i = 0; i < threads; i++) {

            int start = i * range;
            int end = (i == threads - 1)
                    ? total - 1
                    : start + range - 1;

            // Flyweight idea: each thread reuses its own board
            SudokuBoard copy = new SudokuBoard(board.getBoard());

            PermutationIterator iterator =
                    new PermutationIterator(start, end, emptyCells.size());

            SolverWorker worker = new SolverWorker(
                    copy,
                    emptyCells,
                    iterator,
                    this,
                    stopFlag
            );

            Thread t = factory.createThread(worker, i);
            running.add(t);
            t.start();
        }

        // 3️⃣ wait for all threads
        for (Thread t : running) {
            try {
                t.join();
            } catch (InterruptedException ignored) {}
        }

        return solution;
    }

    // 4️⃣ Observer callback
    @Override
    public void onSolutionFound(int[][] solution) {
        if (this.solution == null) {
            this.solution = solution;
            stopFlag.stop(); // notify all threads to stop
        }
    }

    // 5️⃣ helper method (instead of board.getEmptyCells)
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
