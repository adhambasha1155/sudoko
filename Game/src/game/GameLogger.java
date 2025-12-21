package game;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Handles logging of user actions and undo functionality.
 * Log file is stored in the "current" folder alongside the current game.
 * Format: (x, y, val, prev) where prev is the previous value.
 * 
 * IMPORTANT: This class is in the game package (backend) and does NOT use
 * UserAction (which is in the viewer package). It only works with strings
 * and simple arrays to maintain architectural boundaries.
 */
public class GameLogger {
    
    private static final String BASE_DIR = "SudokuGames";
    private static final String CURRENT_DIR = "current";
    private static final String LOG_FILE_NAME = "game.log";
    
    private final Path logFilePath;
    
    public GameLogger() {
        this.logFilePath = Path.of(BASE_DIR, CURRENT_DIR, LOG_FILE_NAME);
        ensureLogFileExists();
    }
    
    /**
     * Ensures the log file exists. Creates it if it doesn't.
     */
    private void ensureLogFileExists() {
        try {
            Files.createDirectories(logFilePath.getParent());
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create log file: " + e.getMessage());
        }
    }
    
    /**
     * Logs a user action immediately (append mode).
     * Format: (x, y, val, prev)
     * 
     * @param row The row index (0-8)
     * @param col The column index (0-8)
     * @param newValue The new value entered
     * @param previousValue The previous value that was there
     * @throws IOException If writing fails
     */
    public void logAction(int row, int col, int newValue, int previousValue) throws IOException {
        String logEntry = String.format("(%d, %d, %d, %d)%n", row, col, newValue, previousValue);
        
        // Append to file immediately
        Files.writeString(logFilePath, logEntry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    /**
     * Logs a simple string message.
     */
    public void logMessage(String message) throws IOException {
        Files.writeString(logFilePath, message + System.lineSeparator(), 
                         StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    /**
     * Gets the last log entry as a raw string.
     * @return The last line from the log file, or null if empty
     */
    public String getLastLogEntry() throws IOException {
        if (!Files.exists(logFilePath)) {
            return null;
        }
        
        List<String> lines = Files.readAllLines(logFilePath);
        
        if (lines.isEmpty()) {
            return null;
        }
        
        return lines.get(lines.size() - 1);
    }
    
    /**
     * Parses a log entry string and returns the action data as an int array.
     * Format: "(row, col, newVal, prevVal)" → [row, col, newVal, prevVal]
     * 
     * @param logEntry The log entry string to parse
     * @return int array [row, col, newValue, previousValue]
     */
    public int[] parseLogEntry(String logEntry) {
        // Remove parentheses and split by comma
        String cleaned = logEntry.trim().replace("(", "").replace(")", "");
        String[] parts = cleaned.split(",");
        
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid log format: " + logEntry);
        }
        
        int row = Integer.parseInt(parts[0].trim());
        int col = Integer.parseInt(parts[1].trim());
        int newValue = Integer.parseInt(parts[2].trim());
        int prevValue = Integer.parseInt(parts[3].trim());
        
        return new int[] { row, col, newValue, prevValue };
    }
    
    /**
     * Removes the last line from the log file (for undo).
     * @throws IOException If file operations fail
     */
    public void removeLastAction() throws IOException {
        if (!Files.exists(logFilePath)) {
            return;
        }
        
        List<String> lines = Files.readAllLines(logFilePath);
        
        if (lines.isEmpty()) {
            return;
        }
        
        // Remove the last line
        lines.remove(lines.size() - 1);
        
        // Write back all remaining lines
        Files.write(logFilePath, lines, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    /**
     * Clears the entire log file.
     * Used when starting a new game or when a game is completed.
     */
    public void clearLog() throws IOException {
        if (Files.exists(logFilePath)) {
            Files.writeString(logFilePath, "", StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
    
    /**
     * Checks if the log file exists and has content.
     */
    public boolean hasActions() {
        try {
            return Files.exists(logFilePath) && Files.size(logFilePath) > 0;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Gets all log entries as strings.
     * @return List of log entry strings
     */
    public List<String> getAllLogEntries() throws IOException {
        if (!Files.exists(logFilePath)) {
            return List.of();
        }
        
        return Files.readAllLines(logFilePath);
    }
}