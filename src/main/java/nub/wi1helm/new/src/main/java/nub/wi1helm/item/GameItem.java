package nub.wi1helm.item;

import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public interface GameItem {

    @NotNull Tag<@NotNull Boolean> getItemTag();

    void onPlayerUse(PlayerUseItemEvent event);
    void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event);

    @NotNull ItemStack getPlayerItem(ItemManager itemManager);
    @NotNull ItemStack getGuiItem(ItemManager itemManager);



}
