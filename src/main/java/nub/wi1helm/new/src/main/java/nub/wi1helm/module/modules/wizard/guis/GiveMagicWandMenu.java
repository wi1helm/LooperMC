package nub.wi1helm.module.modules.wizard.guis;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.wizard.items.wand.MrMagicItem;

public class GiveMagicWandMenu extends Inventory {

    private final ItemManager itemManager;

    public GiveMagicWandMenu(ItemManager itemManager) {
        super(InventoryType.CHEST_5_ROW, Component.empty());
        this.itemManager = itemManager;

        MrMagicItem wandItem = new MrMagicItem();

        setItemStack(22, wandItem.getGuiItem(itemManager));

        eventNode().addListener(InventoryCloseEvent.class, event -> {
            event.getPlayer().getInventory().addItemStack(wandItem.getPlayerItem(itemManager));

            event.getPlayer().showTitle(
                    Title.title(
                            Component.empty()
                                    .append(Component.text("[", NamedTextColor.DARK_GRAY))
                                    .append(Component.text("Mr.Magic", NamedTextColor.DARK_PURPLE))
                                    .append(Component.text("]", NamedTextColor.DARK_GRAY)),
                            Component.text("UUHhhh new owner, watafak man!", NamedTextColor.LIGHT_PURPLE)
                    )
            );

            // Optional sound effect
            event.getPlayer().playSound(Sound.sound()
                    .type(SoundEvent.ENTITY_PARROT_IMITATE_EVOKER)
                    .source(Sound.Source.PLAYER)
                    .pitch(1.0f)
                    .volume(1.0f)
                    .build());
        });
        eventNode().addListener(InventoryPreClickEvent.class, event -> {
            event.setCancelled(true);
            ItemStack itemStack = event.getClickedItem();
            if (!itemStack.hasTag(MrMagicItem.MR_MAGIC_TAG)) return;

            event.getPlayer().closeInventory();
        });
    }
}
