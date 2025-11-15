package nub.wi1helm.npc;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.trait.EntityInstanceEvent;
import org.jetbrains.annotations.NotNull;

public class NPCInteractedEvent implements EntityInstanceEvent {

    private final Player player;
    private final NPC npc;
    private final PlayerHand hand;
    private final Point interactPosition;

    public NPCInteractedEvent(Player player, NPC npc, PlayerHand hand, Point interactPosition) {
        this.player = player;
        this.npc = npc;
        this.hand = hand;
        this.interactPosition = interactPosition;
    }

    public NPCInteractedEvent(Player player, NPC npc) {
        this.player = player;
        this.npc = npc;
        this.hand = PlayerHand.MAIN;
        this.interactPosition = Pos.ZERO;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull NPC getNPC() {
        return this.npc;
    }

    public PlayerHand getHand() {
        return hand;
    }

    public Point getInteractPosition() {
        return interactPosition;
    }

    @Override
    public @NotNull Entity getEntity() {
        return npc;
    }
}
