package viewer;

import controller.*;
import game.*;
import java.io.IOException;

/**
 * Facade that implements the Controllable interface (for the GUI/View).
 * Acts as an adapter between the View layer and the Controller layer (Viewable).
 * 
 * This class bridges the two incompatible interfaces:
 * - Controllable (uses int[][], char, String) - Viewer side
 * - Viewable (uses Game, DifficultyEnum, Catalog) - Controller side
 * 
 * Design Pattern: Adapter/Facade Pattern
 */
public class ControllerFacade implements Controllable {
    
    private final GameController controller;
    
    public ControllerFacade() {
        this.controller = new GameController();
    }
    
    @Override
    public boolean[] getCatalog() {
        Catalog catalog = controller.getCatalog();
        
        return new boolean[] {
            catalog.current,
            catalog.allModesExist
        };
    }
    
    @Override
    public int[][] getGame(char level) throws NotFoundException {
        DifficultyEnum difficulty = charToDifficulty(level);
        Game game = controller.getGame(difficulty);
        return game.getBoard();
    }
    
    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        try {
            SudokuBoard sourceBoard = new SudokuBoard(sourcePath);
            Game sourceGame = Game.fromSudokuBoard(sourceBoard);
            controller.driveGames(sourceGame);
            
        } catch (NotFoundException e) {
            throw new SolutionInvalidException("Source file not found: " + sourcePath);
        } catch (InvalidGameException e) {
            throw new SolutionInvalidException("Source file is invalid: " + e.getMessage());
        }
    }
    
    @Override
    public boolean[][] verifyGame(int[][] game) {
        Game gameObj = new Game(game);
        String status = controller.verifyGame(gameObj);
        
        boolean[][] validity = new boolean[9][9];
        
        if (status.equals("VALID") || status.equals("INCOMPLETE")) {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    validity[r][c] = true;
                }
            }
        } else {
            try {
                SudokuBoard board = new SudokuBoard(9);
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        board.setCell(r, c, game[r][c]);
                    }
                }
                
                SudokuVerifier verifier = new SudokuVerifier(board);
                ValidationResult result = verifier.verify();
                
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        validity[r][c] = true;
                    }
                }
                
            } catch (Exception e) {
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        validity[r][c] = true;
                    }
                }
            }
        }
        
        return validity;
    }
    
    @Override
    public int[][] solveGame(int[][] game) throws InvalidGameException {
        Game gameObj = new Game(game);
        int[] solution = controller.solveGame(gameObj);
        
        int numCells = solution.length / 3;
        int[][] result = new int[numCells][3];
        
        for (int i = 0; i < numCells; i++) {
            result[i][0] = solution[i * 3];
            result[i][1] = solution[i * 3 + 1];
            result[i][2] = solution[i * 3 + 2];
        }
        
        return result;
    }
    
    @Override
    public void logUserAction(UserAction userAction) throws IOException {
        // ControllerFacade is on VIEWER side, so it CAN use UserAction
        // Convert UserAction to simple values for the backend logger
        GameLogger logger = new GameLogger();
        logger.logAction(
            userAction.getRow(), 
            userAction.getCol(), 
            userAction.getNewValue(), 
            userAction.getPreviousValue()
        );
        
        // Also log via controller as a string message
        String logString = userAction.toLogFormat();
        controller.logUserAction(logString);
    }
    
    /**
     * Additional helper method for GUI to save current game state.
     */
    public void saveCurrentGame(int[][] board) throws InvalidGameException {
        Game game = new Game(board);
        controller.saveCurrentGame(game);
    }
    
    /**
     * Additional helper method for GUI to perform undo.
     * @return The action that was undone (UserAction for viewer side), or null if nothing to undo
     */
    public UserAction undo() throws IOException {
        // Get the last log entry from backend
        GameLogger logger = new GameLogger();
        String lastEntry = logger.getLastLogEntry();
        
        if (lastEntry == null) {
            return null; // Nothing to undo
        }
        
        // Parse it using GameLogger's method (returns int[])
        int[] actionData = logger.parseLogEntry(lastEntry);
        
        // Remove the entry
        logger.removeLastAction();
        
        // Convert int[] to UserAction (viewer-side conversion)
        // actionData = [row, col, newValue, previousValue]
        return new UserAction(
            actionData[0],  // row
            actionData[1],  // col
            actionData[2],  // newValue
            actionData[3]   // previousValue
        );
    }
    
    /**
     * Converts char level to DifficultyEnum.
     */
    private DifficultyEnum charToDifficulty(char level) throws NotFoundException {
        return switch (Character.toLowerCase(level)) {
            case 'e' -> DifficultyEnum.EASY;
            case 'm' -> DifficultyEnum.MEDIUM;
            case 'h' -> DifficultyEnum.HARD;
            case 'c' -> DifficultyEnum.CURRENT;
            default -> throw new NotFoundException("Invalid difficulty level: " + level);
        };
    }
}