package nub.wi1helm.module.modules.chores.laundry.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.animal.tameable.ParrotMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class LaundryLine extends GameEntity {

    private final Entity p1 = new Entity(EntityType.PARROT);
    private final Entity p2 = new Entity(EntityType.PARROT);

    private final Pos pos1;
    private final Pos pos2;

    // --- Capacity ---
    private final int maxCapacity;
    private int currentCapacity = 0;

    // Display entities for hung clothes
    private final List<Entity> clothesDisplays = new ArrayList<>();

    public LaundryLine(Pos pos1, Pos pos2, int maxCapacity) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.maxCapacity = Math.max(1, maxCapacity);

        p1.editEntityMeta(ParrotMeta.class, meta -> {
            meta.setHasNoGravity(true);
            meta.setInvisible(true);
        });

        p2.editEntityMeta(ParrotMeta.class, meta -> {
            meta.setHasNoGravity(true);
            meta.setInvisible(true);
        });
    }

    // ------------------------------------------------------
    // --- BASIC ENTITY SETUP ---
    // ------------------------------------------------------

    @Override
    public void setupInteractions() {

    }

    @Override
    public List<Entity> getMinestomEntities() {
        List<Entity> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.addAll(clothesDisplays);
        return list;
    }


    @Override
    public void spawn(EntityManager manager, Instance instance, Pos ignored) {
        spawn(manager, instance);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        p1.setInstance(instance, pos1);
        p2.setInstance(instance, pos2);

        // Connect with leash
        p1.setLeashHolder(p2);

        manager.register(this, p1);
    }

    // ------------------------------------------------------
    // --- CAPACITY & DISPLAY MANAGEMENT ---
    // ------------------------------------------------------

    public boolean isFull() {
        return currentCapacity >= maxCapacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    /** Adds a clothes item display if there's room. */
    public boolean addClothesDisplay(ItemStack item) {
        if (isFull()) return false;

        int slot = currentCapacity;

        Entity display = new Entity(EntityType.ITEM_DISPLAY);
        display.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setHasNoGravity(true);
            meta.setItemStack(item);
        });

        clothesDisplays.add(display);
        currentCapacity++;

        display.setInstance(p1.getInstance(), getSlotPos(slot));

        return true;
    }


    private float calculateYaw(Pos from, Pos to) {
        double dx = to.x() - from.x();
        double dz = to.z() - from.z();
        return (float) Math.toDegrees(Math.atan2(-dx, dz)); // Minestom convention
    }


    public double distanceToLine(Pos p) {
        Pos closest = closestPointOnLineSegment(p, pos1, pos2);
        return closest.distance(p);
    }


    private Pos closestPointOnLineSegment(Pos p, Pos a, Pos b) {
        double abX = b.x() - a.x();
        double abY = b.y() - a.y();
        double abZ = b.z() - a.z();

        double apX = p.x() - a.x();
        double apY = p.y() - a.y();
        double apZ = p.z() - a.z();

        double abLenSq = abX * abX + abY * abY + abZ * abZ;
        if (abLenSq == 0) {
            return a; // degenerate line
        }

        double dot = apX * abX + apY * abY + apZ * abZ;
        double t = dot / abLenSq;

        // clamp to segment
        if (t < 0) t = 0;
        else if (t > 1) t = 1;

        return new Pos(
                a.x() + abX * t,
                a.y() + abY * t,
                a.z() + abZ * t,
                a.yaw(),
                a.pitch()
        );
    }


    // ------------------------------------------------------
    // --- POSITIONING ---
    // ------------------------------------------------------

    private Pos getSlotPos(int slot) {
        double t = (slot + 0.5) / maxCapacity;

        double x = pos1.x() + (pos2.x() - pos1.x()) * t;
        double y = pos1.y() + (pos2.y() - pos1.y()) * t + 0.25;
        double z = pos1.z() + (pos2.z() - pos1.z()) * t;

        float yaw = calculateYaw(pos1, pos2);
        float pitch = 0; // Usually horizontal, adjust if you want tilt

        return new Pos(x, y, z, yaw, pitch);
    }


    @Override
    public @NotNull Tag<String> getEntityTag() {
        return Tag.String("module:laundry:line");
    }
}
