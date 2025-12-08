package nub.wi1helm.module.modules.mayor.interactions;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.entity.interaction.DialogNode;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.entity.interaction.InventoryNode;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mayor.entity.MayorNPC;
import nub.wi1helm.module.modules.mayor.gui.GiveLetterInventory;

import java.util.function.Consumer;
import java.util.function.ToIntFunction;

/**
 * Defines the interaction graph for Mayor Goodwin, using a custom system
 * where the player's current node is their persistent interaction state.
 * The graph transitions directly from the dialogue sequence to the Inventory,
 * and then terminates on the 'alreadyGivenNode' for repeatable, finished dialogue.
 */
public record MayorNPCInteraction(MayorNPC npc, GoalManager goalManager, ItemManager itemManager) {

    // NOTE: External state (like 'givenLetter') is no longer needed
    // because the player's position in the graph holds the state.

    /**
     * Defines the action sequence and configures the interaction graph using the new node system.
     */
    public void setup() {

        // Sounds
        Sound ambientHighPitch = Sound.sound()
                .type(SoundEvent.ENTITY_VILLAGER_AMBIENT)
                .pitch(1.5f)
                .source(Sound.Source.NEUTRAL)
                .build();

        Sound yesHighPitch = Sound.sound()
                .type(SoundEvent.ENTITY_VILLAGER_YES)
                .pitch(1.5f)
                .source(Sound.Source.NEUTRAL)
                .build();

        // Standard delay for reading dialog lines
        TaskSchedule dialogDelay = TaskSchedule.seconds(2);


        // -------------------------------------------------------------------
        // 1. DEFINE END STATE NODE
        // -------------------------------------------------------------------

        // This is the final, repeating state. Any interaction on this node
        // will repeat its dialogue forever until the player is moved elsewhere.
        DialogNode alreadyGivenNode = DialogNode.builder()
                .text(Component.text("I've already given you the letter! Please hurry and deliver it."))
                .type(SoundEvent.ENTITY_VILLAGER_NO)
                .pitch(1.0f)
                .delay(dialogDelay)
                .manual() // Stops the sequence here, making it the repeating state
                .build();

        // -------------------------------------------------------------------
        // 2. DEFINE INVENTORY & DIALOGUE SEQUENCE NODES
        // -------------------------------------------------------------------

        // Endpoint B: Open the Inventory GUI - Non-manual so it auto-advances to the final state.
        InventoryNode giveLetterInventoryNode = InventoryNode.builder()
                .inventory(new GiveLetterInventory(itemManager))
                // Removed .manual(), so it automatically advances to alreadyGivenNode
                .build();

        // Node 1/3: Start of the dialogue chain
        DialogNode line1Node = DialogNode.builder()
                .text(Component.text("Ah, hello there citizen!"))
                .sound(ambientHighPitch)
                .delay(dialogDelay)
                .autoAdvance(true)
                .build();

        // Node 2/3:
        DialogNode line2Node = DialogNode.builder()
                .text(Component.text("Election season is upon us once again."))
                .sound(ambientHighPitch)
                .delay(dialogDelay)
                .autoAdvance(true)
                .build();

        // Node 3/3: Final dialogue line, sets up the inventory presentation.
        DialogNode line3Node = DialogNode.builder()
                .text(Component.text("Deliver this important letter to the villagers!"))
                .sound(yesHighPitch)
                .delay(dialogDelay)
                .autoAdvance(true) // Auto-advance to the inventory node
                .build();


        // -------------------------------------------------------------------
        // 3. DEFINE ROOT LEVEL TRIGGER NODES
        // -------------------------------------------------------------------

        // Trigger A (P20): Goal Active / Start Full Dialog Sequence
        ToIntFunction<PlayerEntityInteractEvent> startSequencePriority = InteractionPriority.allOf(
                InteractionPriority.condition(event -> event.getHand() == PlayerHand.MAIN, 20),
                InteractionPriority.condition(event -> goalManager.hasActiveGoal(ServerGoals.MAYOR_VOTES), 20)
        );

        // This node starts the full dialog sequence (P20)
        InteractionNode startSequenceNode = InteractionNode.create(
                event -> { /* Start sequence */ },
                startSequencePriority,
                true, // Auto-advance to line1Node
                TaskSchedule.immediate()
        );

        // Trigger B (P10): Goal Inactive / Simple Dialog
        ToIntFunction<PlayerEntityInteractEvent> noGoalPriority = InteractionPriority.allOf(
                InteractionPriority.condition(event -> event.getHand() == PlayerHand.MAIN, 10),
                InteractionPriority.condition(event -> !goalManager.hasActiveGoal(ServerGoals.MAYOR_VOTES), 10)
        );

        Consumer<PlayerEntityInteractEvent> noGoalAction = event -> {
            Player player = event.getPlayer();
            player.sendActionBar(Component.text("I'm the mayor of this town. Have a good day."));
            player.playSound(Sound.sound()
                    .type(SoundEvent.ENTITY_VILLAGER_NO)
                    .source(Sound.Source.NEUTRAL)
                    .build());
        };

        InteractionNode noGoalNode = InteractionNode.create(
                noGoalAction,
                noGoalPriority,
                false, // Stops here
                null
        );

        // -------------------------------------------------------------------
        // 4. CONSOLIDATE ALL CONNECTIONS
        // -------------------------------------------------------------------

        // A. Root Triggers (P20 vs P10) - Chosen only on the *very first* interaction
        // If P20 is chosen, the player is moved into the dialogue sequence and never returns to root.
        InteractionNode root = npc.getInteractionGraph().getRoot();
        root.connect(startSequenceNode);     // P20: Full Dialogue path
        root.connect(noGoalNode);            // P10: Simple Dialog path

        // B. Linear Dialogue Flow (P20 path)
        startSequenceNode.connect(line1Node);
        line1Node.connect(line2Node);
        line2Node.connect(line3Node);

        // C. Final Action Flow: Dialogue -> Inventory -> Final Repeating State
        line3Node.connect(giveLetterInventoryNode);
        // The player's state is now transitioned to the final, repeating node.
        giveLetterInventoryNode.connect(alreadyGivenNode);
    }
}