package nub.wi1helm.module.modules.grasseater;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.util.ARGBLike;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.GameModule;
import org.jetbrains.annotations.NotNull;

import javax.naming.Name;

public class GrassEaterModule implements GameModule {

    private final GoalManager goalManager;

    public GrassEaterModule(GoalManager goalManager) {
        this.goalManager = goalManager;
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:grasseater");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {
        if (!goalManager.hasActiveGoal(ServerGoals.GRASS_EATER)) return;

        node.addListener(PlayerBlockInteractEvent.class, event -> {
            if (!event.getBlock().compare(Block.GRASS_BLOCK)) return;
            final Player player = event.getPlayer();

            player.getInstance().setBlock(event.getBlockPosition(), Block.MYCELIUM);
            player.sendActionBar(Component.text("Nom Nom").color(NamedTextColor.GREEN));
            player.playSound(Sound.sound().type(SoundEvent.ENTITY_PLAYER_BURP).build());


            double current = player.getAttribute(Attribute.SCALE).getBaseValue();
            player.getAttribute(Attribute.SCALE).setBaseValue(current + 0.01);

            goalManager.incrementGoal(ServerGoals.GRASS_EATER, 1);

        });
    }
}
