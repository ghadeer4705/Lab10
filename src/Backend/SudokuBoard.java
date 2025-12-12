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

    public SudokuBoard copy(){
        return new SudokuBoard(this);
    }

}
