public class SudokuBoard {
    private int[][] board;

    public SudokuBoard(int[][] board) {
        this.board = board;
    }

    public int getIndex(int row, int col) {
        return board[row][col];
    }


    public int[][] getBoard() {
        return board;
    }

}
