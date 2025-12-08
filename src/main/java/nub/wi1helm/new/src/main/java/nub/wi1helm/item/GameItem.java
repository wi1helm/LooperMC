package nub.wi1helm.item;

import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.item.ItemEntityMeta;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public interface GameItem {


    @NotNull Tag<@NotNull Boolean> getItemTag();

    void onPlayerUse(PlayerUseItemEvent event);
    void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event);
    void onDropItem(ItemDropEvent event);
    void onItemPickup(PickupItemEvent event);

    @NotNull ItemStack getPlayerItem(ItemManager itemManager);
    @NotNull ItemStack getGuiItem(ItemManager itemManager);

    default @NotNull ItemEntity spawnItemEntity(Player player, ItemStack item) {
        ItemEntity entity = new ItemEntity(item);
        entity.setPickupDelay(Duration.ofSeconds(2));
        entity.setInstance(player.getInstance(), player.getPosition().add(0,player.getEyeHeight(),0));
        entity.setVelocity(player.getPosition().direction().mul(4));

        return entity;
    }


}
