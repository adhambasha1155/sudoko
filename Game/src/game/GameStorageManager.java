package game;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles all file system operations for saving and loading Sudoku board states.
 * Creates the required difficulty folders.
 */
public class GameStorageManager {

    // Base directory where all game files will be stored relative to the application's starting point.
    private static final String BASE_DIR_NAME = "SudokuGames";
    private static final String BOARD_FILE_NAME = "board.csv";
    
    // The Path object for the base directory
    private final Path basePath;

    public GameStorageManager() {
        this.basePath = Path.of(BASE_DIR_NAME);
        setupDirectories();
    }

    /**
     * Creates the base directory and the required subdirectories (easy, medium, hard, current).
     */
    private void setupDirectories() {
        try {
            // Create the root directory if it doesn't exist
            Files.createDirectories(basePath);
            
            // Create the subdirectories using the constants from the user's file
            // NOTE: The class is spelled DifficutyConstants.java in your files.
            createDirectory(DifficultyConstants.EASY);
            createDirectory(DifficultyConstants.MEDIUM);
            createDirectory(DifficultyConstants.HARD);
            createDirectory(DifficultyConstants.CURRENT);
            
        } catch (IOException e) {
            System.err.println("Error setting up game directories: " + e.getMessage());
            // An error here is critical, so we print and throw a runtime exception.
            throw new RuntimeException("Failed to set up game storage directories.", e);
        }
    }

    /**
     * Helper to create a single directory for a given difficulty level.
     */
    private void createDirectory(String difficulty) throws IOException {
        Path dirPath = basePath.resolve(difficulty.toLowerCase());
        Files.createDirectories(dirPath);
    }
    
    /**
     * Gets the full file path for a given difficulty level.
     * Example: SudokuGames/hard/board.csv
     */
    private String getFilePath(String difficulty) {
        return basePath
            .resolve(difficulty.toLowerCase())
            .resolve(BOARD_FILE_NAME)
            .toString();
    }
    
    /**
     * Saves the current state of a SudokuBoard to a file, associated with a difficulty level.
     * @param board The board to save.
     * @param difficulty The difficulty level (EASY, MEDIUM, HARD, CURRENT).
     * @throws InvalidGameException If there's an issue writing the file.
     */
    public void saveBoard(SudokuBoard board, String difficulty) throws InvalidGameException {
        String filePath = getFilePath(difficulty);
        Path savePath = Path.of(filePath);

        // 1. Convert the 9x9 grid into a single CSV string
        StringBuilder csvContent = new StringBuilder();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                csvContent.append(board.getCell(r, c));
                if (c < 8) {
                    csvContent.append(",");
                }
            }
            if (r < 8) {
                csvContent.append("\n");
            }
        }
        
        // 2. Write the string to the file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvContent.toString());
            // System.out.println("Game saved successfully to: " + savePath.toAbsolutePath());
        } catch (IOException e) {
            throw new InvalidGameException("Failed to save game file to " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Loads a saved SudokuBoard from a file associated with a difficulty level.
     * @param difficulty The difficulty level (EASY, MEDIUM, HARD, CURRENT).
     * @return A new SudokuBoard loaded from the file.
     * @throws NotFoundException If the file is missing.
     * @throws InvalidGameException If the file exists but is corrupted/malformed.
     */
    public SudokuBoard loadBoard(String difficulty) throws NotFoundException, InvalidGameException {
        // We rely on the SudokuBoard constructor to handle the actual file reading 
        // and throw the appropriate exceptions (FileNotFound/NotFound, parsing errors/InvalidGame)
        String filePath = getFilePath(difficulty);
        
        // Ensure the SudokuBoard constructor has been updated to throw exceptions!
        return new SudokuBoard(filePath);
    }
}
