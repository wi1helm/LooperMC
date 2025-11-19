package nub.wi1helm.module.modules.chores.laundry;

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

import java.util.HashSet;
import java.util.Set;

public class LaundryModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    private final AlexNPC npc;
    private final Set<LaundryBasket> baskets = new HashSet<>();
    private final Set<LaundryLine> lines = new HashSet<>();

    public LaundryModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;

        this.npc = new AlexNPC(goalManager);
        this.npc.spawn(entityManager, worldManager.getTownWorld(), new Pos(-24.5, -42.5, 3.5, -120, -16));

        setupBaskets();
        spawnBaskets();

        setupLines();
        spawnLines();

    }

    private void setupBaskets() {
        baskets.add(new LaundryBasket(goalManager, itemManager, new Pos(-23.5, -41, 1.5), lines));
    }

    private void setupLines() {
        lines.add(new LaundryLine(new Pos(32,-48,-18.2), new Pos(27,-48.5,-24),10));
        lines.add(new LaundryLine(new Pos(-34.0,-49.3,5.0), new Pos(-26,-48.5,-2),20));
    }
    private void spawnLines() {
        lines.forEach(laundryLine -> laundryLine.spawn(entityManager, worldManager.getTownWorld()));
    }

    private void spawnBaskets() {
        baskets.forEach(laundryBasket -> laundryBasket.spawn(entityManager, worldManager.getTownWorld()));
    }


    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:laundry");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
