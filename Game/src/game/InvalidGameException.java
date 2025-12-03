package game;

/**
 * Custom exception thrown when a Sudoku game board is in a state that prevents
 * core logic operations from completing successfully, such as:
 * 1. Attempting to generate new games from an invalid source board (driveGames).
 * 2. Attempting to solve a section of a board that is fundamentally invalid 
 * or requires more than the allowed 5 unknown cells to be solved (solveFiveCells/solveGame).
 * * This file addresses the compilation requirement for the Controllable and Viewable interfaces.
 */
public class InvalidGameException extends Exception {

    /**
     * Constructs an InvalidGameException with a detailed message.
     * @param message The detail message explaining why the game state is invalid for the requested operation.
     */
    public InvalidGameException(String message) {
        super(message);
    }
}