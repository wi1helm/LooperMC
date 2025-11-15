package nub.wi1helm.eventnodes.listeners;

import net.minestom.server.ServerFlag;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.listener.UseEntityListener;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import nub.wi1helm.npc.NPC;
import nub.wi1helm.npc.NPCInteractedEvent;
import nub.wi1helm.npc.NPCManager;

public class EntityInteractionListener {

    public static void entityInteractionListener(ClientInteractEntityPacket packet, Player player) {
        // Call Minestom's built-in interaction logic first
        UseEntityListener.useEntityListener(packet, player);
        useCustomEntityListener(packet, player);
        // Then run our NPC logic
        useNPCListener(packet, player);
    }

    private static void useCustomEntityListener(ClientInteractEntityPacket packet, Player player) {
        final Entity entity = player.getInstance().getEntityById(packet.targetId());
        if (entity == null || !entity.isViewer(player))
            return;

        // Ensure entity is close enough to interact
        if (ServerFlag.ENFORCE_INTERACTION_LIMIT) {
            final double maxDistanceSquared = Math.pow(player.getAttributeValue(Attribute.ENTITY_INTERACTION_RANGE) + 1, 2);
            if (getDistSquared(player, entity) > maxDistanceSquared)
                return;
        }

        // Determine interaction type
        ClientInteractEntityPacket.Type type = packet.type();
        if (type instanceof ClientInteractEntityPacket.Attack) {
            EventDispatcher.call(new InteractEntityEvent(player, entity));
        } else if (type instanceof ClientInteractEntityPacket.InteractAt interactAt) {
            Point hitPos = new Vec(interactAt.targetX(), interactAt.targetY(), interactAt.targetZ());
            EventDispatcher.call(new InteractEntityEvent(player, entity, interactAt.hand(), hitPos));
        }
    }

    private static void useNPCListener(ClientInteractEntityPacket packet, Player player) {
        final Entity entity = player.getInstance().getEntityById(packet.targetId());
        if (entity == null || !entity.isViewer(player))
            return;

        // Ensure entity is close enough to interact
        if (ServerFlag.ENFORCE_INTERACTION_LIMIT) {
            final double maxDistanceSquared = Math.pow(player.getAttributeValue(Attribute.ENTITY_INTERACTION_RANGE) + 1, 2);
            if (getDistSquared(player, entity) > maxDistanceSquared)
                return;
        }

        // Check if it's an NPC
        NPC npc = NPCManager.getNPC(entity.getEntityId());
        if (npc == null)
            return;

        // Determine interaction type
        ClientInteractEntityPacket.Type type = packet.type();
        if (type instanceof ClientInteractEntityPacket.Attack) {
            EventDispatcher.call(new NPCInteractedEvent(player, npc));
        } else if (type instanceof ClientInteractEntityPacket.InteractAt interactAt) {
            Point hitPos = new Vec(interactAt.targetX(), interactAt.targetY(), interactAt.targetZ());
            EventDispatcher.call(new NPCInteractedEvent(player, npc, interactAt.hand(), hitPos));
        }
    }

    private static double getDistSquared(Player player, Entity entity) {
        final Pos playerPos = player.getPosition();
        final double eyeHeight = player.getEyeHeight();
        final double px = playerPos.x();
        final double py = playerPos.y() + eyeHeight;
        final double pz = playerPos.z();

        final BoundingBox box = entity.getBoundingBox();
        final double halfWidth = box.width() / 2;
        final double height = box.height();
        final Pos entityPos = entity.getPosition();

        final double minX = entityPos.x() - halfWidth;
        final double maxX = entityPos.x() + halfWidth;
        final double minY = entityPos.y();
        final double maxY = entityPos.y() + height;
        final double minZ = entityPos.z() - halfWidth;
        final double maxZ = entityPos.z() + halfWidth;

        final double clampX = Math.max(minX, Math.min(px, maxX));
        final double clampY = Math.max(minY, Math.min(py, maxY));
        final double clampZ = Math.max(minZ, Math.min(pz, maxZ));

        final double dx = px - clampX;
        final double dy = py - clampY;
        final double dz = pz - clampZ;
        return dx * dx + dy * dy + dz * dz;
    }
}
