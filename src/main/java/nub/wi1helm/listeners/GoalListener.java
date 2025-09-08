package nub.wi1helm.listeners;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import nub.wi1helm.ServerManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.instances.LoopInstance;
import org.jetbrains.annotations.NotNull;

public class GoalListener {

    private final EventNode<@NotNull Event> node = EventNode.all("goal");

    private final ServerManager serverManager = ServerManager.getManager();
    private final GoalManager goalManager = serverManager.goalManager();

    private final LoopInstance instance = serverManager.getInstance();

    public GoalListener() {
        voidJumpers();
    }

    public void voidJumpers() {
        node.addListener(PlayerMoveEvent.class, event -> {
           final Player player = event.getPlayer();

           if (player.getPosition().y() > instance.getPlayerBottom()) return;

           player.teleport(instance.getSpawn());

           goalManager.incrementGoal(ServerGoals.VOID_JUMPERS);

        });
    }

    public EventNode<@NotNull Event> getNode() {
        return node;
    }
}
