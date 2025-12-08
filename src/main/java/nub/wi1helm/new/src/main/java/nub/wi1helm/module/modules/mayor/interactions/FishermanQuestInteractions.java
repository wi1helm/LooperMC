package nub.wi1helm.module.modules.mayor.interactions;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.entity.interaction.DialogNode;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.module.modules.fishing.entity.FishermanNPC;
import nub.wi1helm.module.modules.mayor.items.MayorLetterItem;
import org.jetbrains.annotations.NotNull;
import java.util.function.ToIntFunction;

/**
 * Defines the interaction graph nodes for the Fisherman's quest chain.
 * This record connects the quest logic branches directly to the Fisherman NPC's
 * Interaction Graph, eliminating the need to pass the root node manually.
 */
public record FishermanQuestInteractions(FishermanNPC fishermanNPC) {

    // --- GLOBAL QUEST STATE (Managed by Mayor Module) ---
    // Note: The quest completion state remains static for module-level tracking.
    private static volatile boolean QUEST_COMPLETED = false;
    private static final Tag<@NotNull String> FISHING_ROD_ITEM_TAG = Tag.String("fishing:super_rod");

    private static final Sound SPEECH = Sound.sound().type(SoundEvent.ENTITY_VILLAGER_AMBIENT).pitch(0.9F).build();
    private static final Sound HIGH_PITCH = Sound.sound().type(SoundEvent.ENTITY_VILLAGER_YES).pitch(1.5f).build();
    private static final TaskSchedule DIALOG_DELAY = TaskSchedule.seconds(2);

    public static boolean isQuestCompleted() {
        return QUEST_COMPLETED;
    }

    public static void completeQuest(Player playerWhoCompleted) {
        if (!QUEST_COMPLETED) {
            QUEST_COMPLETED = true;
            playerWhoCompleted.sendMessage(Component.text("§aYou gave the Fisherman the FishingRod Super 9000B! The mayor gains his vote."));
        }
    }

    /**
     * Builds the entire quest branch and connects it to the Fisherman NPC's root node.
     * The final step of both dialogue branches CONNECTS back to the Root Node to
     * reset the player's interaction state.
     */
    public void setup() {
        // Retrieve the root node directly from the injected NPC instance
        InteractionNode fishermanRoot = fishermanNPC.getInteractionGraph().getRoot();

        // -------------------------------------------------------------------
        // 1. DEFINE DIALOGUE NODES (Completed Path)
        // -------------------------------------------------------------------

        // This node starts the completed dialogue sequence.
        // Priority: 200 (Highest), only if the quest is completed.
        DialogNode completedLine1 = DialogNode.builder()
                .text(Component.text("Fisherman: Someone already brought me the rod!"))
                .sound(SPEECH)
                .delay(DIALOG_DELAY)
                .autoAdvance(true)
                .priority(event -> FishermanQuestInteractions.isQuestCompleted() ? 200 : -1)
                .build();

        DialogNode completedLine2 = DialogNode.builder()
                .text(Component.text("Fisherman: The mayor will get my vote. Thanks!"))
                .sound(SPEECH)
                .autoAdvance(false) // Allows connection back to Root
                .build();


        // -------------------------------------------------------------------
        // 2. DEFINE DIALOGUE NODES (Active Path)
        // -------------------------------------------------------------------

        // This node starts the active dialogue sequence.
        // Priority: 100, only if the quest is NOT completed.
        DialogNode activeLine1 = DialogNode.builder()
                .text(Component.text("Fisherman: Oog! What's this?"))
                .sound(SPEECH)
                .delay(DIALOG_DELAY)
                .autoAdvance(true)
                .priority(event -> !FishermanQuestInteractions.isQuestCompleted() ? 100 : -1)
                .build();

        DialogNode activeLine2 = DialogNode.builder()
                .text(Component.text("Fisherman: A letter from the mayor? Hmmm..."))
                .sound(SPEECH)
                .delay(DIALOG_DELAY)
                .autoAdvance(true)
                .build();

        DialogNode activeLine3 = DialogNode.builder()
                .text(Component.text("Fisherman: If you want me to vote for him..."))
                .sound(SPEECH)
                .delay(DIALOG_DELAY)
                .autoAdvance(true)
                .build();

        DialogNode activeLine4 = DialogNode.builder()
                .text(Component.text("Fisherman: Bring me the FishingRod Super 9000B!"))
                .sound(HIGH_PITCH)
                .autoAdvance(false) // Allows connection back to Root
                .build();


        // -------------------------------------------------------------------
        // 3. DEFINE START NODE (Letter Check Filter)
        // -------------------------------------------------------------------

        // Node triggered only if the player holds the letter (High Priority 100 on the root)
        ToIntFunction<PlayerEntityInteractEvent> isHoldingLetter = event -> event.getPlayer()
                .getItemInMainHand()
                .hasTag(MayorLetterItem.MAYOR_LETTER) ? 100 : -1;

        InteractionNode startNode = InteractionNode.create(
                event -> { /* Initial action */ },
                isHoldingLetter,
                true, // Auto-advance immediately to the quest status check (its children)
                TaskSchedule.immediate()
        );


        // -------------------------------------------------------------------
        // 4. CONSOLIDATE ALL CONNECTIONS AND LINKS
        // -------------------------------------------------------------------

        // A. Final Root Reset Connections (Crucial for unblocking)
        completedLine2.connect(fishermanRoot);
        activeLine4.connect(fishermanRoot);

        // B. Linear Dialogue Flows
        completedLine1.connect(completedLine2); // Completed line 1 -> line 2 -> Root

        activeLine1.connect(activeLine2);       // Active line 1 -> 2
        activeLine2.connect(activeLine3);       // Active line 2 -> 3
        activeLine3.connect(activeLine4);       // Active line 3 -> 4 -> Root

        // C. Quest Status Branching (StartNode connects directly to the two conditional starting lines)
        // The highest priority child will be chosen based on the static QUEST_COMPLETED state.
        startNode.connect(completedLine1);
        startNode.connect(activeLine1);

        // D. Connect the entire quest branch to the Fisherman's Root
        fishermanRoot.connect(startNode);
    }
}