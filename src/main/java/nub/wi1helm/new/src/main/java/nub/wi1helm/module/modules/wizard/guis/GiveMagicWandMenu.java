package nub.wi1helm.module.modules.wizard.guis;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mayor.items.MayorLetterItem;
import nub.wi1helm.module.modules.wizard.items.MagicWandItem;

public class GiveMagicWandMenu extends Inventory {

    private final ItemManager itemManager;

    public GiveMagicWandMenu(ItemManager itemManager) {
        super(InventoryType.CHEST_5_ROW, Component.empty());
        this.itemManager = itemManager;

        MagicWandItem wandItem = new MagicWandItem();

        setItemStack(22, wandItem.getGuiItem(itemManager));

        eventNode().addListener(InventoryCloseEvent.class, event -> {
            event.getPlayer().getInventory().addItemStack(wandItem.getPlayerItem(itemManager));
        });
        eventNode().addListener(InventoryPreClickEvent.class, event -> {
            event.setCancelled(true);
            ItemStack itemStack = event.getClickedItem();
            if (!itemStack.hasTag(MagicWandItem.MAGIC_WAND_TAG)) return;

            event.getPlayer().closeInventory();
        });
    }
}
