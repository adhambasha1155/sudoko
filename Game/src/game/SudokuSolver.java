package game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Solves a Sudoku puzzle using permutation testing.
 * Only works when there are exactly 5 empty cells.
 * 
 * Design Patterns Used:
 * - Iterator Pattern: PermutationIterator generates combinations on-the-fly
 * - Flyweight Pattern: Verifies boards without copying (just overlays values)
 * - Parallelism: Uses multiple threads to test permutations concurrently
 */
public class SudokuSolver {
    
    private static final int REQUIRED_EMPTY_CELLS = 5;
    private static final int NUM_THREADS = 4; // Number of worker threads
    
    /**
     * Solves the puzzle by finding values for all empty cells.
     * @param board The board with exactly 5 empty cells (value 0)
     * @return Array of solutions: [row1, col1, val1, row2, col2, val2, ...]
     *         Total length = 15 (5 cells × 3 values each)
     * @throws InvalidGameException If the board doesn't have exactly 5 empty cells or cannot be solved
     */
    public int[] solve(SudokuBoard board) throws InvalidGameException {
        // 1. Find all empty cells
        List<int[]> emptyCells = findEmptyCells(board);
        
        // 2. Validate we have exactly 5 empty cells
        if (emptyCells.size() != REQUIRED_EMPTY_CELLS) {
            throw new InvalidGameException(
                "Solver requires exactly 5 empty cells, found: " + emptyCells.size()
            );
        }
        
        // 3. Use permutation iterator + parallel search
        int[] solution = solveWithParallelism(board, emptyCells);
        
        if (solution == null) {
            throw new InvalidGameException("No valid solution found for this puzzle");
        }
        
        return solution;
    }
    
    /**
     * Finds all empty cells (cells with value 0) in the board.
     * @return List of [row, col] pairs
     */
    private List<int[]> findEmptyCells(SudokuBoard board) {
        List<int[]> emptyCells = new ArrayList<>();
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col) == 0) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }
        
        return emptyCells;
    }
    
    /**
     * Solves using parallelism - multiple threads test permutations concurrently.
     * Uses atomic variables to coordinate between threads.
     */
    private int[] solveWithParallelism(SudokuBoard board, List<int[]> emptyCells) {
        PermutationIterator iterator = new PermutationIterator(REQUIRED_EMPTY_CELLS);
        
        // Shared state between threads (thread-safe)
        AtomicBoolean solutionFound = new AtomicBoolean(false);
        AtomicReference<int[]> solution = new AtomicReference<>(null);
        
        // Create worker threads
        List<Thread> workers = new ArrayList<>();
        
        for (int i = 0; i < NUM_THREADS; i++) {
            Thread worker = new Thread(() -> {
                // Each thread tests permutations until solution is found
                while (!solutionFound.get() && iterator.hasNext()) {
                    int[] permutation;
                    
                    // Get next permutation (synchronized internally by iterator)
                    synchronized (iterator) {
                        if (!iterator.hasNext()) break;
                        permutation = iterator.next();
                    }
                    
                    // Test this permutation using Flyweight pattern
                    if (isValidSolution(board, emptyCells, permutation)) {
                        // Found a solution!
                        if (solutionFound.compareAndSet(false, true)) {
                            // This thread found it first
                            solution.set(createSolutionArray(emptyCells, permutation));
                        }
                        break;
                    }
                }
            });
            
            workers.add(worker);
            worker.start();
        }
        
        // Wait for all workers to finish
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return solution.get();
    }
    
    /**
     * FLYWEIGHT PATTERN: Verifies if a permutation is valid WITHOUT copying the board.
     * Instead of modifying the board, we create a lightweight wrapper (FlyweightBoard)
     * that "overlays" the test values when getCell() is called.
     * 
     * @param board The original board (not modified)
     * @param emptyCells The positions of empty cells
     * @param values The values to test at those positions
     * @return true if this combination makes a valid Sudoku board
     */
    private boolean isValidSolution(SudokuBoard board, List<int[]> emptyCells, int[] values) {
        FlyweightBoardVerifier verifier = new FlyweightBoardVerifier(board, emptyCells, values);
        return verifier.isValid();
    }
    
    /**
     * Creates the solution array in the format: [row1, col1, val1, row2, col2, val2, ...]
     */
    private int[] createSolutionArray(List<int[]> emptyCells, int[] values) {
        int[] solution = new int[REQUIRED_EMPTY_CELLS * 3];
        
        for (int i = 0; i < REQUIRED_EMPTY_CELLS; i++) {
            solution[i * 3] = emptyCells.get(i)[0];      // row
            solution[i * 3 + 1] = emptyCells.get(i)[1];  // col
            solution[i * 3 + 2] = values[i];              // value
        }
        
        return solution;
    }
    
    /**
     * FLYWEIGHT PATTERN IMPLEMENTATION:
     * A lightweight wrapper that overlays test values on the board without copying.
     * This allows us to verify different permutations efficiently.
     */
    private static class FlyweightBoard extends SudokuBoard {
        private final SudokuBoard baseBoard;
        private final List<int[]> emptyCells;
        private final int[] overlayValues;
        
        public FlyweightBoard(SudokuBoard baseBoard, List<int[]> emptyCells, int[] overlayValues) {
            super(9); // Initialize empty board
            this.baseBoard = baseBoard;
            this.emptyCells = emptyCells;
            this.overlayValues = overlayValues;
        }
        
        /**
         * Overrides getCell to return overlay values for empty cells.
         * This is the key to the Flyweight pattern - we don't copy the board,
         * we just intercept getCell() calls.
         */
        @Override
        public int getCell(int row, int col) {
            // Check if this is one of the empty cells we're testing
            for (int i = 0; i < emptyCells.size(); i++) {
                if (emptyCells.get(i)[0] == row && emptyCells.get(i)[1] == col) {
                    return overlayValues[i]; // Use test value
                }
            }
            // Not an empty cell, use original value from base board
            return baseBoard.getCell(row, col);
        }
    }
    
    /**
     * Verifies if a permutation creates a valid board using existing checker classes.
     * This reuses RowChecker, ColChecker, and BoxChecker.
     */
    private static class FlyweightBoardVerifier {
        private final FlyweightBoard board;
        
        public FlyweightBoardVerifier(SudokuBoard baseBoard, List<int[]> emptyCells, int[] overlayValues) {
            this.board = new FlyweightBoard(baseBoard, emptyCells, overlayValues);
        }
        
        /**
         * Checks if the board (with overlaid values) is valid.
         * Uses the existing checker classes (RowChecker, ColChecker, BoxChecker).
         */
        public boolean isValid() {
            // Create a validation result to collect any errors
            ValidationResult result = new ValidationResult(board);
            
            // Use existing checkers - they work on SudokuBoard interface
            CheckerFactory.createChecker("row", board, result, 0, 9).run();
            if (!result.isValid() && result.getStatus().equals("INVALID")) {
                return false; // Found duplicates in rows
            }
            
            CheckerFactory.createChecker("col", board, result, 0, 9).run();
            if (!result.isValid() && result.getStatus().equals("INVALID")) {
                return false; // Found duplicates in columns
            }
            
            CheckerFactory.createChecker("box", board, result, 0, 9).run();
            if (!result.isValid() && result.getStatus().equals("INVALID")) {
                return false; // Found duplicates in boxes
            }
            
            // Check if the board is complete (no zeros after overlay)
            // If status is INCOMPLETE, it means there are still zeros,
            // which shouldn't happen after we overlay all 5 values
            return result.isValid();
        }
    }
}