package nub.wi1helm.module.modules.fishing.core.behavior;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.module.modules.fishing.core.FishingRodType;
import nub.wi1helm.module.modules.fishing.core.loot.LootTable;
import nub.wi1helm.module.modules.fishing.entity.FishingHook;
import org.jetbrains.annotations.NotNull;

public class VanillaFishBehavior extends AbstractFishBehavior {

    // --- ONLY BEHAVIOR-SPECIFIC CONSTANTS REMAIN ---
    // (Constants specific to the particle path, like WIGGLE, can remain here)
    private static final double PARTICLE_WIGGLE_AMOUNT = 0.15;

    // --- State Machine ---
    private enum State {
        WAITING,
        PARTICLE_APPROACH,
        BITING
    }

    private final FishingRodType rod; // Kept for context

    private State currentState = State.WAITING;
    private int timer;
    private int particleTickCounter;
    private int currentParticleApproachTicks;
    private Pos particleStartPos;

    // CHANGED: Constructor now accepts the BehaviorEntry
    public VanillaFishBehavior(@NotNull FishingHook hook, @NotNull FishingRodType rod, @NotNull FishingRodType.BehaviorEntry entry) {
        super(hook, rod, entry); // Pass hook, rod, and entry to AbstractFishBehavior
        this.rod = rod;

        // Initialize the timer using the rod's general min/max wait time
        this.timer = randomBetween(rod.minWaitTime(), rod.maxWaitTime());
    }

    @Override
    public void start() {
        // No need to call resetWaitTimer, timer is set in constructor
        createTickTask(this::tick, TaskSchedule.tick(1));
    }

    // REMOVED: resetWaitTimer() is no longer needed since the timer is reset in tick() and set in the constructor.
    // The previous implementation of resetWaitTimer() logic is moved into the constructor and the WAITING case.

    @Override
    public void tick() {
        if (hook.isRemoved()) {
            super.cancel();
            return;
        }

        if (hook.notInWater()) {
            // If reel-in without bite, reset timer to start a new cycle
            this.timer = randomBetween(rod.minWaitTime(), rod.maxWaitTime());
            this.currentState = State.WAITING;
            this.particleTickCounter = 0;
            return;
        }

        switch (currentState) {
            case WAITING:
                if (--timer <= 0) {
                    // Start the particle approach phase
                    currentState = State.PARTICLE_APPROACH;

                    // CHANGED: Use injected min/max particle ticks from the BehaviorEntry
                    currentParticleApproachTicks = randomBetween(behaviorEntry.minParticleTicks(), behaviorEntry.maxParticleTicks());
                    timer = currentParticleApproachTicks;
                    particleTickCounter = 0;

                    particleStartPos = getRandomStartPointNearHook();
                }
                break;

            case PARTICLE_APPROACH:
                if (timer > 0) {
                    timer--;
                    spawnSerpentineParticle(hook.getController().getPosition());
                } else {
                    currentState = State.BITING;
                    // CHANGED: Use injected catch window ticks from the RodType
                    timer = rod.catchWindowTicks();
                    hook.showBiteAnimation();
                }
                break;

            case BITING:
                if (--timer <= 0) {
                    // Reset to WAITING and start a new wait timer
                    this.currentState = State.WAITING;
                    this.timer = randomBetween(rod.minWaitTime(), rod.maxWaitTime());
                    this.particleTickCounter = 0;
                }
                break;
        }
    }

    @Override
    protected @NotNull Pos getRandomStartPointNearHook() {
        Pos hookPos = hook.getController().getPosition();
        // CHANGED: Use injected spawn distance from the BehaviorEntry
        double maxDistance = behaviorEntry.spawnDistanceMax();

        // Start randomly within the defined max distance horizontally
        return hookPos.add(
                random.nextDouble(-maxDistance, maxDistance),
                // The +0.3 offset for Y-level is still acceptable for vanilla particle spawning
                0.3,
                random.nextDouble(-maxDistance, maxDistance)
        );
    }

    private void spawnSerpentineParticle(Pos hookPos) {
        if (hook.getInstance() == null) return;

        particleTickCounter++;
        double progress = (double) particleTickCounter / currentParticleApproachTicks;

        Pos particlePos = getParticlePos(hookPos, progress);

        ParticlePacket packet = new ParticlePacket(
                Particle.FISHING,
                (float) particlePos.x(), (float) particlePos.y(), (float) particlePos.z(),
                0f, 0f, 0f,
                0f,
                1
        );

        hook.getOwner().sendPacket(packet);
    }

    private @NotNull Pos getParticlePos(Pos hookPos, double progress) {
        double x = particleStartPos.x() + (hookPos.x() - particleStartPos.x()) * progress;
        double y = particleStartPos.y() + (hookPos.y() - particleStartPos.y()) * progress;
        double z = particleStartPos.z() + (hookPos.z() - particleStartPos.z()) * progress;

        // The wiggle amount can remain a constant for this behavior type
        double wiggleAmount = Math.sin(progress * Math.PI * 4.0) * PARTICLE_WIGGLE_AMOUNT;

        double dx = particleStartPos.x() - hookPos.x();
        double dz = particleStartPos.z() - hookPos.z();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 1.0E-5) {
            x += (dz / distance) * wiggleAmount;
            z += (-dx / distance) * wiggleAmount;
        }

        return new Pos(x, y, z);
    }

    @Override
    public LootTable tryCatch() {
        if (currentState == State.BITING) {
            // Player successfully catches, clean up and return the loot table
            super.cancel(); // Cancels the tick task and removes the hook entity
            return this.lootTable; // LootTable is inherited and stored in AbstractFishBehavior
        }
        return null; // No catch, return null
    }

    @Override
    public void cancel() {
        // If the session cancels early (e.g., player switches slot), the hook is removed via super.cancel()
        super.cancel();
    }
}