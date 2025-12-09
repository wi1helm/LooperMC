package nub.wi1helm.module.modules.wizard.items.wand.spells;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import nub.wi1helm.player.GamePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SpellOfTeleportation implements Spell{

    @Override
    public String getId() {
        return "tepleortation";
    }

    @Override
    public Component getName() {
        return Component.text("Spell of Teleportation", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public void cast(PlayerUseItemEvent event) {
        GamePlayer player = (GamePlayer) event.getPlayer();
        var instance = player.getInstance();
        if (instance == null) return;


        Pos startPos = player.getPosition();
        if (!player.useFood(4)) {
            player.sendActionBar(Component.text("Starving my dude!").color(NamedTextColor.RED));
            return;
        }
        var dir = startPos.direction().normalize(); // direction player is looking
        double maxDistance = 10; // max teleport distance
        double step = 0.5;      // check every 0.5 blocks

        for (double d = 0; d < maxDistance; d += step) {
            var checkPos = startPos.add(dir.mul(d));

            var block = instance.getBlock(checkPos.asBlockVec());
            if (!block.isAir()) {
                // Found a solid block, teleport slightly above it
                player.teleport(checkPos.add(0, 1.2, 0));
                return;
            }
        }

        // If no block found, just teleport max distance forward
        player.teleport(startPos.add(dir.mul(maxDistance)));
        player.playSound(Sound.sound().type(SoundEvent.ENTITY_ENDERMAN_TELEPORT).pitch(1.3F).build());

    }


    @Override
    public ItemStack getVisualItem() {
        return ItemStack.builder(Material.CHORUS_FRUIT)
                .set(DataComponents.ITEM_NAME, getName())
                .set(getTag(), true)
                .build();
    }

    @Override
    public int getVisualSlot() {
        return 11;
    }

    @Override
    public boolean canSwitch(GamePlayer player) {
        // Check if the player has at least 1 Ender Pearl
        return Arrays.stream(player.getInventory().getItemStacks())
                .anyMatch(item -> item.material() == Material.ENDER_PEARL);
    }

    @Override
    public void onDenied(GamePlayer player) {
        // Title with colored brackets and name
        Component title = Component.empty()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(Component.text("Mr.Magic", NamedTextColor.DARK_PURPLE))
                .append(Component.text("]", NamedTextColor.DARK_GRAY));

        // Subtitle with its own color
        Component subtitle = Component.text("Bring me an Ender Pearl first!", NamedTextColor.LIGHT_PURPLE);

        player.showTitle(Title.title(title, subtitle));

        // Optional sound
        player.playSound(Sound.sound()
                .type(SoundEvent.ENTITY_PARROT_IMITATE_EVOKER)
                .source(Sound.Source.PLAYER)
                .pitch(1.0f)
                .volume(1.0f)
                .build());
    }


    @Override
    public void onSwitch(GamePlayer player) {
        // Remove exactly 1 Ender Pearl
        ItemStack[] stacks = player.getInventory().getItemStacks();
        for (int i = 0; i < stacks.length; i++) {
            ItemStack item = stacks[i];
            if (!item.isAir() && item.material() == Material.ENDER_PEARL) {
                if (item.amount() > 1) {
                    player.getInventory().setItemStack(i, item.withAmount(item.amount() - 1));
                } else {
                    player.getInventory().setItemStack(i, ItemStack.AIR);
                }
                break;
            }
        }

        // MrMagic reacts
        player.sendActionBar(Component.text("MrMagic devours the pearl greedily.", NamedTextColor.LIGHT_PURPLE));
    }

}
