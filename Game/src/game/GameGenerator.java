package game;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generates a valid Sudoku puzzle by creating a solved board and then 
 * removing a number of cells based on the difficulty level.
 */
public class GameGenerator {

    private final Random random = new Random();

    /**
     * Generates a new Sudoku puzzle (unsolved board) for the specified difficulty.
     * @param difficulty The difficulty level (e.g., DifficultyConstants.EASY).
     * @return A new SudokuBoard instance containing the puzzle.
     * @throws InvalidGameException If the board generation fails (highly unlikely with backtracking).
     */
    public SudokuBoard generateBoard(String difficulty) throws InvalidGameException {
        // 1. Create a new, empty board
        SudokuBoard solvedBoard = new SudokuBoard(9);
        
        // 2. Fill the board completely and validly using backtracking
        if (!fillBoard(solvedBoard, 0, 0)) {
            throw new InvalidGameException("Failed to generate a fully solved Sudoku board.");
        }
        
        // 3. Determine how many cells to remove based on difficulty
        int cellsToRemove = getCellsToRemove(difficulty);
        
        // 4. Create the final puzzle by removing cells from the solved board
        SudokuBoard puzzleBoard = removeCells(solvedBoard, cellsToRemove);
        
        return puzzleBoard;
    }

    /**
     * Determines the number of cells to remove based on difficulty constants.
     * CORRECTED VALUES per lab requirements:
     * - Easy: 10 cells
     * - Medium: 20 cells  
     * - Hard: 25 cells
     */
    private int getCellsToRemove(String difficulty) {
        return switch (difficulty) {
            case DifficultyConstants.EASY -> 10;     // FIXED: was 20
            case DifficultyConstants.MEDIUM -> 20;   // FIXED: was 30
            case DifficultyConstants.HARD -> 25;     // FIXED: was 40
            default -> throw new IllegalArgumentException("Unknown difficulty level: " + difficulty);
        };
    }

    /**
     * Creates the final puzzle by removing N unique cells from the solved board.
     * @param solvedBoard The fully solved 9x9 board.
     * @param cellsToRemove The number of cells to set to 0.
     * @return The puzzle board.
     */
    private SudokuBoard removeCells(SudokuBoard solvedBoard, int cellsToRemove) {
        // Deep copy of the solved board
        SudokuBoard puzzleBoard = new SudokuBoard(solvedBoard);
        
        // FIXED: Use the corrected RandomPairs API
        RandomPairs randomPairs = new RandomPairs();
        List<int[]> pairsToRemove = randomPairs.generateDistinctPairs(cellsToRemove);
        
        for (int[] pair : pairsToRemove) {
            int row = pair[0];  // x coordinate
            int col = pair[1];  // y coordinate
            puzzleBoard.setCell(row, col, 0); // Set the cell value to 0 (empty)
        }
        
        return puzzleBoard;
    }

    /**
     * The Backtracking Algorithm: Recursively fills the board to create a solved state.
     */
    private boolean fillBoard(SudokuBoard board, int row, int col) {
        // 1. Base Case: If we've passed the last column, move to the next row.
        if (col == 9) {
            row++;
            col = 0;
            // 1b. Base Case: If we've passed the last row, the board is complete.
            if (row == 9) {
                return true;
            }
        }
        
        // 2. Skip filled cells
        if (board.getCell(row, col) != 0) {
            return fillBoard(board, row, col + 1);
        }
        
        // 3. Try numbers 1 through 9 in a random order to ensure variety
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers, random);
        
        for (int num : numbers) {
            // 4. If the number is valid in the current spot...
            if (isValidPlacement(board, row, col, num)) {
                board.setCell(row, col, num);
                
                if (fillBoard(board, row, col + 1)) {
                    return true;
                }
                
                // Backtrack
                board.setCell(row, col, 0);
            }
        }
        
        return false;
    }
    
    /**
     * Checks if placing 'num' at (row, col) is valid based on Sudoku rules.
     */
    private boolean isValidPlacement(SudokuBoard board, int row, int col, int num) {
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (board.getCell(row, i) == num || board.getCell(i, col) == num) {
                return false;
            }
        }

        // Check 3x3 box
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (board.getCell(r, c) == num) {
                    return false;
                }
            }
        }
        return true;
    }
}