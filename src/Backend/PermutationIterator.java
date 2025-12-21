package Backend;

public class PermutationIterator {
    //PermutationIterator is responsible for iterating over all possible combinations for the 5 empty cells using base 9 enumeration.

    private final int start;
    private final int end;
    private int currentPlace;
    private final int size;

    public PermutationIterator(int start, int end, int size) {
        this.start = start;
        this.end = end;
        this.currentPlace = start;
        this.size = size;  //Always 5 according to lab 10 pdf
    }

    public boolean hasNext() {
        return currentPlace <= end; //works from 0-> 9^5 -1 <<Base 9>>
    }

    public int[] next() {
        int[] guess = new int[size];
        int temp = currentPlace++;

        for (int i = size - 1; i >= 0; i--) {
            guess[i] = (temp % 9) + 1;  //Guess Maker
            temp /= 9;
        }
        return guess;
    }
}
