package controller;

import game.SudokuBoard;

/**
 * Represents a Sudoku game on the controller side.
 * MUST ONLY BE USED ON CONTROLLER SIDE - NOT VIEWER SIDE.
 * This is a lightweight wrapper around the board array.
 * IMPORTANT: Uses references, not deep copies (as per lab spec).
 */
public class Game {
    private final int[][] board;
    
    /**
     * Constructs a Game instance wrapping the provided board.
     * IMPORTANT: This does NOT copy the board by value - it uses the reference.
     * @param board The 9x9 Sudoku board (reference, not copy)
     */
    public Game(int[][] board) {
        this.board = board;
    }
    
    /**
     * Gets the board reference.
     * @return The 9x9 board array
     */
    public int[][] getBoard() {
        return board;
    }
    
    /**
     * Gets the value at a specific cell.
     */
    public int getCell(int row, int col) {
        return board[row][col];
    }
    
    /**
     * Sets the value at a specific cell.
     */
    public void setCell(int row, int col, int value) {
        board[row][col] = value;
    }
    
    /**
     * Creates a Game from a SudokuBoard.
     */
    public static Game fromSudokuBoard(SudokuBoard sudokuBoard) {
        int[][] array = new int[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                array[r][c] = sudokuBoard.getCell(r, c);
            }
        }
        return new Game(array);
    }
    
    /**
     * Creates a SudokuBoard from this Game.
     */
    public SudokuBoard toSudokuBoard() {
        SudokuBoard sudokuBoard = new SudokuBoard(9);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                sudokuBoard.setCell(r, c, board[r][c]);
            }
        }
        return sudokuBoard;
    }
}