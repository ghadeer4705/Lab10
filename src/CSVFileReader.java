import java.io.File;
import java.nio.file.Files;
import java.io.File;
import java.util.Scanner;

public class CSVFileReader {

    public static int[][] readFromFile(String filename) {
        int[][] board = new int[9][9];

        try (Scanner sc = new Scanner(new File(filename))) {
            int row = 0;

            while (sc.hasNextLine() && row < 9) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] nums = line.split(",");
                if (nums.length != 9) {
                    throw new Exception("Invalid number of columns in row " + (row + 1));
                }

                for (int col = 0; col < 9; col++) {
                    int val = Integer.parseInt(nums[col].trim());
                    if (val < 1 || val > 9) {
                        throw new Exception("Invalid number at row " + (row + 1) + ", col " + (col + 1));
                    }
                    board[row][col] = val;
                }
                row++;
            }

            if (row != 9) {
                throw new Exception("Invalid number of rows: " + row);
            }

        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
        }

        return board;
    }
}