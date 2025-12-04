import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColumnChecker implements Checker {

    private SudokuBoard board;

    private List<DuplicateInfo> result = Collections.synchronizedList(new ArrayList<>());


    public ColumnChecker(SudokuBoard board) {
        this.board = board;

    }

    // Check a single column
    public void checkSingleColumn(int col) {
        for (int num = 1; num <= 9; num++) {
            List<Integer> positions = new ArrayList<>();
            for (int row = 0; row < 9; row++) {
                if (board.getIndex(row, col) == num) {
                    positions.add(row + 1); // rows start from 1
                }
            }
            if (positions.size() > 1) {
                result.add(new DuplicateInfo("COL", col + 1, num, positions));
            }
        }
    }

    // Check all columns
    public void checkAllColumns() {
        for (int col = 0; col < 9; col++) {
            checkSingleColumn(col);
        }
    }

    @Override
    public void run() {
        checkAllColumns();
    }

    @Override
    public List<DuplicateInfo> getResult() {
        return result;
    }
}
