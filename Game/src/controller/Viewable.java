package controller;

import game.InvalidGameException;
import game.NotFoundException;
import game.SolutionInvalidException;
import java.io.IOException;

/**
 * Interface for the controller layer - defines actions exposed to the viewer.
 * This represents the "controller facade" in the MVC architecture.
 */
public interface Viewable {
    
    /**
     * Gets the catalog of available games.
     * @return Catalog object containing game availability status
     */
    Catalog getCatalog();
    
    /**
     * Returns a random game with the specified difficulty level.
     * @param level The difficulty level (EASY, MEDIUM, HARD, or CURRENT)
     * @return Game object containing the board
     * @throws NotFoundException If no game exists for the specified difficulty
     */
    Game getGame(DifficultyEnum level) throws NotFoundException;
    
    /**
     * Takes a source solution and generates three levels of difficulty.
     * The source game must be VALID (fully solved with no duplicates).
     * @param sourceGame The fully solved Sudoku board
     * @throws SolutionInvalidException If the source game is INVALID or INCOMPLETE
     */
    void driveGames(Game sourceGame) throws SolutionInvalidException;
    
    /**
     * Verifies if a game is valid, invalid, or incomplete.
     * @param game The game to verify
     * @return Status string:
     *         - "VALID" if the game is complete and correct
     *         - "INCOMPLETE" if the game has empty cells (0s)
     *         - "INVALID" with duplicate locations if there are rule violations
     */
    String verifyGame(Game game);
    
    /**
     * Solves the game by finding the correct values for all empty cells.
     * This method only works when there are exactly 5 empty cells.
     * @param game The game to solve (must have exactly 5 empty cells)
     * @return Array of solutions: [row1, col1, val1, row2, col2, val2, ...]
     * @throws InvalidGameException If the game cannot be solved or doesn't have exactly 5 empty cells
     */
    int[] solveGame(Game game) throws InvalidGameException;
    
    /**
     * Logs a user action to the log file.
     * @param userAction Description of the action
     * @throws IOException If writing to the log file fails
     */
    void logUserAction(String userAction) throws IOException;
}