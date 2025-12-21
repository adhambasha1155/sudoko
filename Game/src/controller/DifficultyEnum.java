package controller;

/**
 * Enum representing the difficulty levels for Sudoku games.
 * Used in the Viewable interface (controller side).
 * MUST ONLY BE USED ON CONTROLLER SIDE - NOT VIEWER SIDE.
 */
public enum DifficultyEnum {
    EASY,
    MEDIUM,
    HARD,
    CURRENT;  // Represents the currently played game
    
    /**
     * Converts the enum to the string constant format used by DifficultyConstants.
     */
    public String toConstant() {
        return this.name();
    }
    
    /**
     * Converts a string constant to the corresponding enum value.
     */
    public static DifficultyEnum fromConstant(String constant) {
        return DifficultyEnum.valueOf(constant.toUpperCase());
    }
}