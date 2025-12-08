package nub.wi1helm.module.modules.mail.items;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.tag.Tag;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mail.entity.Mailbox;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PackageItem implements GameItem {

    private final ItemStack item;

    private static Tag<@NotNull Boolean> GUI_TAG = Tag.Boolean("module:mail:item:package:gui");
    public static  Tag<@NotNull Boolean> ITEM_TAG = Tag.Boolean("module:mail:item:package");

    public PackageItem(String address, UUID uuid) {
        this.item = ItemStack.builder(Material.PLAYER_HEAD)
                .set(DataComponents.ITEM_NAME, MiniMessage.miniMessage().deserialize("<white>Package for </white><green>" + address + "</green>"))
                .set(Mailbox.ADDRESS_TAG, uuid)
                .set(DataComponents.PROFILE, new ResolvableProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTY2NzIyMTgyNjg3MywKICAicHJvZmlsZUlkIiA6ICJlOWRlMDE0NjExZDI0NGY5OTVjNmNiMjhkMDk5MWExNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl6YWJldGhUcnVzcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NzZiZWQ4YmY5MmU0NjFlMmIyMDhkZjBjOWVhNjE4Y2JhN2VlZmY5MzFjYjVmODRmMTM1N2Y3MWE3ODA0YWJlIgogICAgfQogIH0KfQ==","")))
                .set(getItemTag(), true)
                .build();
    }

    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return ITEM_TAG;
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
        return item;
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        return item.withTag(GUI_TAG, true);
    }
}
