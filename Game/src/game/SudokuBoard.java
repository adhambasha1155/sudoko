package game;

import java.io.File;                 // Represents a file on your computer
import java.io.FileNotFoundException; // Handles the error if the file is missing
import java.util.Scanner;            // A tool to read text files line-by-line

public class SudokuBoard {

    // 1. The memory storage. 
    // We use a 2D array [row][col] because a Sudoku is a grid.
    // 'final' means this board size/reference won't change after we create it.
    private final int[][] grid = new int[9][9];

    // 2. The Constructor.
    // When you create 'new SudokuBoard("data.csv")', it immediately loads the data.
    public SudokuBoard(String filePath) throws NotFoundException, InvalidGameException{
        loadFromCSV(filePath);
    }

    // 3. The Logic to read the file with validations.
    private void loadFromCSV(String filePath)  throws NotFoundException, InvalidGameException {
        
        try (Scanner scanner = new Scanner(new File(filePath))) {

            // Fill 9 rows (index 0 to 8)
            for (int row = 0; row < 9; row++) {
                
                // Check if the file actually has a line to read.
                if (!scanner.hasNextLine()) {
                   throw new InvalidGameException("CSV is missing row " + (row + 1));
                }

                // Read the whole line. Ex: "5,3,4,6,7,8,9,1,2"
                String line = scanner.nextLine();

                // Split the line by commas. Now we have an array of strings: ["5", "3", "4", ...]
                String[] numbers = line.split(",");

                // Loop through the 9 numbers in this row
                for (int col = 0; col < 9; col++) {
                    
                    // If the column is missing in the CSV, set default invalid value
                    if (col >= numbers.length) {
                        System.out.println("Warning: Missing column " + (col + 1) + " at row " + (row + 1));
                        grid[row][col] = 0; // default invalid
                        continue;
                    }

                    String cell = numbers[col].trim(); // remove spaces
                    int value;

                    try {
                        // Convert string to integer
                        value = Integer.parseInt(cell);

                        // Check if the number is valid for Sudoku (1-9)
                        if (value < 1 || value > 9) {
                            System.out.println("Warning: Invalid number " + value + " at row " + (row + 1) + " col " + (col + 1));
                            value = 0; // treat as invalid
                        }
                    } catch (NumberFormatException e) {
                        // Handle non-numeric values
                        System.out.println("Warning: Non-numeric value '" + cell + "' at row " + (row + 1) + " col " + (col + 1));
                        value = 0; // treat as invalid
                    }

                    // Store the value in the grid
                    grid[row][col] = value;
                }
            }

        } catch (FileNotFoundException e) {
            // If the file isn't found, print a helpful message and stop the program
           throw new NotFoundException("File not found: " + filePath);
        }
    }

    // 4. The Getter.
    // The grid is private (Encapsulation). This method allows other classes 
    // (like the Checkers) to ask "What number is at Row 0, Column 5?"
    public int getCell(int row, int col) {
        return grid[row][col];
    }

    // 5. A Helper to get the whole size (Good practice, though we know it's 9)
    //public int getSize() {
       // return 9;
    }