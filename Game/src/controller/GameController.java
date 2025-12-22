package controller;

import game.*;
import java.io.IOException;

/**
 * Main controller implementing the Viewable interface.
 * Coordinates all backend components: storage, generation, verification, solving, logging.
 */
public class GameController implements Viewable {
    
    private final GameStorageManager storageManager;
    private final GameGenerator generator;
    private final GameLogger logger;
    private final SudokuSolver solver;
    
    public GameController() {
        this.storageManager = new GameStorageManager();
        this.generator = new GameGenerator();
        this.logger = new GameLogger();
        this.solver = new SudokuSolver();
    }
    
    @Override
    public Catalog getCatalog() {
        GameCatalog gameCatalog = storageManager.getGameCatalog();
        
        boolean current = gameCatalog.currentAvailable();
        boolean allModesExist = gameCatalog.easyAvailable() 
                              && gameCatalog.mediumAvailable() 
                              && gameCatalog.hardAvailable();
        
        return new Catalog(current, allModesExist);
    }
    
    @Override
    public Game getGame(DifficultyEnum level) throws NotFoundException {
        try {
            String difficulty = level.toConstant();
            SudokuBoard board = storageManager.loadBoard(difficulty);
            return Game.fromSudokuBoard(board);
        } catch (InvalidGameException e) {
            throw new NotFoundException("Game file is corrupted: " + e.getMessage());
        }
    }
    
    @Override
    public void driveGames(Game sourceGame) throws SolutionInvalidException {
        try {
            SudokuBoard sourceBoard = sourceGame.toSudokuBoard();
            
            SudokuVerifier verifier = new SudokuVerifier(sourceBoard);
            ValidationResult result = verifier.verify();
            
            if (!result.isValid()) {
                String status = result.getStatus();
                throw new SolutionInvalidException(
                    "Source solution is " + status + ". Cannot generate games from invalid/incomplete source."
                );
            }
            
            SudokuBoard easyBoard = generator.generateBoard(DifficultyConstants.EASY);
            SudokuBoard mediumBoard = generator.generateBoard(DifficultyConstants.MEDIUM);
            SudokuBoard hardBoard = generator.generateBoard(DifficultyConstants.HARD);
            
            storageManager.saveBoard(easyBoard, DifficultyConstants.EASY);
            storageManager.saveBoard(mediumBoard, DifficultyConstants.MEDIUM);
            storageManager.saveBoard(hardBoard, DifficultyConstants.HARD);
            
            System.out.println("Successfully generated and saved 3 difficulty levels.");
            
        } catch (InvalidGameException e) {
            throw new SolutionInvalidException("Failed to generate games: " + e.getMessage());
        }
    }
    
    @Override
    public String verifyGame(Game game) {
        try {
            SudokuBoard board = game.toSudokuBoard();
            SudokuVerifier verifier = new SudokuVerifier(board);
            ValidationResult result = verifier.verify();
            
            String status = result.getStatus();
            
            if (status.equals("INVALID")) {
                return status;
            }
            
            return status;
            
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Override
    public int[] solveGame(Game game) throws InvalidGameException {
        SudokuBoard board = game.toSudokuBoard();
        return solver.solve(board);
    }
    
    @Override
    public void logUserAction(String userAction) throws IOException {
        logger.logMessage(userAction);
    }
    
    /**
     * Additional helper method for saving current game state.
     */
    public void saveCurrentGame(Game game) throws InvalidGameException {
        SudokuBoard board = game.toSudokuBoard();
        storageManager.saveCurrentGame(board);
    }
    
    /**
     * Loads the current/incomplete game.
     */
    public Game getCurrentGame() throws NotFoundException, InvalidGameException {
        SudokuBoard board = storageManager.loadCurrentGame();
        return Game.fromSudokuBoard(board);
    }
    
    /**
     * Handles undo operation.
     * Returns simple array to avoid using UserAction (viewer-side class).
     * @return Array [row, col, previousValue] for the last action, or null if log was empty
     */
    public int[] undo() throws IOException {
        String lastLogEntry = logger.getLastLogEntry();
        
        if (lastLogEntry == null) {
            return null;
        }
        
        int[] actionData = parseLogEntry(lastLogEntry);
        logger.removeLastAction();
        
        return new int[] { actionData[0], actionData[1], actionData[3] };
    }
    
    /**
     * Parses a log entry in format "(x, y, val, prev)" into an array.
     */
    private int[] parseLogEntry(String line) {
        line = line.trim().replace("(", "").replace(")", "");
        String[] parts = line.split(",");
        
        int row = Integer.parseInt(parts[0].trim());
        int col = Integer.parseInt(parts[1].trim());
        int newValue = Integer.parseInt(parts[2].trim());
        int prevValue = Integer.parseInt(parts[3].trim());
        
        return new int[] { row, col, newValue, prevValue };
    }
    
    /**
     * Deletes a completed game.
     */
    public void deleteCompletedGame(DifficultyEnum level) throws IOException {
        String difficulty = level.toConstant();
        storageManager.deleteGame(difficulty);
        storageManager.deleteCurrentGame();
        logger.clearLog();
    }
    /**
 * Saves the original puzzle for a difficulty level.
 */
public void saveOriginalGame(Game game, DifficultyEnum difficulty) throws InvalidGameException {
    SudokuBoard board = game.toSudokuBoard();
    String difficultyStr = difficulty.toConstant() + "_ORIGINAL";
    storageManager.saveBoard(board, difficultyStr);
}
public Game getOriginalGame(DifficultyEnum difficulty) throws NotFoundException, InvalidGameException {
    String difficultyStr = difficulty.toConstant() + "_ORIGINAL";
    SudokuBoard board = storageManager.loadBoard(difficultyStr);
    return Game.fromSudokuBoard(board);
}
}