package game;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles logging of user actions and undo functionality.
 * Log file is stored in the "current" folder alongside the current game.
 * Format: (x, y, val, prev) where prev is the previous value.
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
     * Logs a user action from a UserAction object.
     */
    public void logAction(UserAction action) throws IOException {
        logAction(action.getRow(), action.getCol(), action.getNewValue(), action.getPreviousValue());
    }
    
    /**
     * Logs a simple string message.
     */
    public void logMessage(String message) throws IOException {
        Files.writeString(logFilePath, message + System.lineSeparator(), 
                         StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    /**
     * Gets the last action from the log file for undo purposes.
     * @return UserAction representing the last logged action, or null if log is empty
     * @throws IOException If reading fails
     */
    public UserAction getLastAction() throws IOException {
        if (!Files.exists(logFilePath)) {
            return null;
        }
        
        List<String> lines = Files.readAllLines(logFilePath);
        
        if (lines.isEmpty()) {
            return null;
        }
        
        String lastLine = lines.get(lines.size() - 1);
        return parseLogLine(lastLine);
    }
    
    /**
     * Parses a log line in format "(x, y, val, prev)" into a UserAction.
     */
    private UserAction parseLogLine(String line) {
        // Remove parentheses and split by comma
        line = line.trim().replace("(", "").replace(")", "");
        String[] parts = line.split(",");
        
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid log format: " + line);
        }
        
        int row = Integer.parseInt(parts[0].trim());
        int col = Integer.parseInt(parts[1].trim());
        int newValue = Integer.parseInt(parts[2].trim());
        int prevValue = Integer.parseInt(parts[3].trim());
        
        return new UserAction(row, col, newValue, prevValue);
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
     * Gets all actions from the log file.
     */
    public List<UserAction> getAllActions() throws IOException {
        List<UserAction> actions = new ArrayList<>();
        
        if (!Files.exists(logFilePath)) {
            return actions;
        }
        
        List<String> lines = Files.readAllLines(logFilePath);
        
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    actions.add(parseLogLine(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Skipping invalid log line: " + line);
                }
            }
        }
        
        return actions;
    }
}