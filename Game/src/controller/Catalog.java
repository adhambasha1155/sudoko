package controller;

/**
 * Represents the catalog of available games.
 * MUST ONLY BE USED ON CONTROLLER SIDE - NOT VIEWER SIDE.
 * As per lab spec.
 */
public class Catalog {
    // True if there is a game in progress, False otherwise.
    public boolean current;
    
    // True if there is at least one game available
    // for each difficulty, False otherwise.
    public boolean allModesExist;
    
    public Catalog(boolean current, boolean allModesExist) {
        this.current = current;
        this.allModesExist = allModesExist;
    }
}