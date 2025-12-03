package game;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ValidationResult {

    // Updated Record: Now stores 'locations' (the list of indices where the dupe appears)
    public record Duplicate(String type, int id, int value, List<Integer> locations) {}

    // Thread-safe list
    private final List<Duplicate> duplicates = new CopyOnWriteArrayList<>();

    public void addDuplicate(String type, int id, int value, List<Integer> locations) {
        duplicates.add(new Duplicate(type, id, value, locations));
    }

    public boolean isValid() {
        return duplicates.isEmpty();
    }

    public void printDuplicates() {
        if (isValid()) {
            System.out.println("VALID");
            return;
        }

        System.out.println("INVALID");

        // Convert to a standard list to sort it for the report
        List<Duplicate> sortedList = new ArrayList<>(duplicates);
        
        // Sort to ensure Rows print first, then Cols, then Boxes (nice to have)
        // Custom sort: ROW -> COL -> BOX
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
                d.id() + 1,       // Convert 0-index to 1-index
                d.value(), 
                d.locations().toString()
            );
        }
    }
}