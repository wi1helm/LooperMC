package nub.wi1helm.module.modules.hideandseek.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.module.modules.hideandseek.interaction.LilyNPCInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Random;

/**
 * Represents Lily, the hiding NPC for the Hide and Seek minigame.
 * Contains logic for movement, spawning, and maintaining position history.
 */
public class LilyNPC extends NPC {

    private Pos position;
    private static final double MIN_MOVE_DISTANCE = 20;

    private final LinkedList<Pos> lastPositions = new LinkedList<>();
    private static final int HISTORY_SIZE = 5;
    // Fallback position for when no suitable hiding spot is found
    private final Pos fallbackPosition = new Pos(0, -44, 5);

    public LilyNPC(Pos start, EntityManager entityManager, GoalManager goalManager) {
        super(

                new PlayerSkin(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY5NDg4OTM4NTE5OSwKICAicHJvZmlsZUlkIiA6ICIyYTExYzU4Njg1ZmU0ZGM3YjY0Nzc4OGYyMzZkN2VkMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeWthbFRlY2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxNGJiNTlhOTZjOGQyYjJhYTVhN2Q3NDljYmIxOTUxYjA3OThjODU1MzgyZjBhMTNlM2EwZDRkYjc2MmU2NyIKICAgIH0KICB9Cn0=",
                        "RluUD6lF0oUM34vljuOuuQ/bvV1ABPglQ/60FHRvaqz5lXQprOfXc5Lbaj2K+d1G7xV7KaaoDmDGm26HszGfrHlPqctCrdw/m9QTevw+NYPnQayC7CVRFQgoitpWcvd2KPfDGxMrQEXa6tqq8Dy/qHH8niog/Y4zIiH45w94CrQijWho0m+Zbhfe3QvCtB9kA+s8a6auOiErJWbRa291GWTlM7LuwPojWTEoWdcrB+vJSG2BNenFTJziS9knUNW2Krm9FWX2B9967SJwRuDolBjSzUuwC0s/Rb1b+32R0HlWVXUFlCDMic7uxJKw6Br92sAk6oBnun8ZXeHIKjE+/4w8kG7yJZBGVkGO8MWR1O94HpKdhV4i0TGM+SKdQqGVyAb8g1wkg1HLXC68R6QF8W4ojapvHa+GB5l9to2vQxig13gw/lcj5aidPFDiu1J5L8U55lrtOOa3NztOtu+i+Cg7paKTwztCa0imuQ6mqO1ZjzuNIAsHbV/Sk7FX+gySAqVUzXInIlaq5ciurI/hMLoa6VsIiC2KxqTx9lclovtkpqNaRqPi2u79Erl2+ahhcX5f2yw3Es/ksGiJeMi1ahZJ3AHCnwaoSzrjAn0ZIHDhUZUtfOHq/h7dXd7L6wBCdYPPdPher3mJCunLEBjyaQCSR9uZ2FM//H4aKsEn01Q="
                ),
                Component.text("Lily - Hider")
        );
        this.position = start;

        // Initialize interaction handler
        new LilyNPCInteraction(this, goalManager, entityManager).setup();
    }

    /**
     * Gets a new random position for Lily to hide, adhering to distance and history rules.
     *
     * @param instance The current instance.
     * @param center The center of the search radius.
     * @param radius The search radius.
     * @param oldPos The position Lily is moving from.
     * @return A new safe position, or the fallback position if none found.
     */
    public Pos getRandomPosInRadius(Instance instance, Pos center, double radius, Pos oldPos) {
        final int MAX_Y = -30;
        final int MIN_Y = -60;

        Random random = new Random();
        double outsideChance = 0.2; // Chance to hide in the open

        for (int attempt = 0; attempt < 100; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;

            int x = (int) Math.floor(center.x() + Math.cos(angle) * dist);
            int z = (int) Math.floor(center.z() + Math.sin(angle) * dist);

            for (int y = MAX_Y; y >= MIN_Y; y--) {
                Pos groundPos = new Pos(x, y, z);
                var block = instance.getBlock(groundPos);

                if (!block.isAir() && block.registry().collisionShape().isFaceFull(BlockFace.TOP)) {
                    var head = instance.getBlock(new Pos(x, y + 1, z));
                    var upper = instance.getBlock(new Pos(x, y + 2, z));

                    if (head.isAir() && upper.isAir()) {
                        Pos newPos = new Pos(x + 0.5, y + 1, z + 0.5);

                        // Must move far from old position
                        if (newPos.distance(oldPos) < MIN_MOVE_DISTANCE) continue;

                        // Reject if too close to any recent positions
                        boolean tooClose = false;
                        for (Pos recent : lastPositions) {
                            if (newPos.distance(recent) < MIN_MOVE_DISTANCE / 2) {
                                tooClose = true;
                                break;
                            }
                        }
                        if (tooClose) continue;

                        // Discard if sky above (unless outside chance hits)
                        if (hasSkyAbove(instance, newPos) && random.nextDouble() > outsideChance) continue;

                        // Good position found, add to history
                        lastPositions.addFirst(newPos);
                        if (lastPositions.size() > HISTORY_SIZE) lastPositions.removeLast();

                        return newPos;
                    }
                }
            }
        }

        return fallbackPosition;
    }

    private boolean hasSkyAbove(Instance instance, Pos pos) {
        for (int yy = (int) pos.y() + 2; yy <= 300; yy++) {
            var block = instance.getBlock(new Pos(pos.x(), yy, pos.z()));
            if (!block.isAir()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setupInteractions() {

    }

    /**
     * Spawns the NPC at its current internal position.
     */
    @Override
    public void spawn(EntityManager manager, Instance instance) {
        super.spawn(manager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 3);
    }

    // Getters and Setters needed for the interaction class
    public Pos getPosition() {
        return position;
    }

    public void setPosition(Pos position) {
        this.position = position;
    }

    public Pos getFallbackPosition() {
        return fallbackPosition;
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:hideandseek:npc:lily");
    }
}