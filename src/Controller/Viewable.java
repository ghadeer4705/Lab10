package Controller;

import Backend.*;
import Exceptions.*;
import java.io.IOException;

public interface Viewable {

    // Returns the catalog (info about current game and available difficulties)
    Catalog getCatalog();

    // Returns a random game for a difficulty level
    Game getGame(DifficultyEnum level) throws NotFoundException;

    // Given a complete solution, generate 3 difficulty games
    void driveGames(Game sourceGame) throws SolutionInvalidException;

    // Check the game and return a string: "VALID", "INCOMPLETE", "INVALID ..."
    String verifyGame(Game game);

    // Solve a game (5 empty cells) and return an int array: row,col,value,...
    int[] solveGame(Game game) throws InvalidGame;

    // Log a user action in the format "row,col,new,prev"
    void logUserAction(String userAction) throws IOException;
}
