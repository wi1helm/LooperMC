package nub.wi1helm.module.modules.wizard.items.wand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.wizard.guis.SpellMenu;
import nub.wi1helm.module.modules.wizard.items.wand.spells.Spell;
import nub.wi1helm.module.modules.wizard.items.wand.spells.SpellOfNothingness;
import nub.wi1helm.module.modules.wizard.items.wand.spells.Spells;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MrMagicItem implements GameItem {

    public static final Tag<@NotNull Boolean> MR_MAGIC_TAG =
            Tag.Boolean("module:wizard:item:mrmagic");

    public static final Tag<@NotNull String> SELECTED_SPELL_TAG =
            Tag.String("module:wizard:item:selected_spell");

    private static final NamedTextColor MAGIC_COLOR = NamedTextColor.AQUA;

    @Override
    public @NotNull Tag<@NotNull Boolean> getItemTag() {
        return MR_MAGIC_TAG;
    }

    /* ----------------------------------------------------------
     *   MAIN USE HANDLER
     * ---------------------------------------------------------- */
    @Override
    public void onPlayerUse(PlayerUseItemEvent event) {
        final Player player = event.getPlayer();

        if (player.isSneaking()) {
            onChooseSpell(event);
        } else {
            onCastSpell(event);
        }
    }

    /* ----------------------------------------------------------
     *   SPELL CHOOSING
     * ---------------------------------------------------------- */
    private void onChooseSpell(PlayerUseItemEvent event) {
        final Player player = event.getPlayer();

        player.openInventory(new SpellMenu(Spells.ALL, spell -> {

            // Find MrMagic in inventory
            int wandSlot = -1;
            ItemStack wandItem = null;

            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItemStack(i);
                if (!item.isAir() && Boolean.TRUE.equals(item.getTag(MR_MAGIC_TAG))) {
                    wandSlot = i;
                    wandItem = item;
                    break;
                }
            }

            if (wandSlot == -1) {
                player.closeInventory();
                return;
            }

            // Update lore + spell tag
            ItemStack updated = wandItem.with(builder -> {
                builder.set(DataComponents.LORE, getLore(spell));
                builder.set(SELECTED_SPELL_TAG, spell.getId());
            });

            player.getInventory().setItemStack(wandSlot, updated);
            player.closeInventory();
        }));
    }

    /* ----------------------------------------------------------
     *   SPELL CASTING
     * ---------------------------------------------------------- */
    private void onCastSpell(PlayerUseItemEvent event) {
        ItemStack item = event.getItemStack();
        String spellId = item.getTag(SELECTED_SPELL_TAG);

        Spell spell = Spells.fromId(spellId);
        if (spell == null) spell = Spells.NOTHINGNESS;

        spell.cast(event);
    }

    /* ----------------------------------------------------------
     *   OTHER ITEM EVENTS
     * ---------------------------------------------------------- */
    @Override
    public void onPlayerChangeHeldSlot(PlayerChangeHeldSlotEvent event) {}

    @Override
    public void onDropItem(ItemDropEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onItemPickup(PickupItemEvent event) {}

    /* ----------------------------------------------------------
     *   ITEM CREATION
     * ---------------------------------------------------------- */
    @Override
    public @NotNull ItemStack getPlayerItem(ItemManager itemManager) {
        itemManager.registerItem(this);

        Spell initial = Spells.NOTHINGNESS;

        return ItemStack.builder(Material.BREEZE_ROD)
                .set(getItemTag(), true)
                .set(DataComponents.ITEM_NAME,
                        Component.text("Mr.Magic").color(MAGIC_COLOR))
                .set(DataComponents.LORE, getLore(initial))
                .set(SELECTED_SPELL_TAG, initial.getId())
                .build();
    }

    @Override
    public @NotNull ItemStack getGuiItem(ItemManager itemManager) {
        Spell initial = Spells.NOTHINGNESS;

        List<Component> lore = new ArrayList<>(getLore(initial));
        lore.add(Component.empty());
        lore.add(Component.text("Click to claim!")
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));

        return ItemStack.builder(Material.BREEZE_ROD)
                .set(getItemTag(), true)
                .set(DataComponents.ITEM_NAME,
                        Component.text("Mr.Magic").color(MAGIC_COLOR))
                .set(DataComponents.LORE, lore)
                .build();
    }

    /* ----------------------------------------------------------
     *   LORE HELPER
     * ---------------------------------------------------------- */
    private @NotNull List<Component> getLore(Spell spell) {
        return List.of(
                Component.empty(),

                Component.text("Mr.Magic hums and twitches in your hand.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true),
                Component.text("~ He expects entertainment and chaos")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true),

                Component.empty(),
                Component.text("Spell: ")
                        .color(NamedTextColor.WHITE)
                        .append(spell.getName()),
                Component.empty(),

                Component.text("Cast: Click")
                        .color(NamedTextColor.AQUA)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Mr.Magic performs the chosen spell.")
                        .color(NamedTextColor.GRAY),

                Component.empty(),
                Component.text("Choose Spell: Shift + Click")
                        .color(NamedTextColor.AQUA),
                Component.text("Mr.Magic decides if he'll allow it...")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true),

                Component.empty(),
                Component.text("LIVING LEGEND")
                        .color(MAGIC_COLOR)
                        .decorate(TextDecoration.BOLD)
        );
    }
}
