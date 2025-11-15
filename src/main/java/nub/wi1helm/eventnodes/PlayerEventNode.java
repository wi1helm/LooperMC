package nub.wi1helm.eventnodes;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.utils.validate.Check;
import nub.wi1helm.dialogs.GoalDialog;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.instances.LoopInstance;
import nub.wi1helm.player.LoopPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerEventNode {

    private final EventNode<@NotNull PlayerEvent> node = EventNode.type("player", EventFilter.PLAYER);

    private static PlayerEventNode instance;

    private PlayerEventNode() {
        onConfig();
        onSpawn();
        onBlockBreak();

        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

    public static PlayerEventNode init() {
        if (instance == null) instance = new PlayerEventNode();
        return instance;
    }
    public static PlayerEventNode get() {
        Check.stateCondition(instance == null, "PlayerEventNode needs to be initiated before get-ted!");
        return instance;
    }

    private void onConfig() {
        node.addListener(AsyncPlayerConfigurationEvent.class, event -> {

            LoopInstance instance = LoopInstance.get();

            // Set Instance
            event.setSpawningInstance(instance);

            // Get Player
            final Player player = event.getPlayer();
            // Set Spawn
            player.setRespawnPoint(instance.getSpawn());


        });
    }

    private void onSpawn() {
        node.addListener(PlayerSpawnEvent.class, event -> {
           final LoopPlayer player = (LoopPlayer) event.getPlayer();
            // Show current boss bar to this player
            GoalManager.get().showRecommendedBarTo(player);
        });
    }

    private void onBlockBreak() {
        node.addListener(PlayerBlockBreakEvent.class, event -> {
           final LoopPlayer player = (LoopPlayer) event.getPlayer();

            player.showDialog(new GoalDialog().get(GoalManager.get().getActiveGoals()));

            event.setCancelled(!player.canBreak());
        });
    }


    public EventNode<@NotNull PlayerEvent> getNode() {
        return node;
    }
}
