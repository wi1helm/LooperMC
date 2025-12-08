package nub.wi1helm.module.modules.hideandseek;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.hideandseek.entity.LilyNPC;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class HideAndSeekModule implements GameModule {

    private final WorldManager worldManager;
    private final EntityManager entityManager;
    private final GoalManager goalManager;

    private final LilyNPC npc;

    public HideAndSeekModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager) {
        this.worldManager = worldManager;
        this.entityManager = entityManager;
        this.goalManager = goalManager;

        this.npc = new LilyNPC(new Pos(3.5, -47,44.5), entityManager, goalManager);
        this.npc.spawn(entityManager, worldManager.getTownWorld());
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:hideandseek");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
