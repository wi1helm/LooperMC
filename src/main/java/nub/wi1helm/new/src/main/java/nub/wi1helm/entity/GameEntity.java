package nub.wi1helm.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.interaction.InteractionGraph;
import nub.wi1helm.entity.interaction.InteractionNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class GameEntity {

    // ------------------------------------------------------------------------
    // Interaction Graph (new system)
    // ------------------------------------------------------------------------

    private final InteractionGraph interactionGraph = new InteractionGraph();

    public GameEntity() {
        // Ensure every entity always has a root node
        interactionGraph.setRoot(InteractionNode.root());

    }

    public InteractionGraph getInteractionGraph() {
        return interactionGraph;
    }

    public void handleInteraction(PlayerEntityInteractEvent event) {
        interactionGraph.execute(event);
    }

    public abstract void setupInteractions();

    // ------------------------------------------------------------------------
    // Composite Entity Access
    // ------------------------------------------------------------------------

    public abstract List<Entity> getMinestomEntities();

    // ------------------------------------------------------------------------
    // Entity Spawning
    // ------------------------------------------------------------------------

    public abstract void spawn(EntityManager manager, Instance instance, Pos pos);

    public abstract void spawn(EntityManager manager, Instance instance);

    public abstract @NotNull Tag<@NotNull String> getEntityTag();

    // ------------------------------------------------------------------------
    // Removal
    // ------------------------------------------------------------------------

    public void remove() {
        getMinestomEntities().forEach(Entity::remove);
    }

    // ------------------------------------------------------------------------
    // Look-at-Player
    // ------------------------------------------------------------------------

    public static final @NotNull Tag<@NotNull UUID> LOOK_AT_PLAYER_FOCUS =
            Tag.UUID("look_at_player_focus");

    public static final @NotNull Tag<@NotNull LookAtConfig> LOOK_AT_PLAYERS =
            Tag.Structure("look_at_players", LookAtConfig.class);

    public void enableLookAtPlayers(List<Entity> entities, float yaw, float pitch, float range) {
        LookAtConfig config = new LookAtConfig(yaw, pitch, range);
        entities.forEach(entity -> entity.setTag(LOOK_AT_PLAYERS, config));
    }

    public record LookAtConfig(float yaw, float pitch, float range) { }
}
