package nub.wi1helm.module.modules.mayor.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

import java.util.List;

public class MayorLetterItem implements GameItem {
    public static final Tag<@NotNull Boolean> MAYOR_LETTER = Tag.Boolean("mayor:letter");

    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return MAYOR_LETTER;
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
        itemManager.registerItem(this);
        return ItemStack.builder(Material.PAPER)
                .set(DataComponents.ITEM_NAME, Component.text("Letter From the Mayor").color(NamedTextColor.LIGHT_PURPLE))
                .set(getItemTag(), true)
                .build();
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        return ItemStack.builder(Material.PAPER)
                .set(DataComponents.ITEM_NAME, Component.text("Letter From the Mayor").color(NamedTextColor.LIGHT_PURPLE))
                .set(DataComponents.LORE, List.of(Component.text("Click To Get!").color(NamedTextColor.YELLOW)))
                .set(getItemTag(), true)
                .build();
    }
}
