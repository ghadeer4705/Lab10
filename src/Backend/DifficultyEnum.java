package Backend;

public enum DifficultyEnum {
    EASY(10),
    MEDIUM(20),
    HARD(25);

    private final int CellsToRemove;

    DifficultyEnum(int CellsToRemove) {
        this.CellsToRemove = CellsToRemove;
    }
    public int getCellsToRemove() {
        return CellsToRemove;
    }
}
