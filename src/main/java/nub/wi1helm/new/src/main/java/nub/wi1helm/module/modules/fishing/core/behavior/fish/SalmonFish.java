package nub.wi1helm.module.modules.fishing.core.behavior.fish;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.NotNull;


public class SalmonFish extends Fish {

    private static final PlayerSkin SALMON_SKIN = PlayerSkin.fromUsername("Salmon");
    private static final double SALMON_FINAL_SCALE = 0.4;
    private static final double SALMON_DEPTH = 0.5;
    public SalmonFish() {
        super(EntityType.ITEM_DISPLAY, SALMON_SKIN, SALMON_FINAL_SCALE, SALMON_DEPTH);
    }

    @Override
    public void simpleMovementTick(Pos target, double speedFactor) {
        // Default implementation for basic fish movement (can be expanded later)

        // Calculate the vector towards the target
        Vec currentPos = modelEntity.getPosition().asVec();
        Vec targetVec = target.asVec();
        Vec direction = targetVec.sub(currentPos).normalize();

        // Use speedFactor to control the speed
        double speed = 0.01 * speedFactor;

        // Apply movement
        modelEntity.setVelocity(direction.mul(speed));

        // Apply drag/slowdown (for Minestom physics control)
        // modelEntity.setVelocity(modelEntity.getVelocity().mul(0.8));
    }

    @Override
    public @NotNull Entity getBody() {
        return modelEntity;
    }
}