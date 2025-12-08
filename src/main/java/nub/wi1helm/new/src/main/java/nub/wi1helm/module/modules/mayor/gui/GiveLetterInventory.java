package nub.wi1helm.module.modules.mayor.gui;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mayor.items.MayorLetterItem;

public class GiveLetterInventory extends Inventory {

    private final ItemManager itemManager;

    public GiveLetterInventory(ItemManager itemManager) {
        super(InventoryType.CHEST_5_ROW, Component.empty());
        this.itemManager = itemManager;

        MayorLetterItem letterItem = new MayorLetterItem();

        setItemStack(22, letterItem.getGuiItem(itemManager));

        eventNode().addListener(InventoryCloseEvent.class, event -> {
            event.getPlayer().getInventory().addItemStack(letterItem.getPlayerItem(itemManager));
        });
        eventNode().addListener(InventoryPreClickEvent.class, event -> {
            event.setCancelled(true);
            ItemStack itemStack = event.getClickedItem();
            if (!itemStack.hasTag(MayorLetterItem.MAYOR_LETTER)) return;

            event.getPlayer().closeInventory();
        });
    }
}
