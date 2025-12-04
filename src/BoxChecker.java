import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoxChecker implements Checker {

    private SudokuBoard board;

    private List<DuplicateInfo> result = Collections.synchronizedList(new ArrayList<>());


    public BoxChecker(SudokuBoard board) {
        this.board = board;

    }

    // Check a single box
    public void checkSingleBox(int boxRowStart, int boxColStart, int boxNumber) {
        for (int num = 1; num <= 9; num++) {
            List<Integer> positions = new ArrayList<>();
            int position = 1; // position inside box from 1 to 9
            for (int r = boxRowStart; r < boxRowStart + 3; r++) {
                for (int c = boxColStart; c < boxColStart + 3; c++) {
                    if (board.getIndex(r, c) == num) {
                        positions.add(position);
                    }
                    position++;
                }
            }
            if (positions.size() > 1) {
                result.add(new DuplicateInfo("BOX", boxNumber, num, positions));
            }
        }
    }

    // Check all boxes
    public void checkAllBoxes() {
        int boxNumber = 1;
        for (int sr = 0; sr < 9; sr += 3) {
            for (int sc = 0; sc < 9; sc += 3) {
                checkSingleBox(sr, sc, boxNumber);
                boxNumber++;
            }
        }
    }

    @Override
    public void run() {
        checkAllBoxes();
    }

    @Override
    public List<DuplicateInfo> getResult() {
        return result;
    }
}
