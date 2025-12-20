package Backend;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class GameLogger {
    private static final String LOG_FILE ="sudoku/current_game/game.log";

    public void logMove(int row ,int column , int newValue , int currentValue, int preValue) throws IOException {
        try(PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE,true))){
            out.println(row+","+column+","+newValue+","+currentValue+","+preValue);
        }
    }

    public int [] getLastMove() throws IOException {
        List<String> lines = readAllLines();

        if (lines.isEmpty()){
            return null;
        }
        String lastLine = lines.get(lines.size() - 1);
        String[] parts = lastLine.split(",");
        return new int[]{
                Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),Integer.parseInt(parts[3]),
        };

    }
    private List<String> readAllLines() throws IOException {
        List <String> lines = new ArrayList<>();
        File file = new File(LOG_FILE);

        if (!file.exists()){
            return lines;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
            return lines;

    }

    public void clearLog() throws IOException {
        File file = new File(LOG_FILE);
        if (file.exists()){
            file.delete();
        }
    }

}
