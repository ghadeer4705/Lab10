package Backend;

import java.util.ArrayList;
import java.util.List;

public class DuplicateInfo {

    private String type;
    private int number;
    private int repeatedValue;
    private List<Integer> positions;

    public DuplicateInfo(String type, int number, int repeatedValue, List<Integer> positions) {
        this.type = type;
        this.number = number;
        this.repeatedValue = repeatedValue;
        this.positions = new ArrayList<>(positions);
    }

    public void addPosition(int pos) {
        positions.add(pos);
    }


    public String errorFormat() {
        return type + " " + number + ", #" + repeatedValue + ", " + positions; // return error like ROW 1, #1, [1, 8, 9] for example//
    }


}
