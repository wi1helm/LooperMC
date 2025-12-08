package nub.wi1helm.module.modules.fishing.core;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.module.modules.fishing.core.behavior.FishBehavior;
import nub.wi1helm.module.modules.fishing.core.loot.LootTable; // Import is necessary
import nub.wi1helm.module.modules.fishing.entity.FishingHook;

public class FishingSession {

    private final Player player;
    private final FishingRodType type;
    private final FishingHook hook;
    private final FishBehavior fishLogic;

    private final EntityManager entityManager;

    public FishingSession(Player player, FishingRodType type, EntityManager entityManager) {
        this.player = player;
        this.type = type;

        // Select a weighted behavior entry from the rod type
        FishingRodType.BehaviorEntry entry = type.rollBehaviorEntry();
        if (entry == null) throw new IllegalStateException("Rod type has no defined behaviors.");

        this.hook = new FishingHook(player);
        // Pass the chosen entry to the factory
        this.fishLogic = entry.factory().create(hook, type, entry);
        this.entityManager = entityManager;
    }

    public void start() {
        hook.spawn(entityManager, player.getInstance());
        fishLogic.start();
    }

    public void reelIn() {
        // CHANGED: tryCatch returns the LootTable if successful
        LootTable lootTable = fishLogic.tryCatch();

        if (lootTable != null) {
            // The session executes the roll and rewards the player
            ItemStack reward = lootTable.roll();
            player.getInventory().addItemStack(reward);
        }
        // If the fish was caught, the behavior is already cancelled in tryCatch().
        // If not caught, the behavior continues ticking.
    }

    public void cancel() {
        hook.remove();
        fishLogic.cancel();
    }
}