package nub.wi1helm.module.modules.chores.laundry;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EquipmentSlotGroup;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.AttributeList;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.Utils;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ClothesItem implements GameItem {

    public static final Tag<@NotNull Boolean> TAG =
            Tag.Boolean("module:laundry:item:clothes");

    private static final List<Material> MATERIALS = List.of(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    );

    private final GoalManager goalManager;
    private final Set<LaundryLine> lines;

    public ClothesItem(GoalManager goalManager, Set<LaundryLine> lines) {
        this.goalManager = goalManager;
        this.lines = lines;
    }

    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return TAG;
    }

    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {
        event.setCancelled(true);
        final Player player = event.getPlayer();
        // 1️⃣Only handle if the player is holding a clothes item
        ItemStack item = event.getItemStack();
        if (!item.hasTag(TAG)) return;

        // 2️⃣ Find the nearest LaundryLine within a max distance (e.g., 4 blocks)
        double maxDistance = 4.0;
        LaundryLine nearest = getNearestLine(event.getPlayer().getPosition(), maxDistance);

        if (nearest == null) {
            return;
        }

        boolean success = nearest.addClothesDisplay(item);
        if (success) {

            player.sendActionBar(MiniMessage.miniMessage().deserialize(
                    "<green>🧺 You hung the clothes!</green>"));

            // Play satisfying pickup/hang sounds
            player.playSound(Sound.sound()
                    .type(SoundEvent.ENTITY_ITEM_PICKUP)
                    .source(Sound.Source.PLAYER)
                    .pitch(1.0F)
                    .volume(1.0F)
                    .build());
            player.playSound(Sound.sound()
                    .type(SoundEvent.BLOCK_WOOD_PLACE)
                    .source(Sound.Source.BLOCK)
                    .pitch(1.2F)
                    .volume(0.7F)
                    .build());

            // Remove item from hand
            player.getInventory().setItemStack(event.getPlayer().getHeldSlot(), ItemStack.AIR);
            goalManager.incrementGoal(ServerGoals.TOWN_CHORES, 1);
        } else {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>🧺 The laundry line is full!</red>"));
            player.playSound(Sound.sound()
                    .type(SoundEvent.ENTITY_VILLAGER_NO)
                    .source(Sound.Source.PLAYER)
                    .pitch(0.8F)
                    .volume(1.0F)
                    .build());
        }


    }

    @Override
    public void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event) {

    }


    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        itemManager.registerItem(this);
        return generateClothes();
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        // GUI version uses same item, but could be simplified later
        return generateClothes();
    }

    private ItemStack generateClothes() {
        Material mat = Utils.randomElement(MATERIALS);

        return ItemStack.builder(mat)
                .amount(1)
                .set(DataComponents.LORE, List.of(Component.empty(), Component.text("NOT RARE").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)))
                .set(DataComponents.ATTRIBUTE_MODIFIERS, AttributeList.EMPTY.with(new AttributeList.Modifier(Attribute.ARMOR, new AttributeModifier("f", 0, AttributeOperation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.ANY, new AttributeList.Display.Hidden())))
                .set(DataComponents.DYED_COLOR, Utils.randomColor())
                .set(TAG, true)
                .build();
    }

    public LaundryLine getNearestLine(Pos playerPos, double maxDistance) {
        LaundryLine best = null;
        double bestDistance = maxDistance;

        for (LaundryLine line : lines) {
            double dist = line.distanceToLine(playerPos);
            if (dist < bestDistance) {
                best = line;
                bestDistance = dist;
            }
        }
        return best;
    }

}
