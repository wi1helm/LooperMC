package nub.wi1helm.module.modules.fishing;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.fishing.core.FishingManager;
import nub.wi1helm.module.modules.fishing.entity.FishermanNPC;
import nub.wi1helm.module.modules.fishing.items.FishItem;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class FishingModule implements FishingInterface {

    private final WorldManager worldManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;
    private final FishingManager fishingManager;

    private final FishermanNPC npc;

    public FishingModule(WorldManager worldManager, EntityManager entityManager, ItemManager itemManager) {
        this.worldManager = worldManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;

        this.fishingManager = new FishingManager(entityManager);

        this.npc = new FishermanNPC(itemManager, fishingManager);
        this.npc.spawn(entityManager, worldManager.getTownWorld(), new Pos(29.5,-49,6.5,120,3));
        this.npc.setupInteractions();
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:fishing");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }

    @Override
    public @NotNull FishermanNPC getFishermanNPC() {
        return npc;
    }

    @Override
    public @NotNull Tag<?> fishItemTag() {
        return FishItem.FISH_TAG;
    }

    @Override
    public @NotNull FishingManager getFishingManager() {
        return fishingManager;
    }
}
