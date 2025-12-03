package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 1. BASE CHECKER (Parent)
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

    @Override
    public void run() {
        for (int i = startIdx; i < endIdx; i++) {
            check(i);
        }
    }

    protected abstract void check(int id);

    // Shared helper to find duplicates in a list of numbers
    // 'values' maps: Position Index -> Value at that position
    protected void validateSection(String type, int id, Map<Integer, Integer> indexToValueMap) {
        // Map: Value (1-9) -> List of Indices where it appeared
        Map<Integer, List<Integer>> valueLocations = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : indexToValueMap.entrySet()) {
            int index = entry.getKey();
            int val = entry.getValue();

            if (val < 1 || val > 9) continue; // Skip invalid numbers

            valueLocations.putIfAbsent(val, new ArrayList<>());
            valueLocations.get(val).add(index + 1); // Store as 1-based index for output
        }

        // Check for duplicates
        for (Map.Entry<Integer, List<Integer>> entry : valueLocations.entrySet()) {
            if (entry.getValue().size() > 1) {
                // Found a duplicate! Add to result.
                result.addDuplicate(type, id, entry.getKey(), entry.getValue());
            }
        }
    }
}