package nub.wi1helm.module.modules.chores.pondside.gui;

import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

public class PartBoxInventory extends Inventory {
    public PartBoxInventory() {
        super(InventoryType.CHEST_5_ROW, Component.text("Part Box"));

    }
}
