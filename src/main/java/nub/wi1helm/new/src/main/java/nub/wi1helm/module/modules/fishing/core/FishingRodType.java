package nub.wi1helm.module.modules.fishing.core;

import net.minestom.server.tag.Tag;
import nub.wi1helm.module.modules.fishing.core.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record FishingRodType(
        Tag<@NotNull Boolean> tag,

        // --- Common Rod Stats (Now per Rod) ---
        int minWaitTime,           // Min time until any bite/particle starts
        int maxWaitTime,           // Max time until any bite/particle starts
        int catchWindowTicks,      // Time player has to react to the bite

        List<BehaviorEntry> behaviorEntries,
        double totalWeight
) {
    // --- NEW INNER RECORD: Ties a Behavior, Loot, and Weight together ---
    public record BehaviorEntry(
            FishBehaviorFactory factory,
            LootTable lootTable,
            int minParticleTicks,    // Unique stat for this specific behavior type
            int maxParticleTicks,    // Unique stat for this specific behavior type
            double spawnDistanceMin, // Unique stat for this specific behavior type
            double spawnDistanceMax, // Unique stat for this specific behavior type
            double weight
    ) {}

    // Helper method to roll a behavior based on weight
    public BehaviorEntry rollBehaviorEntry() {
        if (behaviorEntries.isEmpty()) return null;

        double r = ThreadLocalRandom.current().nextDouble() * totalWeight;
        for (BehaviorEntry entry : behaviorEntries) {
            if ((r -= entry.weight()) <= 0) return entry;
        }
        return behaviorEntries.getLast();
    }
}