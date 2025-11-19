package nub.wi1helm.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Represents a composite entity made up of one or more Minestom entities.
 * Provides common functionality for interacting, spawning, and removing entities,
 * as well as support for look-at-player behavior.
 */
public abstract class GameEntity {

    // -------------------- Abstract Methods --------------------

    /**
     * @return All Minestom entities that make up this composite entity.
     */
    public abstract List<Entity> getMinestomEntities();

    /**
     * Called when a player interacts with this entity.
     *
     * @param event The interaction event.
     */
    public abstract void onPlayerInteract(PlayerEntityInteractEvent event);

    /**
     * Spawn this entity in the given instance at the given position.
     * Subclasses must implement this because relationships between entities
     * (like passengers or offsets) require all entities to already exist in the instance.
     *
     * @param manager The entity manager handling this entity.
     * @param instance The instance to spawn in.
     * @param pos The position to spawn at.
     */
    public abstract void spawn(EntityManager manager, Instance instance, Pos pos);
    public abstract void spawn(EntityManager manager, Instance instance);

    /**
     * @return The tag uniquely identifying this type of entity.
     */
    public abstract @NotNull Tag<@NotNull String> getEntityTag();

    // -------------------- Removal --------------------

    /**
     * Removes all parts of this composite entity from the world.
     */
    public void remove() {
        getMinestomEntities().forEach(Entity::remove);
    }

    // -------------------- Look-at-Player Support --------------------

    /**
     * Tag used to store the player the entity should look at.
     */
    public static final @NotNull Tag<@NotNull UUID> LOOK_AT_PLAYER_FOCUS = Tag.UUID("look_at_player_focus");

    /**
     * Tag used to store configuration for entities that look at players.
     */
    public static final @NotNull Tag<@NotNull LookAtConfig> LOOK_AT_PLAYERS =
            Tag.Structure("look_at_players", LookAtConfig.class);

    /**
     * Enables the look-at-player behavior for the given entities.
     *
     * @param entities Entities to configure.
     * @param yaw The yaw rotation.
     * @param pitch The pitch rotation.
     * @param range The range within which to look at players.
     */
    public void enableLookAtPlayers(List<Entity> entities, float yaw, float pitch, float range) {
        LookAtConfig config = new LookAtConfig(yaw, pitch, range);
        entities.forEach(entity -> entity.setTag(LOOK_AT_PLAYERS, config));
    }

    // -------------------- Nested Classes --------------------

    /**
     * Configuration for entities that look at players.
     */
    public record LookAtConfig(float yaw, float pitch, float range) {}
}
