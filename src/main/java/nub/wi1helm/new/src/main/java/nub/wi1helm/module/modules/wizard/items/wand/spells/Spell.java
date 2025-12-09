package nub.wi1helm.module.modules.wizard.items.wand.spells;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import nub.wi1helm.player.GamePlayer;
import org.jetbrains.annotations.NotNull;

public interface Spell {
    String getId(); // unique id for the spell
    Component getName();
    void cast(PlayerUseItemEvent event);
    ItemStack getVisualItem();
    int getVisualSlot();

    boolean canSwitch(GamePlayer player);               // Can the player select this spell?
    void onDenied(GamePlayer player);
    void onSwitch(GamePlayer player);                   // Optional: side effects on successful switch


    default Tag<@NotNull Boolean> getTag() {
        return Tag.Boolean("wizard:spell:" + getId());
    }
}
