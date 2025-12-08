package nub.wi1helm.module.modules.fishing.items;

import net.minestom.server.entity.*;
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

public class FishItem implements GameItem {

    public static Tag<@NotNull Boolean> FISH_TAG = Tag.Boolean("module:fountain:item:fish");

    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return FISH_TAG;
    }

    private ItemManager itemManager;

    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {

    }

    @Override
    public void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event) {

    }

    @Override
    public void onDropItem(ItemDropEvent event) {
        if (!event.getItemStack().hasTag(getItemTag())) return;
        ItemEntity entity = spawnItemEntity(event.getPlayer(), event.getItemStack());
    }

    @Override
    public void onItemPickup(PickupItemEvent event) {
        // Check if the entity picking up is a player
        if (!(event.getLivingEntity() instanceof Player player)) {
            return; // not a player → ignore
        }

        ItemStack pickedItem = event.getItemStack();

        player.getInventory().addItemStack(pickedItem);
    }



    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        this.itemManager = itemManager;
        itemManager.registerItem(this);
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
