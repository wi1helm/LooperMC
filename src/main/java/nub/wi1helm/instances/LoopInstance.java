package nub.wi1helm.instances;

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

public class LoopInstance extends InstanceContainer {

    private int CHUNK_RADIUS_X = 6;
    private int CHUNK_RADIUS_Z = 4;

    private final Pos spawn = new Pos(-5.5,-47.0,40.5,180,0);
    private final Integer playerBottom = -64;


    public LoopInstance() {
        super(UUID.randomUUID(), DimensionType.OVERWORLD, new AnvilLoader("resources/instances/lobby"));
        this.enableAutoChunkLoad(false);

        Set<CompletableFuture<Chunk>> futures = new HashSet<>();

        for (int x = -CHUNK_RADIUS_X; x <= CHUNK_RADIUS_X; x++) {
            for (int z = -CHUNK_RADIUS_Z; z <= CHUNK_RADIUS_Z; z++) {
                CompletableFuture<Chunk> future = this.loadChunk(x, z);

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        MinecraftServer.getInstanceManager().registerInstance(this);
    }


    public Pos getSpawn() {
        return spawn;
    }

    public Integer getPlayerBottom() {
        return playerBottom;
    }
}
