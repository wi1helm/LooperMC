package nub.wi1helm.eventnodes.listeners;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.trait.EntityInstanceEvent;
import org.jetbrains.annotations.NotNull;

public class InteractEntityEvent implements EntityInstanceEvent {

    private final Player player;
    private final Entity entityTarget;
    private final PlayerHand hand;
    private final Point interactPosition;

    public InteractEntityEvent(Player player, Entity entityTarget, PlayerHand hand,
                                     Point interactPosition) {
        this.player = player;
        this.entityTarget = entityTarget;
        this.hand = hand;
        this.interactPosition = interactPosition;
    }

    public InteractEntityEvent(Player player, Entity entityTarget) {
        this.player = player;
        this.entityTarget = entityTarget;
        this.hand = PlayerHand.MAIN;
        this.interactPosition = Pos.ZERO;
    }


    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link Entity} with who {@link #getPlayer()} is interacting.
     *
     * @return the {@link Entity}
     */
    public Entity getTarget() {
        return entityTarget;
    }

    /**
     * Gets with which hand the player interacted with the entity.
     *
     * @return the hand
     */
    public PlayerHand getHand() {
        return hand;
    }

    /**
     * Gets the position at which the entity was interacted
     *
     * @see net.minestom.server.network.packet.client.play.ClientInteractEntityPacket.InteractAt
     * @return the interaction position
     */
    public Point getInteractPosition() {
        return interactPosition;
    }

    @Override
    public @NotNull Entity getEntity() {
        return entityTarget;
    }
}
