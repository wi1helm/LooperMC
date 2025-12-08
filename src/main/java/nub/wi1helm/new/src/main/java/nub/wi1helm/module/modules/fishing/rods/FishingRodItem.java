package nub.wi1helm.module.modules.fishing.rods;

import nub.wi1helm.item.GameItem;
import nub.wi1helm.module.modules.fishing.core.FishingRodType;
import org.jetbrains.annotations.NotNull;

public interface FishingRodItem extends GameItem {
    @NotNull FishingRodType getType();

}
