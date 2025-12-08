package nub.wi1helm.module.modules.fishing.inventory;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.fishing.core.FishingManager;
import nub.wi1helm.module.modules.fishing.rods.SimpleFishingRodItem;
import nub.wi1helm.player.GamePlayer;

public class FishermanShop extends Inventory {

    public FishermanShop(ItemManager itemManager, FishingManager fishingManager) {
        super(InventoryType.CHEST_5_ROW, Component.text("Fisherman"));

        SimpleFishingRodItem item = new SimpleFishingRodItem(itemManager, fishingManager);

        setItemStack(22, item.getGuiItem(itemManager));

        eventNode().addListener(InventoryPreClickEvent.class, event -> {
            event.setCancelled(true);

            ItemStack guiItem = event.getClickedItem();

            GamePlayer player = (GamePlayer) event.getPlayer();

            if (!guiItem.hasTag(SimpleFishingRodItem.FISHING_ROD_TAG)) return;

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
