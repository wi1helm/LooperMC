package nub.wi1helm.module.modules.fishfountain;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.player.GamePlayer;

public class FishermanGUI extends Inventory {

    public FishermanGUI(ItemManager itemManager) {
        super(InventoryType.CHEST_5_ROW, Component.text("Fisherman"));

        FishingRodItem item = new FishingRodItem(itemManager);

        setItemStack(22, item.getGuiItem(itemManager));

        eventNode().addListener(InventoryPreClickEvent.class, event -> {
            event.setCancelled(true);

            ItemStack guiItem = event.getClickedItem();

            GamePlayer player = (GamePlayer) event.getPlayer();

            if (!guiItem.hasTag(FishingRodItem.FISHING_ROD_TAG)) return;

            if (!(player.getLoopers() >= 5)) {
                player.sendMessage("Fucking poor");
                player.closeInventory();
                return;
            }

            player.setLoopers(player.getLoopers() - 5);

            player.getInventory().addItemStack(item.getPlayerItem(itemManager));
        });
    }
}
