package game;

public class SudokuVerifier {

    private final SudokuBoard board;

    public SudokuVerifier(SudokuBoard board) {
        this.board = board;
    }

    /**
     * Verifies the Sudoku board using the single sequential mode.
     * The method no longer accepts a 'mode' parameter.
     * @return The ValidationResult containing status and any duplicates found.
     */
    public ValidationResult verify() {
        // ValidationResult now takes the board to check for 0s (INCOMPLETE).
        ValidationResult result = new ValidationResult(board);

        // Single Sequential Mode is now the only verification method.
        runSequential(result);

        return result;
    }

    /**
     * Runs row, column, and box checks sequentially on the main thread.
     */
    private void runSequential(ValidationResult result) {
        // Mode 0: sequential
        CheckerFactory.createChecker("row", board, result, 0, 9).run();
        CheckerFactory.createChecker("col", board, result, 0, 9).run();
        CheckerFactory.createChecker("box", board, result, 0, 9).run();
    }
    
    // All multithreading methods (runMode3, runMode27, executeThreads) are removed.
}