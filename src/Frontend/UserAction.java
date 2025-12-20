package Frontend;

//Represents a user move/action on the Sudoku board
public class UserAction {
    public int row;           // the row the user interacted with
    public int col;           // the column the user interacted with
    public int newValue;      // the new value the user entered
    public int previousValue; // the old value that was there before

    // constructor to easily create a UserAction
    public UserAction(int row, int col, int newValue, int previousValue) {
        this.row = row;
        this.col = col;
        this.newValue = newValue;
        this.previousValue = previousValue;
    }
}
