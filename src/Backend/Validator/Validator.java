package Backend.Validator;

import Backend.SudokuBoard;

public interface Validator {
    ValidationResult validate(SudokuBoard board);
}
