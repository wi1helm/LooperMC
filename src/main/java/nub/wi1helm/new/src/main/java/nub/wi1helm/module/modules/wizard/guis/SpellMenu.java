package nub.wi1helm.module.modules.wizard.guis;

import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryItemChangeEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.TooltipDisplay;
import nub.wi1helm.module.modules.wizard.items.wand.spells.Spell;
import nub.wi1helm.player.GamePlayer;

import java.util.List;
import java.util.function.Consumer;

public class SpellMenu extends Inventory {

    public SpellMenu(List<Spell> spells, Consumer<Spell> onSelect) {
        super(InventoryType.CHEST_5_ROW, Component.text("Choose Spell"));

        // Black pane for outline
        ItemStack blackPane = ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE)
                .set(DataComponents.ITEM_NAME, Component.empty())
                .set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.EMPTY)
                .build();

        int rows = 5;
        int columns = 9;

        // Fill only the outline (edges)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (row == 0 || row == rows - 1 || col == 0 || col == columns - 1) {
                    int slot = row * columns + col;
                    setItemStack(slot, blackPane);
                }
            }
        }

        // Place spells in their visual slots (inside the outline)
        for (Spell spell : spells) {
            setItemStack(spell.getVisualSlot(), spell.getVisualItem());
        }

        eventNode().addListener(InventoryPreClickEvent.class, event -> {
            ItemStack clickedItem = event.getClickedItem();
            final GamePlayer p = (GamePlayer) event.getPlayer();

            for (Spell spell : spells) {
                if (clickedItem.hasTag(spell.getTag())) {

                    if (!spell.canSwitch(p)) {
                        spell.onDenied(p);
                        p.closeInventory();
                        event.setCancelled(true);
                        return;
                    }

                    onSelect.accept(spell);
                    spell.onSwitch(p);

                    p.closeInventory();
                    event.setCancelled(true);
                    return;
                }
            }

            // Prevent taking outline panes
            event.setCancelled(true);
        });

    }

}
