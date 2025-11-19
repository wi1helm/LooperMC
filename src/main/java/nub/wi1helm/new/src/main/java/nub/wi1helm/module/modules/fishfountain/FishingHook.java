package nub.wi1helm.module.modules.fishfountain;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.FishingHookMeta;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FishingHook extends GameEntity {

    private final Entity hook;
    private final Entity controller;
    private final Player owner;

    private double bobTick = 0;
    private static final double BOB_SPEED = 0.18;
    private static final double BOB_AMOUNT = 0.09;
    private static final double SURFACE_OFFSET = -0.02;
    private static final int BOB_WATER_LEVEL_THRESHOLD = 2;
    private int biteTimer = 0; // counts down when fish is biting
    private static final int BITE_DURATION = 40; // 2 seconds if 20 ticks/sec


    private Double stableWaterY = null;
    private boolean fishBiting = false; // becomes true when fish reaches bobber

    private int fishSpawnTimer = 0;
    private static final int MIN_FISH_TIME = 40;
    private static final int MAX_FISH_TIME = 100;
    private final Random random = new Random();

    private Fish activeFish = null;

    public FishingHook(Player owner) {
        this.owner = owner;
        this.hook = new Entity(EntityType.FISHING_BOBBER);
        this.controller = new Entity(EntityType.TEXT_DISPLAY);

        this.controller.editEntityMeta(TextDisplayMeta.class , meta -> {

        });
        this.hook.editEntityMeta(FishingHookMeta.class, meta -> meta.setOwnerEntity(this.owner));

        this.controller.eventNode().addListener(EntityTickEvent.class, event -> tick(event.getEntity()));
    }

    private void tick(Entity controller) {
        Instance instance = controller.getInstance();
        if (instance == null) return;

        Pos pos = controller.getPosition();
        Vec vel = controller.getVelocity();

        Block blockBelow = instance.getBlock(pos.add(0, -0.2, 0));
        int blockY = (int) Math.floor(pos.y() - 0.2);
        boolean inWater = isWater(blockBelow);

        if (!inWater) {
            stableWaterY = null;
            controller.setNoGravity(false);
            return;
        }

        String levelStr = blockBelow.getProperty("level");
        int level = levelStr == null ? 0 : Integer.parseInt(levelStr);

        if (level > BOB_WATER_LEVEL_THRESHOLD) {
            stableWaterY = null;
            controller.setNoGravity(false);
            return;
        }

        controller.setNoGravity(true);

        double waterSurface = getWaterSurface(blockBelow, blockY);

        if (stableWaterY == null) {
            stableWaterY = waterSurface + SURFACE_OFFSET;
            bobTick = 0;
            fishSpawnTimer = random.nextInt(MAX_FISH_TIME - MIN_FISH_TIME) + MIN_FISH_TIME;
        }

        // Bobbing
        bobTick += BOB_SPEED;
        double yBob = Math.sin(bobTick) * BOB_AMOUNT;
        controller.teleport(new Pos(pos.x(), stableWaterY + yBob, pos.z()));
        controller.setVelocity(new Vec(vel.x() * 0.6, 0, vel.z() * 0.6));

        // Spawn single fish if none active
        if (activeFish == null) {
            fishSpawnTimer--;
            if (fishSpawnTimer <= 0) {
                spawnFish(instance, pos);
            }
        }
        if (fishBiting) {
            biteTimer--;
            if (biteTimer <= 0) {
                // Bite window expired, reset
                fishBiting = false;
                stableWaterY += 0.05; // optional, bring bobber back up
            }
        }

        // Tick active fish
        if (activeFish != null) {
            boolean reached = !activeFish.tickTowards(controller);
            if (reached) {
                // Fish reached bobber -> set flag
                fishBiting = true;
                biteTimer = BITE_DURATION; // start bite window

                // Dip bobber slightly
                stableWaterY -= 0.05;

                // Remove fish
                activeFish.remove();
                activeFish = null;
                // Schedule next fish spawn
                fishSpawnTimer = random.nextInt(MAX_FISH_TIME - MIN_FISH_TIME) + MIN_FISH_TIME;
            }
        }
    }

    private void spawnFish(Instance instance, Pos bobberPos) {
        double offsetX = (random.nextDouble() - 0.5) * 24;
        double offsetZ = (random.nextDouble() - 0.5) * 24;
        Pos spawnPos = bobberPos.add(offsetX, 0, offsetZ);

        for (int dy = -1; dy <= 2; dy++) {
            Block b = instance.getBlock(spawnPos.add(0, dy, 0));
            if (isWater(b)) {
                spawnPos = spawnPos.add(0, dy + 0.1, 0);
                break;
            }
        }

        activeFish = new Fish(spawnPos, instance);
    }

    private boolean isWater(Block block) {
        return block.name().contains("water");
    }

    private double getWaterSurface(Block block, int blockY) {
        if (!isWater(block)) return blockY + 1.0;
        String levelStr = block.getProperty("level");
        int level = levelStr == null ? 0 : Integer.parseInt(levelStr);
        return blockY + ((level == 0) ? 1.0 : Math.max((8.0 - level) / 8.0, 0));
    }

    private class Fish {
        private final Entity entity;
        private final Instance instance;
        private Pos target;
        private double wiggleTick = 0;

        public Fish(Pos spawnPos, Instance instance) {
            this.instance = instance;
            this.entity = new Entity(EntityType.ITEM_DISPLAY);
            this.entity.editEntityMeta(ItemDisplayMeta.class, meta -> {
                meta.setItemStack(ItemStack.of(Material.PLAYER_HEAD)
                        .with(DataComponents.PROFILE, new ResolvableProfile(new PlayerSkin(
                                "ewogICJ0aW1lc3RhbXAiIDogMTc0MDQ0NjgxOTEyNiwKICAicHJvZmlsZUlkIiA6ICIxMDJiMWQxOGFhZTg0ZjQzYWMyYTY1MjQyNzdjMDU0NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeXRob2xvdGwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhiMWJjNzAxMzZkNjIwYTgyMmQwOGRmYzJiMDRhYmU5MjdlMWM0MzJiMjU2MTM5NjI0YjAwYzdiZjRkM2MxZSIKICAgIH0KICB9Cn0=",""))));
                meta.setScale(new Vec(0.5, 0.5, 0.5));
            });
            this.entity.setInstance(instance, spawnPos);
        }

        public boolean tickTowards(Entity bobber) {
            Pos pos = entity.getPosition();
            target = bobber.getPosition();

            Vec toBobber = target.sub(pos).asVec();
            double distance = toBobber.length();
            if (distance < 0.3) return false;

            wiggleTick += 0.2 + distance * 0.05;
            double wiggleStrength = Math.min(distance, 1.0);

            Vec wiggle = new Vec(
                    Math.sin(wiggleTick * 3) * 0.03 * wiggleStrength,
                    Math.cos(wiggleTick * 1.5) * 0.02 * wiggleStrength,
                    Math.sin(wiggleTick * 2.5) * 0.03 * wiggleStrength
            );

            Vec movement = toBobber.normalize().mul(Math.min(distance * 0.05, 0.15)).add(wiggle);

            // Look at bobber
            double dx = target.x() - pos.x();
            double dy = target.y() - pos.y();
            double dz = target.z() - pos.z();

            double yaw = Math.toDegrees(Math.atan2(-dx, dz)) - 180;
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            double pitch = Math.toDegrees(-Math.atan2(dy, horizontalDistance));

            entity.setVelocity(movement);
            entity.teleport(pos.add(movement).withView((float) yaw, (float) pitch));

            return true;
        }

        public void remove() {
            entity.remove();
        }
    }

    @Override
    public java.util.List<Entity> getMinestomEntities() {
        java.util.List<Entity> entities = new java.util.ArrayList<>();
        entities.add(hook);
        entities.add(controller);
        if (activeFish != null) entities.add(activeFish.entity);
        return entities;
    }

    @Override
    public void onPlayerInteract(PlayerEntityInteractEvent event) {}

    public void spawn(Instance instance, Pos pos) {
        getMinestomEntities().forEach(entity -> entity.setInstance(instance, pos));
        this.controller.addPassenger(this.hook);
        this.controller.setVelocity(pos.direction().mul(10));
    }

    public boolean removeAndCheckFish() {
        boolean biting = fishBiting;

        if (activeFish != null) activeFish.remove();
        activeFish = null;

        super.remove();
        fishBiting = false;

        return biting;
    }

    @Override
    public void remove() {
        super.remove();
        activeFish.remove();
    }

    @Override public void spawn(EntityManager manager, Instance instance, Pos pos) {}
    @Override public void spawn(EntityManager manager, Instance instance) {}

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:fountain:entity:hook");
    }
}
