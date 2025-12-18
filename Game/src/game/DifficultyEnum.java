package game;


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
