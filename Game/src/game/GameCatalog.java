package game;


public record GameCatalog(
    boolean easyAvailable,
    boolean mediumAvailable,
    boolean hardAvailable,
    boolean currentAvailable
) {
 
    public boolean isAnyGameAvailable() {
        return easyAvailable || mediumAvailable || hardAvailable || currentAvailable;
    }
}
