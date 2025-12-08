package nub.wi1helm.module.modules.hideandseek.interaction;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityPose;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.interaction.DialogNode;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.modules.hideandseek.entity.LilyNPC;

import java.util.function.Consumer;

/**
 * Handles all interactions for the {@link LilyNPC}.
 * Implements the state machine for the Hide and Seek game.
 */
public class LilyNPCInteraction {

    private final LilyNPC npc;
    private final GoalManager goalManager;
    private final EntityManager entityManager;

    public LilyNPCInteraction(LilyNPC npc, GoalManager goalManager, EntityManager entityManager) {
        this.npc = npc;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
    }

    /**
     * Sets up the interaction graph for this NPC.
     */
    public void setup() {
        InteractionNode root = npc.getInteractionGraph().getRoot();

        // --- 1. Not playing hide and seek today (Priority 100) ---
        // Simple dialogue that runs if the goal is inactive.
        DialogNode notPlayingNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<white>Not playing today!</white>"))
                .type(SoundEvent.ENTITY_VILLAGER_AMBIENT).pitch(1.6F)
                .manual()
                .priority(InteractionPriority.condition(
                        event -> !goalManager.hasActiveGoal(ServerGoals.HIDE_AND_SEEK),
                        100 // Highest priority
                ))
                .build();

        // --- 2B. Found Lily Dialogue (Priority 0) ---
        // Dialogue node to give feedback after the teleport action.
        DialogNode foundDialogNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<white>Lily found a new hiding spot!</white>"))
                .type(SoundEvent.ENTITY_ENDERMAN_TELEPORT).pitch(1.3F)
                .manual() // Ends the sequence
                .priority(InteractionPriority.always(0))
                .build();


        // --- 2A. Found Lily Action (Priority 0) ---
        // Action node: Teleports Lily and updates the goal. Auto-advances to dialogue.
        Consumer<PlayerEntityInteractEvent> teleportAction = event -> {
            Instance instance = event.getPlayer().getInstance();
            Pos oldPos = npc.getPosition();

            // 1. Lily does sneaking animation
            npc.getMannequin().setPose(EntityPose.SNEAKING);

            // 2. Find new position
            Pos newSpot = npc.getRandomPosInRadius(instance, npc.getFallbackPosition(), 50, oldPos);

            // 3. Remove, update internal position, and respawn at new location
            npc.getMannequin().remove();
            npc.setPosition(newSpot);
            npc.spawn(entityManager, instance); // Spawn at the new position

            // 4. Update goal progress
            goalManager.incrementGoal(ServerGoals.HIDE_AND_SEEK, 1);

            // The dialogue and sound feedback happen in 2B
        };

        InteractionNode teleportActionNode = InteractionNode.create(
                teleportAction,
                InteractionPriority.always(0), // Default action, lowest priority
                true, // *** AUTO-ADVANCE IS TRUE ***
                TaskSchedule.immediate() // *** NO DELAY ***
        );


        // --- Link the Nodes ---

        // 1. Link the action node to the dialogue node (must be one-way)
        teleportActionNode.connect(foundDialogNode);

        // 2. Link all entry points to the root (Highest priority wins)
        root.link(notPlayingNode);       // P100
        root.connect(teleportActionNode); // P0 (Start of the hide-and-seek sequence)
        foundDialogNode.connect(root);
    }
}