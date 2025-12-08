package nub.wi1helm.module.modules.fishing.core.behavior;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.module.modules.fishing.core.FishingRodType;
import nub.wi1helm.module.modules.fishing.core.loot.LootTable;
import nub.wi1helm.module.modules.fishing.entity.FishingHook;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractFishBehavior implements FishBehavior {

    protected final ThreadLocalRandom random = ThreadLocalRandom.current();
    protected Task tickTask;
    protected final FishingHook hook;

    protected final FishingRodType.BehaviorEntry behaviorEntry;
    protected final LootTable lootTable;
    protected final int catchWindowTicks;

    public AbstractFishBehavior(@NotNull FishingHook hook, @NotNull FishingRodType rodType, @NotNull FishingRodType.BehaviorEntry behaviorEntry) {
        this.hook = hook;
        this.behaviorEntry = behaviorEntry;
        this.lootTable = behaviorEntry.lootTable();
        // Get common stats from the rod type or use the entry's custom values
        this.catchWindowTicks = rodType.catchWindowTicks();
    }

    @Override
    public int randomBetween(int min, int max) {
        return random.nextInt(min, max + 1);
    }

    /**
     * Creates and stores the task responsible for calling the tick() method.
     */
    protected void createTickTask(Runnable task, TaskSchedule interval) {
        this.tickTask = hook.getScheduler().buildTask(task)
                .repeat(interval)
                .schedule();
    }

    /**
     * Determines a suitable starting position for the particle trail near the hook.
     * This is abstract because different behaviors might have different particle start patterns.
     * * @return A position near the hook's water level.
     */
    protected abstract Pos getRandomStartPointNearHook();

    /**
     * Ensures the tick task is cancelled and the hook is removed when the behavior is finished.
     */
    @Override
    public void cancel() {
        if (tickTask != null) {
            tickTask.cancel();
        }
        hook.remove();
    }
}