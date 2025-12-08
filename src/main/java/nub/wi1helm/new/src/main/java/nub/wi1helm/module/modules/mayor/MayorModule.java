package nub.wi1helm.module.modules.mayor;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.fishing.FishingInterface;
import nub.wi1helm.module.modules.mayor.entity.MayorNPC;
import nub.wi1helm.module.modules.mayor.interactions.FishermanQuestInteractions;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class MayorModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    // Other modules interfaces.
    private final FishingInterface fishingInterface;


    //NPC
    private final MayorNPC mayorNPC;
    public MayorModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager, FishingInterface fishingInterface) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;
        this.fishingInterface = fishingInterface;


        this.mayorNPC = new MayorNPC(goalManager, itemManager);
        this.mayorNPC.spawn(entityManager, worldManager.getTownWorld(), new Pos(30.5,-47,-14.5,90,0));
        this.mayorNPC.setupInteractions();

        new FishermanQuestInteractions(fishingInterface.getFishermanNPC()).setup();

    }
    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:mayor");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
