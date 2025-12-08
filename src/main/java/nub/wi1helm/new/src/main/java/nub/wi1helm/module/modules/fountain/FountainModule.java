package nub.wi1helm.module.modules.fountain;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.fishing.FishingInterface;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FountainModule implements GameModule {

    private final FishingInterface fishingInterface;
    private final WorldManager worldManager;
    private final GoalManager goalManager;

    private final List<Fountain> fountains = new ArrayList<>();

    public FountainModule(FishingInterface fishingInterface,
                          WorldManager worldManager,
                          GoalManager goalManager,
                          EntityManager entityManager,
                          ItemManager itemManager) {

        this.fishingInterface = fishingInterface;
        this.worldManager = worldManager;
        this.goalManager = goalManager;

        if (!goalManager.hasActiveGoal(ServerGoals.FOUNTAIN_FISH)) return;

        addFountain(new Pos(-9.5, -46, 4.4), 2, 100, 1);
    }

    public void addFountain(Pos pos,
                            double radius,
                            int maxSacrifice,
                            int globalGoalContribution) {

        fountains.add(new Fountain(
                pos,
                radius,
                // Pass new parameters
                maxSacrifice,
                globalGoalContribution,
                fishingInterface,
                goalManager,
                worldManager
        ));
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:fountain");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {}
}