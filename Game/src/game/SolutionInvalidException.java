package game;

/**
 * Exception thrown when the source solution provided to driveGames() 
 * is not VALID (i.e., it is INVALID or INCOMPLETE).
 * This prevents generating new games from a flawed source.
 */
public class SolutionInvalidException extends Exception {

    /**
     * Constructs a SolutionInvalidException with a detailed message.
     * @param message The detail message explaining why the solution is invalid.
     */
    public SolutionInvalidException(String message) {
        super(message);
    }
}