package nub.wi1helm.module.modules.fishing.core;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.fishing.rods.FishingRodItem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishingManager {

    private final Map<UUID, FishingSession> sessions = new HashMap<>();
    private final Map<Tag<@NotNull Boolean>, FishingRodType> rodTypes = new HashMap<>();
    private final EntityManager entityManager;

    public FishingManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void registerRod(FishingRodType type) {
        rodTypes.put(type.tag(), type);
    }

    public void checkFishing(Player player, FishingRodItem rod) {
        if (sessions.get(player.getUuid()) == null) {
            startFishing(player, rod);
        } else {
            stopFishing(player, true);
        }
    }

    public void startFishing(Player player, FishingRodItem rod) {
        FishingSession session = new FishingSession(player, rod.getType(), entityManager);
        sessions.put(player.getUuid(), session);
        session.start();
        player.playSound(Sound.sound().type(SoundEvent.ENTITY_FISHING_BOBBER_THROW).build());
    }

    public void stopFishing(Player player, boolean reelIn) {
        FishingSession session = sessions.remove(player.getUuid());
        if (session == null) return;

        if (reelIn) session.reelIn();
        session.cancel();
        player.playSound(Sound.sound().type(SoundEvent.ENTITY_FISHING_BOBBER_RETRIEVE).build());
    }
}
