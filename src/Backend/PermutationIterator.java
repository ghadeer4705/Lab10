package Backend;

public class PermutationIterator {

    private final int start;
    private final int end;
    private int current;
    private final int size;

    public PermutationIterator(int start, int end, int size) {
        this.start = start;
        this.end = end;
        this.current = start;
        this.size = size;
    }

    public boolean hasNext() {
        return current <= end;
    }

    public int[] next() {
        int[] guess = new int[size];
        int temp = current++;

        for (int i = size - 1; i >= 0; i--) {
            guess[i] = (temp % 9) + 1;
            temp /= 9;
        }
        return guess;
    }
}
