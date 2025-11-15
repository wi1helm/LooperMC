package nub.wi1helm.content.chores.laundry;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.animal.tameable.ParrotMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.MathUtils; // Minestom's utility class for math is useful here
import nub.wi1helm.eventnodes.GoalEventNode;
import nub.wi1helm.eventnodes.PlayerEventNode;
import nub.wi1helm.instances.LoopInstance;

public class LaundryLine {

    // --- Capacity Tags & Constants ---
    // The capacity will now be stored directly on the LaundryLine instance,
    // or you could use a static map/database if these instances are volatile.
    public static final Tag<Integer> CAPACITY_TAG = Tag.Integer("line_capacity");
    private Integer maxCapacity = 10;
    // Max distance from the closest point on the line segment to be considered "close enough"
    private static final double MAX_INTERACT_DISTANCE = 4.0;

    // Visual points only (Parrots with Leash)
    private Entity point1 = new Entity(EntityType.PARROT);
    private Entity point2 = new Entity(EntityType.PARROT);

    // Capacity field to store the current count
    private int currentCapacity = 0;

    private final Pos pos1;
    private final Pos pos2;

    public LaundryLine(Pos point1, Pos point2, int capacity) {
        this.maxCapacity = capacity;
        this.pos1 = point1;
        this.pos2 = point2;

        // Optionally, initialize capacity from a persistent tag if you want this to survive chunk unloading
        // For simplicity here, we rely on the in-memory 'currentCapacity' field.

        // Setup the invisible points for the line visual
        this.point1.editEntityMeta(ParrotMeta.class, this::metadata);
        this.point2.editEntityMeta(ParrotMeta.class, this::metadata);

        // Add the listener for using an item with the clothes tag
        PlayerEventNode.get().getNode().addListener(PlayerUseItemEvent.class, this::handleUseClothesItem);
    }

    private void metadata(ParrotMeta meta) {
        meta.setHasNoGravity(true);
        meta.setInvisible(true);
    }

    public void spawn() {
        point1.setInstance(LoopInstance.get(), pos1);
        point2.setInstance(LoopInstance.get(), pos2);

        // Create the "line" visual using a leash
        this.point1.setLeashHolder(this.point2);
    }

    /**
     * Calculates the closest position on the line segment (P1 to P2) to a given point (P).
     * @param p The point to check distance from (Player position).
     * @param p1 The start of the line segment.
     * @param p2 The end of the line segment.
     * @return The Pos object representing the closest point on the segment.
     */
    private Pos closestPointOnLineSegment(Pos p, Pos p1, Pos p2) {
        double vx = p2.x() - p1.x();
        double vy = p2.y() - p1.y();
        double vz = p2.z() - p1.z();

        // Vector A = P - P1
        double ax = p.x() - p1.x();
        double ay = p.y() - p1.y();
        double az = p.z() - p1.z();

        // Projection length t = (A . V) / |V|^2
        double dotProduct = ax * vx + ay * vy + az * vz;
        double lineLengthSq = vx * vx + vy * vy + vz * vz;

        if (lineLengthSq == 0.0) {
            // Line segment is actually a point, return P1
            return p1;
        }

        double t = dotProduct / lineLengthSq;

        // Clamp t to [0, 1] to ensure the closest point is on the SEGMENT, not the infinite line
        t = MathUtils.clamp(t, 0.0, 1.0);

        // Closest point C = P1 + t * V
        double cx = p1.x() + t * vx;
        double cy = p1.y() + t * vy;
        double cz = p1.z() + t * vz;

        // Use the original world instance and orientation
        return new Pos(cx, cy, cz, p1.yaw(), p1.pitch());
    }

    /**
     * Handles the PlayerUseItemEvent for placing clothes on the line.
     */
    private void handleUseClothesItem(PlayerUseItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemStack();

        // 1. Check if the item is clothes
        if (itemInHand.hasTag(LaundryBasket.getClothesTag())) {

            // 2. Find the closest point on the line segment to the player
            Pos closestPoint = closestPointOnLineSegment(player.getPosition(), this.pos1, this.pos2);
            double distance = player.getPosition().distance(closestPoint);

            if (distance <= MAX_INTERACT_DISTANCE) {
                // 3. Check capacity (using the instance field)
                if (currentCapacity < this.maxCapacity) {
                    // Success: Increment capacity, remove item, and notify
                    currentCapacity++;
                    event.setCancelled(true);
                    // Remove one item from the player's hand
                    event.getPlayer().getInventory().setItemStack(event.getPlayer().getHeldSlot(), ItemStack.AIR);
                    spawnClothes(closestPoint, itemInHand);
                    player.sendMessage(Component.text("You hung up some clothes! (" + currentCapacity + "/" + maxCapacity + ")"));
                    GoalEventNode.get().hangClothes();
                } else {
                    // Line is full
                    player.sendMessage(Component.text("The laundry line is full!"));
                }

                // Always cancel the event if the item is clothes, to prevent default item behavior

            }
        }
    }

    private void spawnClothes(Pos pos, ItemStack item) {
        Entity entity = new Entity(EntityType.ITEM_DISPLAY);

        entity.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setItemStack(item);
            meta.setHasNoGravity(true);
        });
        entity.setInstance(LoopInstance.get(), pos);

    }
}