package Backend;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private List<DuplicateInfo> rowErrors = new ArrayList<>();
    private List<DuplicateInfo> columnErrors = new ArrayList<>();
    private List<DuplicateInfo> boxErrors = new ArrayList<>();

    public void addRowErrors(List<DuplicateInfo> list) {
        rowErrors.addAll(list);
    }

    public void addColumnErrors(List<DuplicateInfo> list) {
        columnErrors.addAll(list);
    }

    public void addBoxErrors(List<DuplicateInfo> list) {
        boxErrors.addAll(list);
    }


    public boolean isValid() {
        return rowErrors.isEmpty() && columnErrors.isEmpty() && boxErrors.isEmpty();
    }

    public boolean isCompleted(SudokuBoard sb){

        for (int r =0 ; r<9; r++){
            for (int c=0; c<9; c++){
                if (sb.getIndex(r,c) ==0){
                    return false;
                }
            }
        }
        return true;
    }

    public void printFinalResult(SudokuBoard sb) {
        if (isCompleted(sb)) {
            System.out.println("COMPLETED");
            return;
        }
       else if (isValid()) {
            System.out.println("VALID");
            return;
        } else {
            System.out.println("INVALID");
            for (DuplicateInfo d : rowErrors) System.out.println(d.errorFormat());
            System.out.println("----------------------------------------");
            for (DuplicateInfo d : columnErrors) System.out.println(d.errorFormat());
            System.out.println("----------------------------------------");
            for (DuplicateInfo d : boxErrors) System.out.println(d.errorFormat());
        }
    }


}