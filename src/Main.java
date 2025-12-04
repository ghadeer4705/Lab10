public class Main {
    public static void main(String[] args) throws Exception {
        String file = "sudoku.csv"; // Hardcoded path for check
        int[][] grid = CSVFileReader.readFromFile(file);
        SudokuBoard board = new SudokuBoard(grid);

        Validator validator = new Mode0Validator();
        ValidationResult result = validator.validate(board);
        result.printFinalResult();
    }
}
