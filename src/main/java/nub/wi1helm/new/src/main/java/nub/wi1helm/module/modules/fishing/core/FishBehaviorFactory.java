package nub.wi1helm.module.modules.fishing.core;

import nub.wi1helm.module.modules.fishing.core.behavior.FishBehavior;
import nub.wi1helm.module.modules.fishing.entity.FishingHook;

@FunctionalInterface
public interface FishBehaviorFactory {
    // Factory now receives the specific entry that was rolled
    FishBehavior create(FishingHook hook, FishingRodType rodType, FishingRodType.BehaviorEntry entry);
}