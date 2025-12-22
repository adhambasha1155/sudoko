package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


abstract class BaseChecker implements Runnable {
    protected final SudokuBoard board;
    protected final ValidationResult result;
    protected final int startIdx;
    protected final int endIdx;

    public BaseChecker(SudokuBoard board, ValidationResult result, int startIdx, int endIdx) {
        this.board = board;
        this.result = result;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
    }
//thread
    @Override
    public void run() {
        for (int i = startIdx; i < endIdx; i++) {
            check(i);
        }
    }
//polymorphism 
    protected abstract void check(int id);

    protected void validateSection(String type, int id, Map<Integer, Integer> indexToValueMap) {
        // Map: Value (1-9) -> List of Indices where it appeared
        Map<Integer, List<Integer>> valueLocations = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : indexToValueMap.entrySet()) {
            int index = entry.getKey();
            int val = entry.getValue();

            // Updated comment: Skip 0 (INCOMPLETE state) and any other invalid numbers (e.g., > 9).
            // These values should not be counted as duplicates.
            if (val < 1 || val > 9) continue; 

            valueLocations.putIfAbsent(val, new ArrayList<>());
            valueLocations.get(val).add(index ); // Store as 1-based index for output
        }

        // Check for duplicates
        for (Map.Entry<Integer, List<Integer>> entry : valueLocations.entrySet()) {
            if (entry.getValue().size() > 1) {
                // Duplicate found! Add to the thread-safe result list.
                result.addDuplicate(type, id, entry.getKey(), entry.getValue());
            }
        }
    }
}