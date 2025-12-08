package nub.wi1helm.module.modules.fishing.core.behavior;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.module.modules.fishing.core.FishingRodType;
import nub.wi1helm.module.modules.fishing.core.behavior.fish.BlobFish;
import nub.wi1helm.module.modules.fishing.core.behavior.fish.Fish;
import nub.wi1helm.module.modules.fishing.core.behavior.fish.SalmonFish;
import nub.wi1helm.module.modules.fishing.core.loot.LootTable;
import nub.wi1helm.module.modules.fishing.entity.FishingHook;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class VanillaFishWithEntityBehavior extends AbstractFishBehavior {

    private static final double PARTICLE_WIGGLE_AMOUNT = 0.25;

    // --- State Machine ---
    private enum State {
        WAITING,
        PARTICLE_APPROACH,
        BITING
    }

    private final FishingRodType rod;
    private final List<Class<? extends Fish>> availableFishTypes = new ArrayList<>();

    private Fish fish = null;
    private State currentState = State.WAITING;

    private int timer;
    private int currentParticleTick; // Current tick count within the approach phase
    private int totalParticleTicks;  // Total ticks for the approach phase
    private Pos fishSpawnPos;        // Initial position of the fish when it spawns
    private Pos hookTargetPos;       // The underwater target position beneath the bobber
    private double currentFishScale = 0.0;

    public VanillaFishWithEntityBehavior(@NotNull FishingHook hook, @NotNull FishingRodType rod, @NotNull FishingRodType.BehaviorEntry entry) {
        super(hook, rod, entry); // Pass all required data up to AbstractFishBehavior

        this.rod = rod;

        initializeFishList();
    }

    private void initializeFishList() {
        availableFishTypes.add(SalmonFish.class);
        availableFishTypes.add(BlobFish.class);
    }

    @Override
    public void start() {
        scheduleNextFish();
        createTickTask(this::tick, TaskSchedule.tick(1));
    }

    private void scheduleNextFish() {
        removeFish();
        currentState = State.WAITING;
        // Use rod's overall min/max wait time for the initial phase
        this.timer = randomBetween(rod.minWaitTime(), rod.maxWaitTime());
    }

    private void spawnFish() {
        Pos hookPos = hook.getController().getPosition();

        try {
            Class<? extends Fish> chosenFishClass = availableFishTypes.get(random.nextInt(availableFishTypes.size()));
            fish = chosenFishClass.getDeclaredConstructor().newInstance();

            // 1. Calculate Random Spawn Distance
            // Use entry's specific spawn distance
            double distance = random.nextDouble(behaviorEntry.spawnDistanceMin(), behaviorEntry.spawnDistanceMax());

            // 2. Calculate Spawn Position
            double angle = random.nextDouble() * 2 * Math.PI;
            double x = hookPos.x() + Math.cos(angle) * distance;
            double z = hookPos.z() + Math.sin(angle) * distance;

            // Use the fish's specific depth for the spawn Y coordinate
            fishSpawnPos = new Pos(x, hookPos.y() - fish.getDepth(), z);

            // Reset the scale tracker when a new fish is spawned
            this.currentFishScale = 0.0;
            fish.spawn(null, hook.getController().getInstance(), fishSpawnPos);

            // Define the final underwater position of the fish
            hookTargetPos = new Pos(hookPos.x(), hookPos.y() - fish.getDepth(), hookPos.z());

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println("Failed to spawn fish: " + e.getMessage());
            fish = null;
        }
    }

    private void removeFish() {
        if (fish != null) {
            fish.remove();
        }
        fish = null;
    }

    @Override
    public void tick() {
        if (hook.isRemoved()) {
            super.cancel();
            return;
        }

        if (hook.notInWater()) {
            scheduleNextFish();
            return;
        }

        Pos hookPos = hook.getController().getPosition();

        switch (currentState) {
            case WAITING:
                if (--timer <= 0) {
                    currentState = State.PARTICLE_APPROACH;
                    // Use entry's specific particle timing
                    totalParticleTicks = randomBetween(behaviorEntry.minParticleTicks(), behaviorEntry.maxParticleTicks());
                    timer = totalParticleTicks;
                    currentParticleTick = 0;
                    spawnFish();
                }
                break;

            case PARTICLE_APPROACH:
                if (fish == null) return;

                currentParticleTick++;
                timer--;

                // --- Fish Control Logic ---
                Pos particlePos = updateFishApproach(hookPos);
                spawnParticle(particlePos);

                if (timer <= 0) {
                    // Fish has arrived, trigger the bite
                    currentState = State.BITING;
                    timer = catchWindowTicks;
                    hook.showBiteAnimation();

                    // Teleport fish to final biting position
                    fish.teleport(hookTargetPos);
                    fish.lookAt(hookPos);
                }
                break;

            case BITING:
                if (--timer <= 0) {
                    // Reset using rod's wait time
                    scheduleNextFish();
                }
                break;
        }
    }

    /**
     * Calculates the fish's position, scales it, and updates its rotation.
     * @param hookPos The current bobber position.
     * @return The calculated position where the particle should be spawned (above water).
     */
    private Pos updateFishApproach(Pos hookPos) {
        double progress = (double) currentParticleTick / totalParticleTicks;

        // --- 1. Calculate Base Serpentine Position (X and Z) ---

        // Base interpolation for X and Z
        double x = fishSpawnPos.x() + (hookTargetPos.x() - fishSpawnPos.x()) * progress;
        double z = fishSpawnPos.z() + (hookTargetPos.z() - fishSpawnPos.z()) * progress;

        // Horizontal distance from the current interpolated position (x, z) to the bobber (hookPos.x, hookPos.z)
        double dx_bobber = hookPos.x() - x;
        double dz_bobber = hookPos.z() - z;
        double horizontalDistanceToBobber = Math.sqrt(dx_bobber * dx_bobber + dz_bobber * dz_bobber);

        // 2. --- Calculate Dynamic Y Position (Conditional Lunge) ---
        double y = getY(hookPos, horizontalDistanceToBobber, progress);

        // --- 3. Apply Serpentine Wiggle (to X and Z) ---
        double wiggleAmount = Math.sin(progress * Math.PI * 4.0) * PARTICLE_WIGGLE_AMOUNT;

        double dx_path = fishSpawnPos.x() - hookTargetPos.x();
        double dz_path = fishSpawnPos.z() - hookTargetPos.z();
        double distance_path = Math.sqrt(dx_path * dx_path + dz_path * dz_path);

        if (distance_path > 1.0E-5) {
            x += (dz_path / distance_path) * wiggleAmount;
            z += (-dx_path / distance_path) * wiggleAmount;
        }

        Pos newFishPos = new Pos(x, y, z);
        fish.teleport(newFishPos);

        updateFishScale();

        fish.lookAt(hookPos);

        // --- 5. Particle Position ---
        // Particle Y remains slightly above the hook's surface Y, but uses the fish's X/Z
        return new Pos(x, hookPos.y() + 0.1, z);
    }

    private double getY(Pos hookPos, double horizontalDistanceToBobber, double progress) {
        double y_start = hookTargetPos.y(); // The fish's natural depth
        double y_end = hookPos.y();         // The bobber's surface Y level

        double y;

        // LUNGE CONDITION: Only start ascending when within 1.5 blocks horizontally
        final double LUNGE_THRESHOLD = 1.5;

        if (horizontalDistanceToBobber <= LUNGE_THRESHOLD) {
            // Calculate the interpolation factor based on how close we are to the bobber
            // lungeProgress goes from 0 (at 1.5 blocks) to 1 (at 0 blocks)
            double lungeProgress = 1.0 - (horizontalDistanceToBobber / LUNGE_THRESHOLD);
            lungeProgress = Math.pow(lungeProgress, 2); // Squared for an accelerating "snap" effect

            // Interpolate Y from depth (y_start) up to the surface (y_end)
            y = y_start + (y_end - y_start) * lungeProgress;

        } else {
            // Stay at the natural depth (linear interpolation from spawn Y to target Y)
            // Use simple linear interpolation for Y until the lunge starts
            y = fishSpawnPos.y() + (hookTargetPos.y() - fishSpawnPos.y()) * progress;
        }
        return y;
    }

    private void updateFishScale() {
        if (fish == null) return;

        final double SCALE_RATE = 0.05; // The desired "pop in" rate per tick

        // Increment the scale, capping it at the fish's final scale
        this.currentFishScale = Math.min(this.currentFishScale + SCALE_RATE, fish.getFinalScale());

        // Apply the new scale to the entity
        fish.setScale(this.currentFishScale);
    }
    private void spawnParticle(Pos particlePos) {
        Player owner = getOwner();
        if (owner == null) return;

        ParticlePacket packet = new ParticlePacket(
                Particle.FISHING,
                (float) particlePos.x(), (float) particlePos.y(), (float) particlePos.z(),
                0f, 0f, 0f,
                0f,
                1
        );

        owner.sendPacket(packet);
    }

    private Player getOwner() {
        if (hook.getOwner() != null) {
            return hook.getOwner();
        }
        return null;
    }

    @Override
    public LootTable tryCatch() {
        if (currentState == State.BITING) {
            // 1. Clean up the behavior
            removeFish();

            // 2. Schedule the next fish using rod's default timing
            this.timer = randomBetween(rod.minWaitTime(), rod.maxWaitTime());

            // 3. Stop the tick task and remove the hook entity
            super.cancel();

            // 4. Return the specific loot table associated with this behavior
            return this.lootTable;
        }
        return null; // No catch
    }

    @Override
    public void cancel() {
        removeFish();
        super.cancel();
    }

    @Override
    protected @NotNull Pos getRandomStartPointNearHook() {
        // Unused now that spawn point is calculated in spawnFish()
        return Pos.ZERO;
    }
}