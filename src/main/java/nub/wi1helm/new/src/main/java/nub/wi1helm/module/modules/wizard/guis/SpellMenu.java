package nub.wi1helm.module.modules.wizard.guis;

import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

public class SpellMenu extends Inventory {
    public SpellMenu() {
        super(InventoryType.CHEST_5_ROW, Component.text("Choose Spell"));
    }
}
