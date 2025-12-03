package game;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ValidationResult {

    // Updated Record: Now stores 'locations' (the list of indices where the dupe appears)
    public record Duplicate(String type, int id, int value, List<Integer> locations) {}

    // New Fields for INCOMPLETE state checking
    private final SudokuBoard board;
    private boolean hasZero = false;

    // Thread-safe list
    private final List<Duplicate> duplicates = new CopyOnWriteArrayList<>();

    /**
     * Constructor updated to accept SudokuBoard and check for the presence of 0s.
     * This is required to determine the INCOMPLETE state.
     * @param board The board to check for incomplete state.
     */
    public ValidationResult(SudokuBoard board) {
        this.board = board;
        // Check for 0s immediately upon creation
        for(int r = 0; r < 9; r++) {
            for(int c = 0; c < 9; c++) {
                // If a cell contains '0', the board is incomplete.
                if (board.getCell(r, c) == 0) {
                    hasZero = true;
                    // Stop checking as soon as one 0 is found
                    return;
                }
            }
        }
    }

    public void addDuplicate(String type, int id, int value, List<Integer> locations) {
        duplicates.add(new Duplicate(type, id, value, locations));
    }

    /**
     * Determines the overall status: VALID, INVALID, or INCOMPLETE.
     */
    public String getStatus() {
        if (!duplicates.isEmpty()) {
            return "INVALID";
        }
        if (hasZero) {
            return "INCOMPLETE";
        }
        return "VALID";
    }

    /**
     * The board is truly valid only if no duplicates exist AND no empty cells exist.
     */
    public boolean isValid() {
        return getStatus().equals("VALID");
    }

    public void printDuplicates() {
        String status = getStatus();
        System.out.println(status);
        
        if (status.equals("VALID") || status.equals("INCOMPLETE")) {
            // Only print details if invalid
            return;
        }

        // Convert to a standard list to sort it for the report
        List<Duplicate> sortedList = new ArrayList<>(duplicates);
        
        // Sort to ensure a consistent output order: ROW -> COL -> BOX
        sortedList.sort(Comparator.comparingInt(d -> {
            return switch (d.type()) {
                case "ROW" -> 1;
                case "COL" -> 2;
                case "BOX" -> 3;
                default -> 4;
            };
        }));

        String currentType = "";
        
        for (Duplicate d : sortedList) {
            // Print separator line when type changes (ROW -> COL)
            if (!d.type().equals(currentType)) {
                if (!currentType.isEmpty()) {
                    System.out.println("------------------------------------------");
                }
                currentType = d.type();
            }

            // Output Format: ROW 1, #1, [1, 2, 3]
            System.out.printf("%s %d, #%d, %s%n", 
                d.type(), 
                d.id() + 1,       // Convert 0-based index to 1-based index
                d.value(),        // The duplicate value itself
                d.locations());   // The list of 1-based column/row/box indices
        }
    }
}