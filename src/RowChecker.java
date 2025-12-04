import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RowChecker implements Checker {

    private SudokuBoard board;

    private List<DuplicateInfo> result = Collections.synchronizedList(new ArrayList<>());


    public RowChecker(SudokuBoard board) {
        this.board = board;

    }

    // Check a single row
    public void checkSingleRow(int row) {
        for (int num = 1; num <= 9; num++) {
            List<Integer> positions = new ArrayList<>();
            for (int col = 0; col < 9; col++) {
                if (board.getIndex(row, col) == num) {
                    positions.add(col + 1); // columns start from 1
                }
            }
            if (positions.size() > 1) {
                result.add(new DuplicateInfo("ROW", row + 1, num, positions));
            }
        }
    }

    // Check all rows
    public void checkAllRows() {
        for (int row = 0; row < 9; row++) {
            checkSingleRow(row);
        }
    }

    @Override
    public void run() {
        checkAllRows();
    }

    @Override
    public List<DuplicateInfo> getResult() {
        return result;
    }
}

