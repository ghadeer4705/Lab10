package Backend;

public class SolverThreadFactory {

    public Thread createThread(Runnable task, int index) {
        return new Thread(task, "SolverThread-" + index);
    }
}
