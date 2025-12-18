package game;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles all file system operations for saving and loading Sudoku board states.
 * Creates the required difficulty folders including the "current" folder for incomplete games.
 */
public class GameStorageManager {

    private static final String BASE_DIR_NAME = "SudokuGames";
    private static final String BOARD_FILE_NAME = "board.csv";
    
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
            Files.createDirectories(basePath);
            
            createDirectory(DifficultyConstants.EASY);
            createDirectory(DifficultyConstants.MEDIUM);
            createDirectory(DifficultyConstants.HARD);
            createDirectory(DifficultyConstants.CURRENT);
            
        } catch (IOException e) {
            System.err.println("Error setting up game directories: " + e.getMessage());
            throw new RuntimeException("Failed to set up game storage directories.", e);
        }
    }

    private void createDirectory(String difficulty) throws IOException {
        Path dirPath = basePath.resolve(difficulty.toLowerCase());
        Files.createDirectories(dirPath);
    }
    
    private String getFilePath(String difficulty) {
        return basePath
            .resolve(difficulty.toLowerCase())
            .resolve(BOARD_FILE_NAME)
            .toString();
    }
    
    /**
     * Saves a board to the specified difficulty folder.
     */
    public void saveBoard(SudokuBoard board, String difficulty) throws InvalidGameException {
        String filePath = getFilePath(difficulty);
        
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
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(csvContent.toString());
        } catch (IOException e) {
            throw new InvalidGameException("Failed to save game file to " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Loads a board from the specified difficulty folder.
     */
    public SudokuBoard loadBoard(String difficulty) throws NotFoundException, InvalidGameException {
        String filePath = getFilePath(difficulty);
        return new SudokuBoard(filePath);
    }
    
    /**
     * Gets the game catalog showing which games are available.
     */
    public GameCatalog getGameCatalog() {
        boolean easy = Files.exists(Path.of(getFilePath(DifficultyConstants.EASY)));
        boolean medium = Files.exists(Path.of(getFilePath(DifficultyConstants.MEDIUM)));
        boolean hard = Files.exists(Path.of(getFilePath(DifficultyConstants.HARD)));
        boolean current = Files.exists(Path.of(getFilePath(DifficultyConstants.CURRENT)));
        
        return new GameCatalog(easy, medium, hard, current);
    }
    
    /**
     * Deletes a game file for the specified difficulty.
     * Used when a game is completed and verified as VALID.
     */
    public void deleteGame(String difficulty) throws IOException {
        Path filePath = Path.of(getFilePath(difficulty));
        
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
    
    /**
     * Saves the current game state to the "current" folder.
     * This is called whenever the user makes a move.
     */
    public void saveCurrentGame(SudokuBoard board) throws InvalidGameException {
        saveBoard(board, DifficultyConstants.CURRENT);
    }
    
    /**
     * Loads the current/incomplete game.
     */
    public SudokuBoard loadCurrentGame() throws NotFoundException, InvalidGameException {
        return loadBoard(DifficultyConstants.CURRENT);
    }
    
    /**
     * Deletes the current/incomplete game.
     * Called when the game is completed.
     */
    public void deleteCurrentGame() throws IOException {
        deleteGame(DifficultyConstants.CURRENT);
    }
}