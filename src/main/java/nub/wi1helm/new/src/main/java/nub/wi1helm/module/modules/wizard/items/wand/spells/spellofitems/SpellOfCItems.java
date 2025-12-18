package nub.wi1helm.module.modules.wizard.items.wand.spells.spellofitems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.module.modules.chores.pondside.items.IronBoltItem;
import nub.wi1helm.module.modules.chores.pondside.items.MetalPlateItem;
import nub.wi1helm.module.modules.chores.pondside.items.WireSpoolItem;
import nub.wi1helm.module.modules.wizard.items.wand.spells.Spell;
import nub.wi1helm.player.GamePlayer;

import java.util.*;

public class SpellOfCItems implements Spell {

    private final Random random = new Random();

    @Override
    public String getId() {
        return "items";
    }

    @Override
    public Component getName() {
        return Component.text("Spell Of Items").color(NamedTextColor.GOLD);
    }

    @Override
    public void cast(PlayerUseItemEvent event) {
        final Player player = event.getPlayer();

        final Pos center = player.getPosition().add(player.getEyeHeight());
        final List<GameItem> itemPool = List.of(new IronBoltItem(), new MetalPlateItem(), new WireSpoolItem());


    }

    @Override
    public ItemStack getVisualItem() {
        return ItemStack.builder(Material.ITEM_FRAME).set(DataComponents.ITEM_NAME, getName()).build();
    }

    @Override
    public int getVisualSlot() {
        return 12;
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
}
