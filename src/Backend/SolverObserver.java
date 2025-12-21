package Backend;

public interface SolverObserver {
    void onSolutionFound(int[][] solution);//<<I>> for notifying GameSolver when a valid solution is found by SolverWorker//Notify()

}
