package nub.wi1helm.entity;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityDespawnEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import nub.wi1helm.core.GameService;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManager implements GameService {

    private final EventNode<@NotNull Event> node = EventNode.all("entity");
    private final Map<Integer, GameEntity> entities = new ConcurrentHashMap<>();

    public EntityManager() {
        onPlayerInteract();
        onPlayerMove();
        onEntityDespawn();
    }

    public void registerListeners() {
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

    public void register(GameEntity game, Entity entity) {
        entities.put(entity.getEntityId(), game);
    }

    private void onPlayerInteract() {
        node.addListener(PlayerEntityInteractEvent.class, event -> {
            if (event.getHand() != PlayerHand.MAIN) return;

            GameEntity gameEntity = entities.get(event.getTarget().getEntityId());
            if (gameEntity == null) return;

            gameEntity.handleInteraction(event); // <-- new system
        });
    }

    private void onPlayerMove() {
        node.addListener(PlayerMoveEvent.class, event -> {
            updateLookAtForNearbyNPCs(event.getPlayer());
        });
    }


    private void updateLookAtForNearbyNPCs(Player player) {

        for (var entry : entities.entrySet()) {
            int id = entry.getKey();
            GameEntity entity = entry.getValue();

            var main = entity.getMinestomEntities().getFirst();

            // Only handle NPCs close to the moving player (10 block radius)
            if (player.getDistance(main) > 10)
                continue;

            var config = main.getTag(GameEntity.LOOK_AT_PLAYERS);
            if (config == null) {
                continue;
            }

            float range = config.range();
            float pitch = config.pitch();
            float yaw = config.yaw();

            double pdist = player.getDistance(main);

            var focus = main.getTag(GameEntity.LOOK_AT_PLAYER_FOCUS);
            Player target = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(focus);

            if (target == null) {
                target = player;
            }

            double tdist = target.getDistance(main);

            // Reset if no players in range
            if (pdist > range && tdist > range) {
                entity.getMinestomEntities().forEach(e -> e.setView(yaw, pitch));
                continue;
            }

            // If moving player is closer than current target → change focus
            if (pdist <= tdist) {
                entity.getMinestomEntities().forEach(e -> {
                    e.lookAt(player);
                    e.setTag(GameEntity.LOOK_AT_PLAYER_FOCUS, player.getUuid());
                });
            }
        }
    }



    private void onEntityDespawn() {
        node.addListener(EntityDespawnEvent.class, e -> {
            entities.remove(e.getEntity().getEntityId());
        });
    }
}
