package nub.wi1helm.module.modules.voidjumpers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class VoidJumperModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;

    public VoidJumperModule(WorldManager worldManager, GoalManager goalManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
    }


    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:voidjumper");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node)  {
        node.addListener(PlayerMoveEvent.class, event -> {
            if (event.getPlayer().getPosition().y() < worldManager.getTownWorld().getPlayerBottomY()) {
                event.getPlayer().teleport(worldManager.getTownWorld().getSpawnPosition());

                event.getPlayer().sendActionBar(MiniMessage.miniMessage().deserialize("<light_purple>Woops, not suppose to be there!<light_purple>"));
                goalManager.incrementGoal(ServerGoals.VOID_JUMPERS, 1);
            }


        });
    }
}
