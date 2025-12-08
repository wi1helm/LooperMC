package nub.wi1helm.module.modules.chores.pondside;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.chores.pondside.entity.PartBox;
import nub.wi1helm.module.modules.chores.pondside.entity.PondSideMechanicNPC;
import nub.wi1helm.module.modules.fishing.FishingInterface;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class PondSideModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    private final FishingInterface fishingInterface;

    private final PondSideMechanicNPC npc;
    private final PartBox partBox;

    public PondSideModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager, FishingInterface fishingInterface) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;
        this.fishingInterface = fishingInterface;

        this.npc = new PondSideMechanicNPC();
        this.npc.spawn(entityManager, worldManager.getTownWorld());

        this.partBox = new PartBox(entityManager, goalManager);
        this.partBox.spawn(entityManager, worldManager.getTownWorld(), new Pos(17.5,-46,1.5));
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:pondside");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
