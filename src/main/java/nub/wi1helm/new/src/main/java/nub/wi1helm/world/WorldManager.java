package nub.wi1helm.world;

/**
 * Manages access to the primary game world instance.
 * It is responsible for initializing the GameWorld upon server startup.
 */
public class WorldManager {

    private final TownWorld townWorld;

    public WorldManager() {
        // Initialization logic: Create the single, persistent world instance.
        this.townWorld = new TownWorld();
    }

    /**
     * Gets the main, persistent world instance where all players are located.
     */
    public TownWorld getTownWorld() {
        return townWorld;
    }
}