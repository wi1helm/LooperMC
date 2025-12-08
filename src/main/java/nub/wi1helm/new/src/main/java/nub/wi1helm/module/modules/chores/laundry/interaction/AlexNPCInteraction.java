package nub.wi1helm.module.modules.chores.laundry.interaction;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.sound.SoundEvent;
import nub.wi1helm.entity.interaction.DialogNode; // New Import
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.modules.chores.laundry.entity.AlexNPC;

import java.util.function.ToIntFunction; // Added for clarity, though not strictly required

/**
 * Handles all interactions for the {@link AlexNPC}.
 * <p>
 * Uses a graph of {@link InteractionNode} objects to determine
 * the appropriate response when a player interacts with the NPC.
 */
public record AlexNPCInteraction(AlexNPC npc, GoalManager goalManager) {

    /**
     * Creates a new interaction handler for the Alex NPC.
     *
     * @param npc         The NPC this interaction is attached to.
     * @param goalManager The goal manager to track chore goals.
     */
    public AlexNPCInteraction {
    }

    /**
     * Defines the interaction flow based on the state of ServerGoals.TOWN_CHORES
     * and sets up the interaction graph for the NPC using DialogNodes.
     */
    public void setup() {

        // --- Base Condition: Must be Main Hand ---
        // This condition is used by all nodes to ensure the click is relevant.
        ToIntFunction<PlayerEntityInteractEvent> mainHandCondition = event -> event.getHand() == PlayerHand.MAIN ? 1 : -1;


        // --- 1. Completed Chore Dialogue (Priority: 20) ---
        // Runs if the goal is active AND completed.
        DialogNode completedNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<white>Thank you for helping me</white>"))
                .type(SoundEvent.ENTITY_VILLAGER_AMBIENT)
                .manual() // No auto-advance, waits for next click to reset to root
                .priority(InteractionPriority.condition(
                        event -> mainHandCondition.applyAsInt(event) > 0 &&
                                goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES) &&
                                goalManager.isGoalCompleted(ServerGoals.TOWN_CHORES),
                        20 // Highest Priority
                ))
                .build();


        // --- 2. Uncompleted Chore Dialogue (Priority: 10) ---
        // Runs if the goal is active but not completed.
        DialogNode uncompletedNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<white>Could you help me dry my clothes</white>"))
                .type(SoundEvent.ENTITY_VILLAGER_YES)
                .manual()
                .priority(InteractionPriority.condition(
                        event -> mainHandCondition.applyAsInt(event) > 0 &&
                                goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES),
                        10 // Mid Priority
                ))
                .build();


        // --- 3. No Chore Dialogue (Priority: 0) ---
        // Runs as the default if neither of the above high-priority nodes matches.
        DialogNode noChoreNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<white>How are you today?</white>"))
                .type(SoundEvent.ENTITY_PILLAGER_AMBIENT)
                .manual()
                .priority(InteractionPriority.condition(
                        event -> mainHandCondition.applyAsInt(event) > 0,
                        0 // Default Priority
                ))
                .build();


        // --- Link all nodes to the root ---
        InteractionNode root = npc.getInteractionGraph().getRoot();

        // Use link/connect for registration. Priority dictates execution.
        root.link(completedNode);   // P20
        root.link(uncompletedNode); // P10
        root.link(noChoreNode);     // P0
    }
}