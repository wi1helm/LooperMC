package nub.wi1helm.module.modules.fishfountain;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class FountainModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    private final FishermanNPC npc;

    public FountainModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;

        this.npc = new FishermanNPC(itemManager);
        this.npc.spawn(entityManager, worldManager.getTownWorld(), new Pos(29.5,-49,6.5,120,3));
    }




    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:fountain");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
