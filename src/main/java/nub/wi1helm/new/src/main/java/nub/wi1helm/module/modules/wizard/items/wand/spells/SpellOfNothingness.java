package nub.wi1helm.module.modules.wizard.items.wand.spells;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import nub.wi1helm.player.GamePlayer;
import org.jetbrains.annotations.NotNull;

public class SpellOfNothingness implements Spell {

    private static final Component TITLE = Component.text("Spell of Nothingness", NamedTextColor.GRAY);
    private static final Component DESCRIPTION = Component.text("...nothing happens.", NamedTextColor.DARK_GRAY);

    @Override
    public String getId() {
        return "nothingness";
    }

    @Override
    public Component getName() {
        return TITLE;
    }

    @Override
    public void cast(PlayerUseItemEvent event) {
        Player player = event.getPlayer();
        player.sendActionBar(DESCRIPTION);
    }

    @Override
    public ItemStack getVisualItem() {
        return ItemStack.builder(Material.LIGHT_GRAY_STAINED_GLASS_PANE) // or any item you like
                .set(getTag(), true)
                .set(net.minestom.server.component.DataComponents.ITEM_NAME, TITLE)
                .build();
    }

    @Override
    public int getVisualSlot() {
        return 10; // for example, first slot in the menu
    }

    @Override
    public boolean canSwitch(GamePlayer player) {
        return true;
    }

    @Override
    public void onDenied(GamePlayer player) {

    }


    @Override
    public void onSwitch(GamePlayer player) {

    }


    @Override
    public Tag<@NotNull Boolean> getTag() {
        return Spell.super.getTag(); // uses the default implementation in Spell
    }
}
