package viewer;

import game.InvalidGameException;
import game.NotFoundException;
import game.SolutionInvalidException;
import java.io.IOException;

/**
 * Interface for the viewer/presentation layer.
 * Represents actions that GUI components signal according to user actions.
 * This is the "view facade" in the MVC architecture.
 */
public interface Controllable {
    
    /**
     * Gets the catalog of available games.
     * @return Boolean array where:
     *         [0] = true if there's a current/incomplete game
     *         [1] = true if all difficulty modes (easy, medium, hard) exist
     */
    boolean[] getCatalog();
    
    /**
     * Gets a game of the specified difficulty level.
     * @param level Difficulty character: 'e' (easy), 'm' (medium), 'h' (hard), 'c' (current)
     * @return 9x9 board array
     * @throws NotFoundException If no game exists for the specified level
     */
    int[][] getGame(char level) throws NotFoundException;
    
    /**
     * Generates three difficulty levels from a source solution file.
     * @param sourcePath File path to the solved Sudoku CSV file
     * @throws SolutionInvalidException If the source solution is not VALID
     */
    void driveGames(String sourcePath) throws SolutionInvalidException;
    
    /**
     * Verifies if each cell in the game is correct or has duplicates.
     * @param game The 9x9 board to verify
     * @return 9x9 boolean array where:
     *         true = cell is valid (no duplicates)
     *         false = cell is invalid (part of a duplicate)
     */
    boolean[][] verifyGame(int[][] game);
    
    /**
     * Solves the game when exactly 5 cells are empty.
     * @param game The 9x9 board to solve
     * @return 2D array where each row is [row, col, solution_value]
     *         Example: [[2, 3, 7], [4, 5, 9], ...] means cell (2,3) should be 7, etc.
     * @throws InvalidGameException If the game cannot be solved or doesn't have exactly 5 empty cells
     */
    int[][] solveGame(int[][] game) throws InvalidGameException;
    
    /**
     * Logs a user action.
     * @param userAction The UserAction object containing action details
     * @throws IOException If writing to the log file fails
     */
    void logUserAction(UserAction userAction) throws IOException;
}