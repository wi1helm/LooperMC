package nub.wi1helm.module.modules.chores.pondside.items;

import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import org.jetbrains.annotations.NotNull;

public class FishingRodSuper300BItem implements GameItem {
    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return Tag.Boolean("module:pondside:item:fishingrodsuper3000b");
    }

    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {

    }

    @Override
    public void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event) {

    }

    @Override
    public void onDropItem(ItemDropEvent event) {

    }

    @Override
    public void onItemPickup(PickupItemEvent event) {

    }

    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        return null;
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        return null;
    }
}
