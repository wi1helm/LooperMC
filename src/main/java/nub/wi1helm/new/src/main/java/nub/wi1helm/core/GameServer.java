package nub.wi1helm.core;

import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import nub.wi1helm.player.GamePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameServer {

    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    private static GameCore core;


    static void main() {
        // 1. Initialize the Minestom Server
        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        // 2. Initialize the GameKernel (Service Locator)
        // This is where all managers and content are initialized and wired together.
        try {
            core = new GameCore();
        } catch (Exception e) {
            logger.error("FATAL: Failed to initialize GameCore.", e);
            System.exit(1);
        }

        // 3. Set the Custom Player Provider
        // All connecting players will use our custom GamePlayer class.
        MinecraftServer.getConnectionManager().setPlayerProvider((connection, gameProfile) -> {
            // We pass dependencies needed by the player upon creation
            return new GamePlayer(connection, gameProfile, core.getGoalManager());
        });

        logger.info("Game Core initialized successfully. Starting Minestom server...");

        // 4. Start the Server
        server.start("0.0.0.0", 25565);

    }
}
