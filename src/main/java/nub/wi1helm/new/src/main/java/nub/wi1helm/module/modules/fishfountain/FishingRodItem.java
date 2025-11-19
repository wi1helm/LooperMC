package nub.wi1helm.module.modules.fishfountain;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.other.FishingHookMeta;
import net.minestom.server.event.entity.EntityDespawnEvent;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FishingRodItem implements GameItem {

    public static final Tag<@NotNull Boolean> FISHING_ROD_TAG = Tag.Boolean("fisherman:rod");

    private static final Map<UUID, FishingHook> hooks = new HashMap<>();

    private final ItemManager itemManager;

    public FishingRodItem(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return FISHING_ROD_TAG;
    }

    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {
        final Player player = event.getPlayer();

        if (hooks.containsKey(player.getUuid())) {
            FishingHook hook = hooks.get(player.getUuid());
            boolean caught = hook.removeAndCheckFish();
            if (caught) {
                player.getInventory().addItemStack(new FishItem().getPlayerItem(itemManager));
                player.sendActionBar(MiniMessage.miniMessage().deserialize("<green>You Caught a fish</green>"));
                player.playSound(Sound.sound().type(SoundEvent.ENTITY_FISHING_BOBBER_SPLASH).source(Sound.Source.AMBIENT).pitch(0.8F).volume(1F).build());
            }
            hooks.remove(player.getUuid());
        } else {
            FishingHook hook = new FishingHook(player);
            hook.spawn(player.getInstance(), player.getPosition().add(0,player.getEyeHeight(),0));
            hooks.put(player.getUuid(), hook);
        }
    }

    @Override
    public void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        final Player player = event.getPlayer();

        if (hooks.containsKey(player.getUuid())) {
            FishingHook hook = hooks.get(player.getUuid());
            hook.remove();
            hooks.remove(player.getUuid());
        }
    }

    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        itemManager.registerItem(this);
        return ItemStack.builder(Material.FISHING_ROD)
                .set(FISHING_ROD_TAG, true)
                .set(DataComponents.ITEM_NAME, Component.text("Simple Fishing Rod").color(NamedTextColor.WHITE))
                .set(DataComponents.LORE, List.of(
                        Component.empty(),
                        Component.text("If at first you don't succeed, fishing").color(NamedTextColor.GRAY),
                        Component.text("is a better choice than skydiving").color(NamedTextColor.GRAY),
                        Component.text("- Reddit user").color(NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("COMMON").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
                ))
                .build();
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        return ItemStack.builder(Material.FISHING_ROD)
                .set(FISHING_ROD_TAG, true)
                .set(DataComponents.ITEM_NAME, Component.text("Simple Fishing Rod").color(NamedTextColor.WHITE))
                .set(DataComponents.LORE, List.of(
                        Component.empty(),
                        Component.text("If at first you don't succeed, fishing").color(NamedTextColor.GRAY),
                        Component.text("is a better choice than skydiving").color(NamedTextColor.GRAY),
                        Component.text("- Reddit user").color(NamedTextColor.GRAY),
                        Component.text("Cost: ", NamedTextColor.WHITE)
                                .append(Component.text("5\uD83D\uDD01", NamedTextColor.GOLD))
                                .decoration(TextDecoration.ITALIC, false),
                        Component.empty(),
                        Component.text("NOT RARE").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
                ))
                .build();
    }

}