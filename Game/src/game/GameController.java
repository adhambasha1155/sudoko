package game;

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
        
        // Convert GameCatalog to Catalog
        boolean current = gameCatalog.currentAvailable();
        boolean allModesExist = gameCatalog.easyAvailable() 
                              && gameCatalog.mediumAvailable() 
                              && gameCatalog.hardAvailable();
        
        return new Catalog(current, allModesExist);
    }
    
    @Override
    public Game getGame(DifficultyEnum level) throws NotFoundException {
        try {
            // Convert enum to string constant
            String difficulty = level.toConstant();
            
            // Load the board
            SudokuBoard board = storageManager.loadBoard(difficulty);
            
            // Convert to Game and return
            return Game.fromSudokuBoard(board);
            
        } catch (InvalidGameException e) {
            // Convert to NotFoundException if game is corrupted
            throw new NotFoundException("Game file is corrupted: " + e.getMessage());
        }
    }
    
    @Override
    public void driveGames(Game sourceGame) throws SolutionInvalidException {
        try {
            // 1. Convert Game to SudokuBoard
            SudokuBoard sourceBoard = sourceGame.toSudokuBoard();
            
            // 2. Verify the source solution is VALID
            SudokuVerifier verifier = new SudokuVerifier(sourceBoard);
            ValidationResult result = verifier.verify();
            
            if (!result.isValid()) {
                String status = result.getStatus();
                throw new SolutionInvalidException(
                    "Source solution is " + status + ". Cannot generate games from invalid/incomplete source."
                );
            }
            
            // 3. Generate three difficulty levels
            SudokuBoard easyBoard = generator.generateBoard(DifficultyConstants.EASY);
            SudokuBoard mediumBoard = generator.generateBoard(DifficultyConstants.MEDIUM);
            SudokuBoard hardBoard = generator.generateBoard(DifficultyConstants.HARD);
            
            // 4. Save all three games
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
            // Convert Game to SudokuBoard
            SudokuBoard board = game.toSudokuBoard();
            
            // Verify using SudokuVerifier
            SudokuVerifier verifier = new SudokuVerifier(board);
            ValidationResult result = verifier.verify();
            
            String status = result.getStatus();
            
            // If INVALID, include duplicate information
            if (status.equals("INVALID")) {
                // Get duplicate locations as a string
                // Format: "INVALID 1,2 3,3 6,7" (example positions)
                StringBuilder sb = new StringBuilder("INVALID");
                
                // You can extract duplicate positions from ValidationResult if needed
                // For now, just return the status
                return status;
            }
            
            return status; // "VALID" or "INCOMPLETE"
            
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    @Override
    public int[] solveGame(Game game) throws InvalidGameException {
        // Convert Game to SudokuBoard
        SudokuBoard board = game.toSudokuBoard();
        
        // Use the solver
        return solver.solve(board);
    }
    
    @Override
    public void logUserAction(String userAction) throws IOException {
        // Log the message
        logger.logMessage(userAction);
    }
    
    /**
     * Additional helper method for saving current game state.
     * Called by the facade when user makes a move.
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
     * @return The UserAction that was undone, or null if log was empty
     */
    public UserAction undo() throws IOException {
        UserAction lastAction = logger.getLastAction();
        
        if (lastAction == null) {
            return null; // Nothing to undo
        }
        
        // Remove the action from log
        logger.removeLastAction();
        
        return lastAction;
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
}