package nub.wi1helm.module.modules.fountain;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.entity.EntityTickEvent;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.modules.fishing.FishingInterface;
import nub.wi1helm.world.WorldManager;

public class Fountain {

    private final WorldManager worldManager;
    private final FishingInterface fishing;
    private final GoalManager goalManager;

    public final Entity text1;   // top line
    public final Entity text2;   // progress
    public final Pos position;
    public final double radius;

    // 🎣 NEW: The total number of points this fountain needs to complete its task
    private final int maxSacrifice;
    // 🎣 NEW: Tracks the current progress of this specific fountain
    private int currentSacrifice = 0;

    // The amount of progress each dropped fish contributes to the global goal.
    // NOTE: This value is now separate from the displayed max.
    private final int globalGoalContribution;

    public Fountain(
            Pos position,
            double radius,
            // 🎣 MODIFIED: maxSacrifice (the target for this fountain)
            int maxSacrifice,
            // 🎣 NEW: globalGoalContribution (how much a fish here helps the global goal)
            int globalGoalContribution,
            FishingInterface fishing,
            GoalManager goalManager,
            WorldManager worldManager) {

        this.position = position;
        this.radius = radius;
        this.fishing = fishing;
        this.goalManager = goalManager;
        this.worldManager = worldManager;

        this.maxSacrifice = Math.max(1, maxSacrifice);
        this.globalGoalContribution = Math.max(1, globalGoalContribution);

        // spawn line 1
        this.text1 = createTextDisplay(Component.text("Fountain Sacrifice", NamedTextColor.GOLD));
        text1.setInstance(worldManager.getTownWorld(), this.position.add(0, 0.4, 0));

        // spawn line 2
        this.text2 = createTextDisplay(Component.empty());
        text2.setInstance(worldManager.getTownWorld(), this.position);
        // 🎣 MODIFIED: Use local max and current (0) for initial display
        updateFish();

        // attach tick listener to ANY display entity
        this.text1.eventNode().addListener(EntityTickEvent.class, event -> tick());
    }

    // ─────────────────────────────────────────────────────────────────────

    private void tick() {
        // Only process sacrifices if the fountain is not yet complete
        if (currentSacrifice >= maxSacrifice) return;

        var nearby = worldManager.getTownWorld().getNearbyEntities(position, radius);

        for (Entity e : nearby) {
            if (!(e instanceof ItemEntity item)) continue;

            // must be tagged fish
            if (!item.getItemStack().hasTag(fishing.fishItemTag())) continue;

            int itemStackAmount = item.getItemStack().amount();

            // remove (sacrifice)
            item.remove();

            // 🎣 MODIFIED:
            // 1. Calculate how much to add to THIS fountain's progress (1 point per item)
            currentSacrifice += itemStackAmount;

            // 2. Calculate how much to add to the GLOBAL goal
            int progressGained = itemStackAmount * globalGoalContribution;

            goalManager.incrementGoal(ServerGoals.FOUNTAIN_FISH, progressGained);

            // 3. Update the display using local values
            updateFish();
        }
    }

    // ─────────────────────────────────────────────────────────────────────

    private boolean isInWater(Entity e) {
        return worldManager.getTownWorld().getBlock(e.getPosition()).isLiquid();
    }

    // 🎣 MODIFIED: No longer takes amount/max as arguments, uses local fields
    public void updateFish() {
        int displayAmount = Math.min(currentSacrifice, maxSacrifice);
        int displayMax = maxSacrifice;

        text2.editEntityMeta(TextDisplayMeta.class, meta -> {
            if (currentSacrifice < maxSacrifice) {
                meta.setText(Component.text("Sacrifice: " + displayAmount + "/" + displayMax + " Fish", NamedTextColor.AQUA));
            } else {
                // You can add the "something different happens" logic here or in a goal listener
                meta.setText(Component.text("Sacrifice: COMPLETED!", NamedTextColor.GREEN));
            }
        });
    }

    public static Entity createTextDisplay(Component text) {
        Entity e = new Entity(EntityType.TEXT_DISPLAY);

        e.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setBackgroundColor(0);
            meta.setHasNoGravity(true);
            meta.setText(text);
            meta.setShadow(true);
        });

        return e;
    }
}