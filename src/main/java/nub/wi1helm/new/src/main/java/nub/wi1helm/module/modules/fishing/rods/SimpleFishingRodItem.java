package nub.wi1helm.module.modules.fishing.rods;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.fishing.core.FishingManager;
import nub.wi1helm.module.modules.fishing.core.FishingRodType;
import nub.wi1helm.module.modules.fishing.core.FishingRodType.BehaviorEntry; // Import the new structure
import nub.wi1helm.module.modules.fishing.core.behavior.VanillaFishWithEntityBehavior;
import nub.wi1helm.module.modules.fishing.core.loot.WeightedLootTable;
import nub.wi1helm.module.modules.fishing.items.FishItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleFishingRodItem implements FishingRodItem {

    public static final Tag<@NotNull Boolean> FISHING_ROD_TAG = Tag.Boolean("module:fishing:item:simplefishingrod");

    private final ItemManager itemManager;
    private final FishingManager fishingManager;
    private final FishingRodType rodType;

    public SimpleFishingRodItem(ItemManager itemManager, FishingManager fishingManager) {
        this.itemManager = itemManager;
        this.fishingManager = fishingManager;

        // --- 1. Define Loot Tables ---
        // Loot table for catching a fish
        WeightedLootTable fishLootTable = new WeightedLootTable(itemManager)
                // Assuming FishItem is a GameItem, add it with a high weight
                .add(new FishItem(), 100.0);

        // --- 2. Define Behavior Entries ---

        // This is the only behavior, designed for catching fish entities
        BehaviorEntry fishEntry = new BehaviorEntry(
                // Factory (Passed the necessary arguments, including the entry itself)
                VanillaFishWithEntityBehavior::new,

                // LootTable specific to this behavior (always fish)
                fishLootTable,

                // Stats for this Behavior (Min/Max Particle Ticks)
                100, // minParticleTicks
                180, // maxParticleTicks

                // Stats for this Behavior (Spawn Distance)
                2.0, // spawnDistanceMin
                10.0, // spawnDistanceMax

                // Weight (100% chance since it's the only one)
                100.0
        );

        List<BehaviorEntry> entries = List.of(fishEntry);
        double totalWeight = entries.stream().mapToDouble(BehaviorEntry::weight).sum();

        // --- 3. Create and Register Rod Type ---

        this.rodType = new FishingRodType(
                FISHING_ROD_TAG,

                // Common Rod Stats (Min/Max Wait Time)
                60, // minWaitTime (Min bite time for the rod)
                120, // maxWaitTime (Max bite time for the rod)
                20,  // catchWindowTicks (Time player has to react to the bite)

                // Weighted Behavior List
                entries,
                totalWeight
        );

        fishingManager.registerRod(rodType);
    }

    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return FISHING_ROD_TAG;
    }

    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {
        Player player = event.getPlayer();
        fishingManager.checkFishing(player, this);
    }

    @Override
    public void onDropItem(net.minestom.server.event.item.ItemDropEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onItemPickup(net.minestom.server.event.item.PickupItemEvent event) {}

    @Override
    public void onPlayerChangeHeldSlot(net.minestom.server.event.player.PlayerChangeHeldSlotEvent event) {
        fishingManager.stopFishing(event.getPlayer(), false);
    }

    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        itemManager.registerItem(this);
        return ItemStack.builder(Material.FISHING_ROD)
                .set(getItemTag(), true)
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
        itemManager.registerItem(this);
        return ItemStack.builder(Material.FISHING_ROD)
                .set(getItemTag(), true)
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

    @Override
    public @NotNull FishingRodType getType() {
        return rodType;
    }
}
