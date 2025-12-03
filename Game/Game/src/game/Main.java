package game;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        // 1. Check Input Arguments (Updated for Single Sequential Mode)
        if (args.length < 1) {
            System.out.println("Error: Missing arguments.");
            System.out.println("Usage: java -jar SudokuVerifier.jar <csv-path>");
            System.exit(1);
        }

        String filePath = args[0];
        // int mode = 0; // Mode variable is no longer needed

        // 2. Removed: Mode validation logic

        // 3. Validate File
        File f = new File(filePath);
        if (!f.exists() || f.isDirectory()) {
            System.out.println("Error: File not found at " + filePath);
            System.exit(1);
        }

        System.out.println("Running Sudoku Verifier...");
        System.out.println("File: " + filePath);
        System.out.println("Mode: Sequential (Fixed)"); // Mode is now fixed
        System.out.println("-----------------------------------");

        // 4. Start Timer (Required for Report)
        long startTime = System.currentTimeMillis();

        // 5. Run Logic
        SudokuBoard board = new SudokuBoard(filePath);
        SudokuVerifier verifier = new SudokuVerifier(board);
        // Updated: Removed the 'mode' argument
        ValidationResult result = verifier.verify();

        // 6. Stop Timer
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 7. Print Validation Report (Valid/Invalid/Incomplete + Locations)
        result.printDuplicates();

        // 8. Print Execution Stats (Required for Comparison)
        System.out.println("\n[Execution Stats]");
        System.out.println("Mode: Sequential (Fixed)");
        System.out.println("Time: " + duration + " ms");
    }
}