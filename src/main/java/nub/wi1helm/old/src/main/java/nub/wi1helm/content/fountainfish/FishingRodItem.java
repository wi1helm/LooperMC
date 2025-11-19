package nub.wi1helm.content.fountainfish;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FishingRodItem {

    public static Tag<@NotNull Boolean> FISHING_ROD_TAG = Tag.Boolean("fisherman:rod");

    private Entity bobber;
    private boolean trown = false;
    public FishingRodItem() {

        PlayerEventNode.get().getNode().addListener(PlayerUseItemEvent.class, event -> {
           if(!event.getItemStack().hasTag(FISHING_ROD_TAG)) return;
           if (event.getHand() != PlayerHand.MAIN) return;
           event.getPlayer().sendMessage(event.getHand().toString());
           if (!trown) {
               event.getPlayer().sendMessage("Trown");
               this.bobber = new Entity(EntityType.FISHING_BOBBER);
               this.bobber.setInstance(event.getInstance(),event.getPlayer().getPosition().add(0,1.4,0));
               this.bobber.setVelocity(event.getPlayer().getPosition().facing().vec());
           } else {
               event.getPlayer().sendMessage("anti trown");
               this.bobber.remove();
               this.bobber = null;
           }


        });
    }

    public ItemStack getShopItem() {
        return ItemStack.builder(Material.FISHING_ROD).set(FISHING_ROD_TAG, true).set(DataComponents.LORE, List.of(Component.text("Cost: 5 Loopers"))).build();
    }

    public ItemStack getPlayerItem() {
        return ItemStack.builder(Material.FISHING_ROD).set(FISHING_ROD_TAG, true).build();
    }
}
