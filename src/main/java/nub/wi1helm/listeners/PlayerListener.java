package nub.wi1helm.listeners;

import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import nub.wi1helm.ServerManager;
import nub.wi1helm.dialogs.GoalDialog;
import nub.wi1helm.player.LoopPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerListener {

    private final EventNode<@NotNull PlayerEvent> node = EventNode.type("player", EventFilter.PLAYER);

    private final ServerManager manager = ServerManager.getManager();

    public PlayerListener() {
        onConfig();
        onSpawn();

        onBlockBreak();
    }

    private void onConfig() {
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {

            // Set Instance
            event.setSpawningInstance(manager.getInstance());

            // Get Player
            final Player player = event.getPlayer();
            // Set Spawn
            player.setRespawnPoint(manager.getInstance().getSpawn());


        });
    }

    private void onSpawn() {
        node.addListener(PlayerSpawnEvent.class, event -> {
           final LoopPlayer player = (LoopPlayer) event.getPlayer();
            manager.goalManager().updateRecommendedBar();
            // Show current boss bar to this player
            manager.goalManager().showRecommendedBarTo(player);
        });
    }

    private void onBlockBreak() {
        node.addListener(PlayerBlockBreakEvent.class, event -> {
           final LoopPlayer player = (LoopPlayer) event.getPlayer();

            player.showDialog(new GoalDialog().get(manager.goalManager().getActiveGoals()));


            event.setCancelled(!player.canBreak());

        });
    }


    public EventNode<@NotNull PlayerEvent> getNode() {
        return node;
    }
}
