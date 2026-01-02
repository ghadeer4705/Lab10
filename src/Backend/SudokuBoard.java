package Backend;

public class SudokuBoard {
    private int[][] board;


    public SudokuBoard(int[][] board) {
        this.board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.board[i][j] = board[i][j];
            }
        }
    }


    public SudokuBoard(SudokuBoard copyBoard) {
        this.board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.board[i][j] = copyBoard.board[i][j];
            }
        }
    }

    public int getIndex(int row, int col) {
        return board[row][col];
    }

    public void setIndex(int row, int col, int value) {
        board[row][col] = value;
    }

    public int[][] getBoard() {
        return board;
    }


    public SudokuBoard copy() {
        return new SudokuBoard(this);
    }

    public boolean isValid() {
        // Check rows
        for (int r = 0; r < 9; r++) {
            boolean[] found = new boolean[10];
            for (int c = 0; c < 9; c++) {
                int val = board[r][c];
                if (val != 0) {
                    if (found[val]) return false;
                    found[val] = true;
                }
            }
        }

        // Check columns
        for (int c = 0; c < 9; c++) {
            boolean[] found = new boolean[10];
            for (int r = 0; r < 9; r++) {
                int val = board[r][c];
                if (val != 0) {
                    if (found[val]) return false;
                    found[val] = true;
                }
            }
        }

        // Check 3x3 boxes
        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {
                boolean[] found = new boolean[10];
                for (int r = br * 3; r < br * 3 + 3; r++) {
                    for (int c = bc * 3; c < bc * 3 + 3; c++) {
                        int val = board[r][c];
                        if (val != 0) {
                            if (found[val]) return false;
                            found[val] = true;
                        }
                    }
                }
            }
        }

        return true;
    }
}
