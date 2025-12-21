package Backend;

public class SudokuBoard {
    private int[][] board;

    public SudokuBoard(int[][] board) {
        this.board = board;
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

    public SudokuBoard (SudokuBoard copyBoard){
        this.board = new int[9][9];
        for(int i =0 ; i <9 ; i++){
            for(int j =0 ; j <9 ; j++){
                this.board[i][j] = copyBoard.board[i][j];

            }
        }

    }
    // Deep copy method
    public SudokuBoard copy(){
        return new SudokuBoard(this);
    }

    public boolean isValid() {
        // Check rows
        for (int r = 0; r < 9; r++) {
            boolean[] found = new boolean[10]; // index 1-9
            for (int c = 0; c < 9; c++) {
                int valu = board[r][c];
                if (valu != 0) {
                    if (found[valu]) return false; // duplicate found
                    found[valu] = true;
                }
            }
        }

        // Check columns
        for (int c = 0; c < 9; c++) {
            boolean[] found = new boolean[10];
            for (int r = 0; r < 9; r++) {
                int valu = board[r][c];
                if (valu != 0) {
                    if (found[valu]) return false;
                    found[valu] = true;
                }
            }
        }

        // Check boxes
        for (int bRow = 0; bRow < 3; bRow++) {
            for (int bCol = 0; bCol < 3; bCol++) {
                boolean[] found = new boolean[10];
                for (int r = bRow * 3; r < bRow * 3 + 3; r++) {
                    for (int c = bCol * 3; c < bCol * 3 + 3; c++) {
                        int valu = board[r][c];
                        if (valu != 0) {
                            if (found[valu]) return false;
                            found[valu] = true;
                        }
                    }
                }
            }
        }

        return true; //valid
    }
}
