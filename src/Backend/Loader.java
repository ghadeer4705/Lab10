package Backend;

import Backend.Validator.CSVFileReader;
import Exceptions.NotFoundException;

public class Loader {
    private GameStoring storage;

    public Loader() {
        this.storage =GameStoring.getInstance();
    }
    public SudokuBoard loadIncompleteGame() throws NotFoundException {
        return storage.loadCurrentGame();
    }
    public SudokuBoard loadGameByDifficulty(String difficulty) throws NotFoundException {
        String gamePath = storage.getRandomGameFilePath(difficulty);
        int[][] grid = CSVFileReader.readFromFile(gamePath);
        return new SudokuBoard(grid);
    }
}
