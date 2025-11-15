package nub.wi1helm.content.chores.laundry;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.tag.Tag;
import nub.wi1helm.content.Content;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.instances.LoopInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class LaundryManager implements Content {

    private final LaundryNPC npc;
    private final Set<LaundryBasket> baskets = new HashSet<>();
    private final Set<LaundryLine> lines = new HashSet<>();

    private final GoalManager goalManager;
    private final LoopInstance instance;

    public LaundryManager(@NotNull LoopInstance instance, @NotNull GoalManager goalManager) {
        this.instance = instance;
        this.goalManager = goalManager;

        this.npc = new LaundryNPC(this.instance);
        setupBaskets();
        setupLines();

        this.npc.spawn();
        if (!goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES)) return;

        spawnBaskets();
        spawnLines();
    }

    private void setupBaskets() {
        baskets.add(new LaundryBasket(this.instance, new Pos(-23.5, -41, 1.5)));
    }

    private void setupLines() {
        lines.add(new LaundryLine(new Pos(32,-48,-18.2), new Pos(27,-48.5,-24),10));
        lines.add(new LaundryLine(new Pos(-34.0,-49.3,5.0), new Pos(-26,-48.5,-2),20));
    }
    private void spawnLines() {
        lines.forEach(LaundryLine::spawn);
    }

    private void spawnBaskets() {
        baskets.forEach(LaundryBasket::spawn);
    }

    public LaundryNPC getNPC() {
        return npc;
    }

    public Set<LaundryBasket> getBaskets() {
        return baskets;
    }

    @Override
    public Tag<?> getManagerTag() {
        return Tag.String("LAUNDRY_CHORE_CONTENT");
    }
}