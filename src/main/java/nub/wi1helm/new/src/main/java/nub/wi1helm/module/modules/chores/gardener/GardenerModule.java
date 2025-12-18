package nub.wi1helm.module.modules.chores.gardener;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.chores.gardener.entity.GardenerNPC;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class GardenerModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    private final GardenerNPC npc;

    public GardenerModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;

        this.npc = new GardenerNPC(worldManager, goalManager, itemManager);
        this.npc.spawn(entityManager, worldManager.getTownWorld(), new Pos(-34.5, -46, 39.5,-138, 4));
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:gardener");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
