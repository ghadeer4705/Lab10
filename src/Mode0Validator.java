public class Mode0Validator implements Validator {
    @Override
    public ValidationResult validate(SudokuBoard board) {
        ValidationResult result = new ValidationResult();

        // Rows
        RowChecker row = new RowChecker(board);
        row.checkAllRows();
        result.addRowErrors(row.getResult());

        // Columns
        ColumnChecker column = new ColumnChecker(board);
        column.checkAllColumns();
        result.addColumnErrors(column.getResult());

        // Boxes
        BoxChecker box = new BoxChecker(board);
        box.checkAllBoxes();
        result.addBoxErrors(box.getResult());

        return result;
    }
}
