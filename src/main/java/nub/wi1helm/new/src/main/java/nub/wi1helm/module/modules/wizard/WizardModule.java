package nub.wi1helm.module.modules.wizard;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.wizard.entity.WizardNPC;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class WizardModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    private final WizardNPC npc;

    public WizardModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;

        this.npc = new WizardNPC(itemManager);
        this.npc.spawn(entityManager, worldManager.getTownWorld(), new Pos(-13.5,-24,16.5, -169, 8));
        this.npc.setupInteractions();
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:wizard");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
