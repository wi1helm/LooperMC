package nub.wi1helm.listeners;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNull;

public class GlobalListener {


    private static GlobalListener instance;

    private final PlayerListener player = new PlayerListener();
    private final GoalListener goal = new GoalListener();
    private final EventNode<@NotNull Event> node = MinecraftServer.getGlobalEventHandler();


    private GlobalListener() {
        node.addChild(player.getNode());
        node.addChild(goal.getNode());
    }

    public static GlobalListener getInstance() {

        if (instance != null) return instance;

        instance = new GlobalListener();

        return instance;
    }
}
