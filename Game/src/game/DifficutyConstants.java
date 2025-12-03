package game;

/**
 * Defines the four standard constant string values for the Sudoku game difficulty levels.
 * This approach uses public static final String fields to define the constants.
 */
public final class DifficutyConstants {

    // Prevent instantiation of this utility class
    private DifficutyConstants() {} 

    public static final String EASY = "EASY";
    public static final String MEDIUM = "MEDIUM";
    public static final String HARD = "HARD";
    public static final String CURRENT = "CURRENT"; // Represents the game currently being played/saved (for the GameStorageManager)
}