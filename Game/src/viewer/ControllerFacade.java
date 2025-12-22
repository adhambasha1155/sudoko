package viewer;

import controller.*;
import game.*;
import java.io.IOException;
import java.util.List;

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
        // Create a boolean array to track which cells are valid
        boolean[][] validity = new boolean[9][9];
        
        // Initialize all cells as valid (assume valid until proven otherwise)
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                validity[r][c] = true;
            }
        }
        
        try {
            // Create SudokuBoard from the game array
            SudokuBoard board = new SudokuBoard(9);
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    board.setCell(r, c, game[r][c]);
                }
            }
            
            // Run verification
            SudokuVerifier verifier = new SudokuVerifier(board);
            ValidationResult result = verifier.verify();
            
            // If there are duplicates, mark the cells involved as invalid
            List<ValidationResult.Duplicate> duplicates = result.getDuplicates();
            
            for (ValidationResult.Duplicate dup : duplicates) {
                String type = dup.type();
                int id = dup.id();
                int value = dup.value();
                List<Integer> locations = dup.locations();
                
                // Mark all cells involved in this duplicate as invalid
                for (int loc : locations) {
                    if (type.equals("ROW")) {
                        // For ROW duplicates:
                        // - id = row number (0-8)
                        // - loc = column number (0-8) where the duplicate value appears
                        validity[id][loc] = false;
                        
                    } else if (type.equals("COL")) {
                        // For COL duplicates:
                        // - id = column number (0-8)
                        // - loc = row number (0-8) where the duplicate value appears
                        validity[loc][id] = false;
                        
                    } else if (type.equals("BOX")) {
                        // For BOX duplicates:
                        // - id = box number (0-8)
                        // - loc = local index within the box (0-8)
                        // Need to convert to absolute row/col
                        int boxRow = (id / 3) * 3;  // Top-left row of the box
                        int boxCol = (id % 3) * 3;  // Top-left col of the box
                        int localRow = loc / 3;     // Row within the 3x3 box (0-2)
                        int localCol = loc % 3;     // Col within the 3x3 box (0-2)
                        int absRow = boxRow + localRow;
                        int absCol = boxCol + localCol;
                        validity[absRow][absCol] = false;
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error verifying game: " + e.getMessage());
            e.printStackTrace();
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
public int[] undo() throws IOException {
    return controller.undo();
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
    public void deleteCompletedGame(DifficultyEnum difficulty) throws IOException {
    controller.deleteCompletedGame(difficulty);
}
    public void clearLog() throws IOException {
    GameLogger logger = new GameLogger();
    logger.clearLog();
}
    /**
 * Gets the original (unsolved) puzzle for a difficulty level.
 */
public int[][] getOriginalGame(DifficultyEnum difficulty) throws NotFoundException {
    try {
        Game game = controller.getOriginalGame(difficulty);
        return game.getBoard();
    } catch (InvalidGameException e) {
        throw new NotFoundException("Original game file is corrupted: " + e.getMessage());
    }
}
public void saveOriginalGame(int[][] board, DifficultyEnum difficulty) throws InvalidGameException {
    Game game = new Game(board);
    controller.saveOriginalGame(game, difficulty);
}
}