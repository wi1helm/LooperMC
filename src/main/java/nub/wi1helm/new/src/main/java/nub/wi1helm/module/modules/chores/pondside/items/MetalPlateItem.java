package nub.wi1helm.module.modules.chores.pondside.items;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import org.jetbrains.annotations.NotNull;

public class MetalPlateItem implements GameItem {
    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return Tag.Boolean("module:poolside:item:metalplate");
    }

    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {

    }

    @Override
    public void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event) {

    }

    @Override
    public void onDropItem(ItemDropEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onItemPickup(PickupItemEvent event) {

    }

    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        return ItemStack.builder(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .set(DataComponents.ITEM_NAME, Component.text("Metal Plate"))
                .set(getItemTag(), true)
                .build();
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        return ItemStack.builder(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                .set(DataComponents.ITEM_NAME, Component.text("Metal Plate"))
                .build();
    }
}
