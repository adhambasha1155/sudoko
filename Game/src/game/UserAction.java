package game;

/**
 * Represents a user action on the Sudoku board.
 * Used by the Controllable interface on the viewer side.
 * Format for logging: (x, y, val, prev)
 */
public class UserAction {
    private final int row;
    private final int col;
    private final int newValue;
    private final int previousValue;
    
    /**
     * Creates a UserAction representing a cell change.
     * @param row The row index (0-8)
     * @param col The column index (0-8)
     * @param newValue The new value entered (1-9, or 0 for empty)
     * @param previousValue The previous value that was replaced
     */
    public UserAction(int row, int col, int newValue, int previousValue) {
        this.row = row;
        this.col = col;
        this.newValue = newValue;
        this.previousValue = previousValue;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getNewValue() {
        return newValue;
    }
    
    public int getPreviousValue() {
        return previousValue;
    }
    
    /**
     * Formats the action for logging: (x, y, val, prev)
     */
    public String toLogFormat() {
        return String.format("(%d, %d, %d, %d)", row, col, newValue, previousValue);
    }
    
    @Override
    public String toString() {
        return String.format("UserAction[row=%d, col=%d, newValue=%d, previousValue=%d]", 
                           row, col, newValue, previousValue);
    }
}