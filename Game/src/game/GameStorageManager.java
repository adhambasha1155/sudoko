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

 
    private void createDirectory(String difficulty) throws IOException {
        Path dirPath = basePath.resolve(difficulty.toLowerCase());
        Files.createDirectories(dirPath);
    }
    
  // hfhmha ba3deen
    private String getFilePath(String difficulty) {
        return basePath
            .resolve(difficulty.toLowerCase())
            .resolve(BOARD_FILE_NAME)
            .toString();
    }
    
   
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
    
    public SudokuBoard loadBoard(String difficulty) throws NotFoundException, InvalidGameException {
        // We rely on the SudokuBoard constructor to handle the actual file reading 
        // and throw the appropriate exceptions (FileNotFound/NotFound, parsing errors/InvalidGame)
        String filePath = getFilePath(difficulty);
        
        // Ensure the SudokuBoard constructor has been updated to throw exceptions!
        return new SudokuBoard(filePath);
    }
    public GameCatalog getGameCatalog() {
    // Helper to check if the board.csv file exists in the specified difficulty folder
    // Note: This relies on the file naming convention defined in GameStorageManager.
    
    boolean easy = Files.exists(Path.of(getFilePath(DifficultyConstants.EASY)));
    boolean medium = Files.exists(Path.of(getFilePath(DifficultyConstants.MEDIUM)));
    boolean hard = Files.exists(Path.of(getFilePath(DifficultyConstants.HARD)));
    boolean current = Files.exists(Path.of(getFilePath(DifficultyConstants.CURRENT)));
    
    return new GameCatalog(easy, medium, hard, current);
}
}
