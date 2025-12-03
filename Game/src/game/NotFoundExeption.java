package game;

/**
 * Custom exception thrown when a requested game file (identified by difficulty level)
 * cannot be found in the game's storage directory. This is typically used by the 
 * persistence layer when the front-end attempts to load a saved game that doesn't exist.
 * * This file resolves the compilation error in the Viewable interface method:
 * int[][] getGame(String level) throws NotFoundException;
 */
public class NotFoundExeption extends Exception {

    /**
     * Constructs a NotFoundException with a detailed message.
     * @param message The detail message (e.g., "Game of difficulty 'HARD' not found.").
     */
    public NotFoundExeption(String message) {
        super(message);
    }
}