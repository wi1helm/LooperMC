package nub.wi1helm.module.modules.fishing.entity;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.FishingHookMeta;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Scheduler;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FishingHook extends GameEntity {

    // --- Constants ---
    private static final double INITIAL_SPEED_MULTIPLIER = 10.0;
    private static final double BOB_SPEED = 0.1;
    private static final double BOB_AMOUNT = 0.09;
    private static final double SURFACE_OFFSET = -0.02;
    private static final double MAX_PULL_DOWN = 0.3;
    private static final double PULL_DOWN_RECOVERY_SPEED = 0.016666666666666666;
    private static final double CONTROLLER_DRAG = 0.2;
    // Offset for checking the block *below* the hook (as Minestom entities are point-based)
    private static final double WATER_CHECK_OFFSET = -0.2;
    private static final String HOOK_TAG_KEY = "module:fishing:hook";

    // --- Entity State ---
    private final Player owner;
    private final Entity hook;
    private final Entity controller;

    private boolean isRemoved = false;
    private double bobTick = 0;
    private double pullDownOffset = 0;

    private Double stableWaterY = null;

    public FishingHook(Player owner) {
        this.owner = owner;

        this.hook = new Entity(EntityType.FISHING_BOBBER);
        this.hook.editEntityMeta(FishingHookMeta.class, meta -> {
            meta.setOwnerEntity(owner);
        });

        this.controller = new Entity(EntityType.TEXT_DISPLAY);
        this.controller.setNoGravity(true);

        this.controller.eventNode().addListener(EntityTickEvent.class,
                event -> tick(event.getEntity()));
    }

    /** Spawn the hook in front of the player (eye level) */
    @Override
    public void spawn(EntityManager manager, Instance instance) {
        Pos spawnPos = owner.getPosition().add(0, owner.getEyeHeight(), 0);
        spawn(manager, instance, spawnPos);
    }

    /** Spawn hook at a specific position */
    @Override
    public void spawn(EntityManager manager, Instance instance, Pos pos) {
        this.controller.setInstance(instance, pos);
        this.hook.setInstance(instance, pos);
        this.controller.addPassenger(hook);
        this.controller.setVelocity(owner.getPosition().direction().mul(INITIAL_SPEED_MULTIPLIER));
    }

    /** Tick for bobbing motion and pull-down recovery */
    private void tick(@NotNull Entity controller) {
        Instance instance = controller.getInstance();
        if (instance == null) return;

        Pos pos = controller.getPosition();

        // --- Core Water Check ---
        if (notInWater()) {
            stableWaterY = null;
            controller.setNoGravity(false); // Re-enable gravity to fall/stop
            return;
        }

        // Hook is in water
        controller.setNoGravity(true);
        Block blockBelow = instance.getBlock(pos.add(0, WATER_CHECK_OFFSET, 0));

        if (stableWaterY == null) {
            // First time hitting water, establish stable surface Y
            stableWaterY = getWaterSurface(blockBelow, (int) Math.floor(pos.y() + WATER_CHECK_OFFSET)) + SURFACE_OFFSET;
            bobTick = 0;
            pullDownOffset = 0;
        }

        // --- Bobbing Motion ---
        bobTick += BOB_SPEED;
        double yBob = Math.sin(bobTick) * BOB_AMOUNT;

        // --- Pull-down Recovery ---
        pullDownOffset = Math.max(pullDownOffset - PULL_DOWN_RECOVERY_SPEED, 0);

        // --- Update Position ---
        Pos targetPos = new Pos(pos.x(), stableWaterY + yBob - pullDownOffset, pos.z());

        controller.teleport(targetPos);
        controller.setVelocity(controller.getVelocity().mul(CONTROLLER_DRAG));
    }

    public boolean notInWater() {
        Instance instance = controller.getInstance();
        Pos pos = controller.getPosition();
        // Check the block at the entity's precise location
        Block currentBlock = instance.getBlock(pos);

        // Check the block slightly below the entity's location
        Block blockBelow = instance.getBlock(pos.add(0, WATER_CHECK_OFFSET, 0));

        // The hook is considered "in water" if either its current block OR
        // the block directly below it is water.
        return !isWater(currentBlock) && !isWater(blockBelow);
    }

    // ... (Other methods remain the same) ...

    /** Show bite animation: small dip + splash sound */
    public void showBiteAnimation() {
        Instance instance = controller.getInstance();
        if (instance == null) return;

        // Splash sound at hook position
        instance.playSound(Sound.sound()
                .type(SoundEvent.ENTITY_FISHING_BOBBER_SPLASH)
                .source(Sound.Source.PLAYER)
                .build()
        );
        instance.sendGroupedPacket(new ParticlePacket(
                Particle.SPLASH,
                controller.getPosition(),
                new Pos(0.1,0.001,0.1),
                0, 20
        ));

        // Apply a temporary dip (pull-down)
        pullDownOffset = Math.min(pullDownOffset + 0.15, MAX_PULL_DOWN);

        // Quick bob shift for an immediate "tug" effect
        bobTick += Math.PI / 6;
    }

    /** Remove hook safely */
    @Override
    public void remove() {
        if (this.isRemoved) return;
        this.isRemoved = true;
        hook.remove();
        controller.remove();
    }

    /** Get water surface Y based on block level */
    private double getWaterSurface(Block block, int blockY) {
        if (!block.isLiquid()) return blockY + 1.0;

        String levelStr = block.getProperty("level");
        int level = (levelStr == null) ? 0 : Integer.parseInt(levelStr);

        return blockY + ((level == 0) ? 1.0 : (8 - level) / 8.0);
    }

    /** Checks if a block is water (Minestom handles both 'WATER' and names containing 'water') */
    private static boolean isWater(@NotNull Block block) {
        return block.isLiquid() && block.name().contains("water");
    }

    // --- Getters and Minestom Overrides ---

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(hook, controller);
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String(HOOK_TAG_KEY);
    }

    @Override
    public void setupInteractions() { }

    public boolean isRemoved() {
        return isRemoved;
    }

    public Entity getController() {
        return controller;
    }

    public Scheduler getScheduler() {
        return hook.scheduler();
    }

    public Player getOwner() {
        return owner;
    }

    public Instance getInstance() {
        return controller.getInstance();
    }
}