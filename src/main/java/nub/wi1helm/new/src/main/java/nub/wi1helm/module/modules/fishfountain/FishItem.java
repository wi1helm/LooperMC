package nub.wi1helm.module.modules.fishfountain;

import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import org.jetbrains.annotations.NotNull;

public class FishItem implements GameItem {
    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return Tag.Boolean("module:fountain:item:fish");
    }

    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {

    }

    @Override
    public void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event) {

    }

    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        return ItemStack.builder(Material.COD)
                .set(getItemTag(), true)
                .build();
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        return ItemStack.builder(Material.COD)
                .set(getItemTag(), true)
                .build();
    }
}
