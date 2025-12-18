package game;

import java.io.IOException;

/**
 * Facade that implements the Controllable interface (for the GUI/View).
 * Acts as an adapter between the View layer and the Controller layer (Viewable).
 * 
 * This class bridges the two incompatible interfaces:
 * - Controllable (uses int[][], char, String)
 * - Viewable (uses Game, DifficultyEnum, Catalog)
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
        // Call controller's getCatalog which returns Catalog object
        Catalog catalog = controller.getCatalog();
        
        // Convert Catalog to boolean array
        return new boolean[] {
            catalog.current,        // [0] = has current game
            catalog.allModesExist   // [1] = all modes exist
        };
    }
    
    @Override
    public int[][] getGame(char level) throws NotFoundException {
        // Convert char to DifficultyEnum
        DifficultyEnum difficulty = charToDifficulty(level);
        
        // Call controller to get Game
        Game game = controller.getGame(difficulty);
        
        // Convert Game to int[][]
        return game.getBoard();
    }
    
    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        try {
            // Load the source solution from file path
            SudokuBoard sourceBoard = new SudokuBoard(sourcePath);
            
            // Convert to Game
            Game sourceGame = Game.fromSudokuBoard(sourceBoard);
            
            // Call controller's driveGames
            controller.driveGames(sourceGame);
            
        } catch (NotFoundException e) {
            throw new SolutionInvalidException("Source file not found: " + sourcePath);
        } catch (InvalidGameException e) {
            throw new SolutionInvalidException("Source file is invalid: " + e.getMessage());
        }
    }
    
    @Override
    public boolean[][] verifyGame(int[][] game) {
        // Convert int[][] to Game
        Game gameObj = new Game(game);
        
        // Call controller's verifyGame
        String status = controller.verifyGame(gameObj);
        
        // For this method, we need to return which cells are valid/invalid
        // Create a 9x9 boolean array (true = valid, false = invalid)
        boolean[][] validity = new boolean[9][9];
        
        if (status.equals("VALID") || status.equals("INCOMPLETE")) {
            // All filled cells are valid
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    validity[r][c] = true;
                }
            }
        } else {
            // Status is INVALID - need to mark which cells have duplicates
            // For now, we'll use the verifier to find duplicates
            try {
                SudokuBoard board = new SudokuBoard(9);
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        board.setCell(r, c, game[r][c]);
                    }
                }
                
                SudokuVerifier verifier = new SudokuVerifier(board);
                ValidationResult result = verifier.verify();
                
                // Mark all cells as valid initially
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        validity[r][c] = true;
                    }
                }
                
                // TODO: Mark cells with duplicates as false
                // This would require extracting duplicate positions from ValidationResult
                // For now, simplified version
                
            } catch (Exception e) {
                // Error case - mark all as valid
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
        // Convert int[][] to Game
        Game gameObj = new Game(game);
        
        // Call controller's solveGame
        int[] solution = controller.solveGame(gameObj);
        
        // Convert solution from [row1, col1, val1, row2, col2, val2, ...]
        // to [[row1, col1, val1], [row2, col2, val2], ...]
        int numCells = solution.length / 3;
        int[][] result = new int[numCells][3];
        
        for (int i = 0; i < numCells; i++) {
            result[i][0] = solution[i * 3];      // row
            result[i][1] = solution[i * 3 + 1];  // col
            result[i][2] = solution[i * 3 + 2];  // value
        }
        
        return result;
    }
    
    @Override
    public void logUserAction(UserAction userAction) throws IOException {
        // Convert UserAction to string format
        String logString = userAction.toLogFormat();
        
        // Call controller's logUserAction
        controller.logUserAction(logString);
        
        // Also log to the structured log for undo support
        GameLogger logger = new GameLogger();
        logger.logAction(userAction);
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
     * @return The action that was undone, or null if nothing to undo
     */
    public UserAction undo() throws IOException {
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
}