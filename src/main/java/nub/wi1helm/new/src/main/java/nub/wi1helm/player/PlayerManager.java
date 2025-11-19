package nub.wi1helm.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import nub.wi1helm.core.GameService;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.sidebar.SidebarManager;
import nub.wi1helm.world.TownWorld;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class PlayerManager implements GameService {

    private final EventNode<@NotNull PlayerEvent> node = EventNode.type("player", EventFilter.PLAYER);
    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final SidebarManager sidebarManager;

    public PlayerManager(WorldManager worldManager, GoalManager goalManager, SidebarManager sidebarManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.sidebarManager = sidebarManager;

        onConfig();
        onJoin();

        System.out.println("PlayerManager initialized.");
   }

   private void onConfig(){
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setClearChat(true);

            TownWorld town = worldManager.getTownWorld();
            final Player player = event.getPlayer();

            event.setSpawningInstance(town);
            player.setRespawnPoint(town.getSpawnPosition());

        });
   }

    private void onJoin(){
        node.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            goalManager.showRecommendedBarTo(player);
            sidebarManager.createSidebar((GamePlayer) player);

        });
    }

    @Override
    public void registerListeners() {
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }
}
