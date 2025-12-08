package nub.wi1helm.module.modules.fishing;

import net.minestom.server.tag.Tag;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.fishing.core.FishingManager;
import nub.wi1helm.module.modules.fishing.entity.FishermanNPC;
import org.jetbrains.annotations.NotNull;

public interface FishingInterface extends GameModule {

    @NotNull FishermanNPC getFishermanNPC();

    @NotNull Tag<?> fishItemTag();

    @NotNull FishingManager getFishingManager();
}
