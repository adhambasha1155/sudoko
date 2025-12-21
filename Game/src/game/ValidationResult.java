package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Stores the results of Sudoku board validation.
 * Thread-safe implementation for concurrent checker operations.
 */
public class ValidationResult {
    
    // Thread-safe list of duplicates found
    private final List<Duplicate> duplicates;
    
    // Flag indicating if any zero (empty cell) was found
    private final boolean hasZero;
    
    // Reference to the board being validated
    private final SudokuBoard board;
    
    /**
     * Record class representing a duplicate value found during validation.
     */
    public record Duplicate(
        String type,           // "ROW", "COL", or "BOX"
        int id,                // Row/Column/Box number (0-8)
        int value,             // The duplicate value (1-9)
        List<Integer> locations // Indices where the duplicate appears
    ) {}
    
    /**
     * Creates a ValidationResult and checks for zeros.
     */
    public ValidationResult(SudokuBoard board) {
        this.board = board;
        this.duplicates = new ArrayList<>();
        this.hasZero = checkForZeros();
    }
    
    /**
     * Checks if the board contains any zeros (empty cells).
     */
    private boolean checkForZeros() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Adds a duplicate to the results (thread-safe).
     */
    public synchronized void addDuplicate(String type, int id, int value, List<Integer> locations) {
        duplicates.add(new Duplicate(type, id, value, locations));
    }
    
    /**
     * Gets all duplicates found.
     * @return List of Duplicate records
     */
    public List<Duplicate> getDuplicates() {
        return new ArrayList<>(duplicates); // Return copy for thread safety
    }
    
    /**
     * Checks if there are any duplicates.
     */
    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }
    
    /**
     * Gets the validation status.
     * @return "VALID", "INVALID", or "INCOMPLETE"
     */
    public String getStatus() {
        if (hasDuplicates()) {
            return "INVALID";
        } else if (hasZero) {
            return "INCOMPLETE";
        } else {
            return "VALID";
        }
    }
    
    /**
     * Checks if the board is valid (complete and no duplicates).
     */
    public boolean isValid() {
        return getStatus().equals("VALID");
    }
    
    /**
     * Checks if the board is incomplete (has zeros).
     */
    public boolean isIncomplete() {
        return hasZero;
    }
    
    /**
     * Gets the number of duplicates found.
     */
    public int getDuplicateCount() {
        return duplicates.size();
    }
    
    /**
     * Prints all duplicates in a readable format.
     */
    public void printDuplicates() {
        if (duplicates.isEmpty()) {
            System.out.println("No duplicates found.");
            return;
        }
        
        System.out.println("Duplicates found:");
        for (Duplicate dup : duplicates) {
            System.out.printf("%s %d, #%d, %s%n", 
                dup.type(), 
                dup.id(), 
                dup.value(), 
                dup.locations());
        }
    }
    
    /**
     * Gets a formatted string representation of the validation result.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{");
        sb.append("status=").append(getStatus());
        sb.append(", duplicates=").append(duplicates.size());
        sb.append(", hasZero=").append(hasZero);
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Gets duplicates grouped by type (ROW, COL, BOX).
     */
    public Map<String, List<Duplicate>> getDuplicatesByType() {
        return duplicates.stream()
            .collect(Collectors.groupingBy(Duplicate::type));
    }
    
    /**
     * Gets duplicates for a specific row.
     */
    public List<Duplicate> getRowDuplicates(int row) {
        return duplicates.stream()
            .filter(d -> d.type().equals("ROW") && d.id() == row)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets duplicates for a specific column.
     */
    public List<Duplicate> getColumnDuplicates(int col) {
        return duplicates.stream()
            .filter(d -> d.type().equals("COL") && d.id() == col)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets duplicates for a specific box.
     */
    public List<Duplicate> getBoxDuplicates(int box) {
        return duplicates.stream()
            .filter(d -> d.type().equals("BOX") && d.id() == box)
            .collect(Collectors.toList());
    }
    
    /**
     * Checks if a specific cell is involved in any duplicate.
     * @param row Row index (0-8)
     * @param col Column index (0-8)
     * @return true if the cell is part of a duplicate
     */
    public boolean isCellInvalid(int row, int col) {
        // Check row duplicates
        for (Duplicate dup : getRowDuplicates(row)) {
            if (dup.locations().contains(col)) {
                return true;
            }
        }
        
        // Check column duplicates
        for (Duplicate dup : getColumnDuplicates(col)) {
            if (dup.locations().contains(row)) {
                return true;
            }
        }
        
        // Check box duplicates
        int boxId = (row / 3) * 3 + (col / 3);
        for (Duplicate dup : getBoxDuplicates(boxId)) {
            int localIndex = (row % 3) * 3 + (col % 3);
            if (dup.locations().contains(localIndex)) {
                return true;
            }
        }
        
        return false;
    }
}