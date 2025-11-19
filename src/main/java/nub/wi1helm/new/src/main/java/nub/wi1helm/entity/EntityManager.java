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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManager implements GameService {

    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

    private final EventNode<@NotNull Event> node = EventNode.all("entity");
    private final Map<Integer, GameEntity> entities = new ConcurrentHashMap<>();


    public EntityManager() {

        onPlayerInteract();
        onPlayerMove();
        onEntityDespawn();
        logger.info("EntityManager initialized.");
    }

    @Override
    public void registerListeners() {
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

    public void register(GameEntity game, Entity entity) {
        entities.put(entity.getEntityId(), game);
        logger.info("Registered: {}", game.getEntityTag().getKey());
    }

    private void onPlayerInteract() {
        node.addListener(PlayerEntityInteractEvent.class, event -> {
            final Entity entity = event.getTarget();
            final PlayerHand hand = event.getHand();
            if (hand != PlayerHand.MAIN) return;

            final GameEntity gameEntity = entities.get(entity.getEntityId());
            if (gameEntity == null) return;

            gameEntity.onPlayerInteract(event);
        });
    }

    private void onPlayerMove() {
        node.addListener(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();

            entities.forEach((integer, entity) -> {
                Entity main = entity.getMinestomEntities().getFirst();
                GameEntity.LookAtConfig config = main.getTag(GameEntity.LOOK_AT_PLAYERS);

                if (config == null) return;

                double range = config.range();
                double pitch = config.pitch();
                double yaw = config.yaw();
                Player target = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(main.getTag(GameEntity.LOOK_AT_PLAYER_FOCUS));

                if (target == null || target.getUuid().equals(player.getUuid())) {
                    target = player;
                }
                if (player.getDistance(main) >= range && target.getDistance(main) >= range) {
                    entity.getMinestomEntities().forEach(e -> e.setView((float) yaw, (float) pitch));
                    return;
                }
                if (player.getDistance(main) < target.getDistance(main) || player.getDistance(main) >= range)
                    return;

                entity.getMinestomEntities().forEach(e -> {
                    e.lookAt(player);
                    e.setTag(GameEntity.LOOK_AT_PLAYER_FOCUS, player.getUuid());
                });
            });
        });
    }






    private void onEntityDespawn() {
        node.addListener(EntityDespawnEvent.class, entityDespawnEvent -> {
           final Entity entity = entityDespawnEvent.getEntity();
           entities.remove(entity.getEntityId());
        });
    }
}
