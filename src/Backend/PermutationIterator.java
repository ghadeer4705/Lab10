package Backend;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PermutationIterator implements Iterator<int[]> {

    private final int length;
    private final int[] current;
    private boolean hasNext = true;

    public PermutationIterator(int length) {
        this.length = length;
        this.current = new int[length];
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public int[] next() {
        if (!hasNext) {
            throw new NoSuchElementException();
        }


        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = current[i] + 1;
        }

        increment();
        return result;
    }

    private void increment() {
        for (int i = length - 1; i >= 0; i--) {
            if (current[i] < 8) {
                current[i]++;
                return;
            } else {
                current[i] = 0;
            }
        }
        hasNext = false;
    }
}
