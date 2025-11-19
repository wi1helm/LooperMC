package nub.wi1helm.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.world.DimensionType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the single, persistent game world (the lobby instance).
 * This class handles the specific loading and initialization logic for the world file.
 */
public final class TownWorld extends InstanceContainer {

    private static final int CHUNK_RADIUS_X = 6;
    private static final int CHUNK_RADIUS_Z = 4;

    // Define the specific spawn point for this world
    private final Pos spawn = new Pos(-5.5, -47.0, 40.5, 180, 0);
    private final int playerBottom = -64;

    public TownWorld() {
        // Initialize with a unique UUID, OVERWORLD dimension, and Anvil loader path
        super(UUID.randomUUID(), DimensionType.OVERWORLD, new AnvilLoader("resources/worlds/town"));
        this.enableAutoChunkLoad(false);

        // 1. Load the surrounding chunks synchronously (blocking call via .join())
        Set<CompletableFuture<Chunk>> futures = new HashSet<>();
        for (int x = -CHUNK_RADIUS_X; x <= CHUNK_RADIUS_X; x++) {
            for (int z = -CHUNK_RADIUS_Z; z <= CHUNK_RADIUS_Z; z++) {
                futures.add(this.loadChunk(x, z));
            }
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        // 2. Register the instance with the Minestom manager
        MinecraftServer.getInstanceManager().registerInstance(this);
        System.out.println("TownWorld initialized and spawn chunks loaded.");
    }

    public Pos getSpawnPosition() {
        return spawn;
    }

    public int getPlayerBottomY() {
        return playerBottom;
    }
}