package nub.wi1helm.module.modules.fishing.core.behavior.fish;

import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class Fish extends GameEntity {

    protected final Entity modelEntity;
    protected final PlayerSkin modelSkin;
    protected final double finalScale; // Renamed 'scale' to 'finalScale' for clarity
    protected final double depth;

    public Fish(EntityType entityType, PlayerSkin modelSkin, double finalScale, double depth) {
        super();
        this.modelSkin = modelSkin;
        this.finalScale = finalScale;
        this.depth = depth;
        this.modelEntity = new Entity(entityType);

        this.modelEntity.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setItemStack(ItemStack.of(Material.PLAYER_HEAD)
                    .with(DataComponents.PROFILE, new ResolvableProfile(modelSkin)));
            // Start at 0 scale; Behavior controls the scaling
            meta.setScale(new Vec(0, 0, 0));
            meta.setHasNoGravity(true);
        });
        setupInteractions();
    }

    // --- Abstract Fish Logic: Behavior-Independent Actions ---

    /** Allows the behavior to control the fish's movement */
    public abstract void simpleMovementTick(Pos target, double speedFactor);

    /** Sets the visual scale of the fish model */
    public void setScale(double scale) {
        this.modelEntity.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setScale(new Vec(scale, scale, scale));
        });
    }

    /** Rotates the fish to face a target position */
    public void lookAt(Pos target) {
        Pos currentPos = modelEntity.getPosition();
        Vec directionToTarget = target.sub(currentPos).asVec().normalize();

        float yaw = (float) Math.toDegrees(Math.atan2(-directionToTarget.x(), directionToTarget.z()));
        float pitch = (float) Math.toDegrees(Math.asin(-directionToTarget.y()));

        this.modelEntity.setView(yaw, pitch);
    }

    public abstract @NotNull Entity getBody();

    // --- GameEntity Implementations ---

    @Override
    public List<Entity> getMinestomEntities() {
        return Collections.singletonList(modelEntity);
    }

    @Override
    public void setupInteractions() {}

    @Override
    public void spawn(EntityManager manager, Instance instance, Pos pos) {
        this.modelEntity.setInstance(instance, pos);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {}

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("GameEntity:" + this.getClass().getSimpleName());
    }

    // --- Getters ---
    public double getFinalScale() { return finalScale; }

    public double getDepth() {
        return depth;
    }

    public Pos getPosition() { return modelEntity.getPosition(); }
    public void teleport(Pos pos) { modelEntity.teleport(pos); }
    public boolean reachedTarget(Pos target, double threshold) {
        return modelEntity.getPosition().distance(target) < threshold;
    }
}