package game;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        // 1. Check Input Arguments
        if (args.length < 2) {
            System.out.println("Error: Missing arguments.");
            System.out.println("Usage: java -jar SudokuVerifier.jar <csv-path> <mode>");
            System.exit(1);
        }

        String filePath = args[0];
        int mode = 0;

        // 2. Validate Mode
        try {
            mode = Integer.parseInt(args[1]);
            if (mode != 0 && mode != 3 && mode != 27) {
                System.out.println("Error: Mode must be 0, 3, or 27.");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Mode must be a number (0, 3, 27).");
            System.exit(1);
        }

        // 3. Validate File
        File f = new File(filePath);
        if (!f.exists() || f.isDirectory()) {
            System.out.println("Error: File not found at " + filePath);
            System.exit(1);
        }

        System.out.println("Running Sudoku Verifier...");
        System.out.println("File: " + filePath);
        System.out.println("Mode: " + mode);
        System.out.println("-----------------------------------");

        // 4. Start Timer (Required for Report)
        long startTime = System.currentTimeMillis();

        // 5. Run Logic
        SudokuBoard board = new SudokuBoard(filePath);
        SudokuVerifier verifier = new SudokuVerifier(board);
        ValidationResult result = verifier.verify(mode);

        // 6. Stop Timer
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 7. Print Validation Report (Valid/Invalid + Locations)
        // Make sure this matches the method name in your ValidationResult class
        result.printDuplicates();

        // 8. Print Execution Stats (Required for Comparison)
        System.out.println("\n[Execution Stats]");
        System.out.println("Mode: " + mode);
        System.out.println("Time: " + duration + " ms");
    }
}