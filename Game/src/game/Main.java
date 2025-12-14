package game;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        // 1. Check Input Arguments
        if (args.length < 1) {
            System.out.println("Error: Missing arguments.");
            System.out.println("Usage: java -jar SudokuVerifier.jar <csv-path>");
            System.exit(1);
        }

        String filePath = args[0];

        // 2. Validate File Path existence (Optional here, but good for fast feedback)
        File f = new File(filePath);
        if (!f.exists() || f.isDirectory()) {
            System.out.println("Error: File not found at " + filePath);
            System.exit(1);
        }

        System.out.println("Running Sudoku Verifier...");
        System.out.println("File: " + filePath);
        System.out.println("Mode: Sequential (Fixed)");
        System.out.println("-----------------------------------");

        long startTime = System.currentTimeMillis();

        // 3. Run Logic INSIDE a try-catch block
        try {
            SudokuBoard board = new SudokuBoard(filePath);
            SudokuVerifier verifier = new SudokuVerifier(board);
            ValidationResult result = verifier.verify();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            result.printDuplicates();

            System.out.println("\n[Execution Stats]");
            System.out.println("Mode: Sequential (Fixed)");
            System.out.println("Time: " + duration + " ms");

        } catch (NotFoundException e) {
            // Handle the missing file gracefully
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (InvalidGameException e) {
            // Handle the corrupted CSV gracefully
            System.err.println("Game Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.err.println("An unexpected error occurred.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}