package nub.wi1helm.module.modules.fishing.core.behavior;

import nub.wi1helm.module.modules.fishing.core.loot.LootTable; // Import is necessary

public interface FishBehavior {

    // Only defines the contract

    void start();
    void tick();
    LootTable tryCatch();

    void cancel();

    // Removed default randomBetween (moved to AbstractFishBehavior)
    int randomBetween(int min, int max);
}