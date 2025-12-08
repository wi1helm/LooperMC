package nub.wi1helm.module.modules.mail.interaction;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule; // New Import for delay control
import nub.wi1helm.Utils;
import nub.wi1helm.entity.interaction.DialogNode;      // New Import for dialogue abstraction
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mail.entity.Mailbox;
import nub.wi1helm.module.modules.mail.entity.PostmasterNPC;
import nub.wi1helm.module.modules.mail.items.PackageItem;

import java.util.Arrays;
import java.util.List;

/**
 * Handles all interactions for the {@link PostmasterNPC}.
 * <p>
 * Uses a graph of {@link InteractionNode} objects to determine
 * the appropriate response when a player interacts with the NPC.
 * The interaction graph checks conditions in order of priority
 * and executes the first matching node.
 */
public class PostmasterNPCInteraction {

    private final List<Mailbox> mailboxes;
    private final GoalManager goalManager;
    private final ItemManager itemManager;
    private final PostmasterNPC npc;

    /**
     * Creates a new interaction handler for the Postmaster NPC.
     *
     * @param npc         The NPC this interaction is attached to.
     * @param mailboxes   The list of mailboxes in the world.
     * @param goalManager The goal manager to track delivery goals.
     * @param itemManager The item manager to generate packages.
     */
    public PostmasterNPCInteraction(PostmasterNPC npc, List<Mailbox> mailboxes,
                                    GoalManager goalManager, ItemManager itemManager) {
        this.npc = npc;
        this.mailboxes = mailboxes;
        this.goalManager = goalManager;
        this.itemManager = itemManager;
    }

    /**
     * Helper method to count the total number of packages a player is currently holding.
     */
    private long countPlayerPackages(Player player) {
        return Arrays.stream(player.getInventory().getItemStacks())
                .filter(item -> item.hasTag(PackageItem.ITEM_TAG))
                .mapToLong(ItemStack::amount)
                .sum();
    }

    /**
     * Sets up the interaction graph for this NPC.
     */
    public void setup() {
        // --- High Priority Dialogue Nodes (Use DialogNode for clean feedback) ---

        // Node 1: No active mail goal (Priority 100)
        DialogNode noPackages = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<aqua>Sorry, no packages for you today</aqua>"))
                .type(SoundEvent.ENTITY_VILLAGER_NO).pitch(1.0f)
                .manual() // Requires click to advance/terminate
                .priority(InteractionPriority.condition(event -> !goalManager.hasActiveGoal(ServerGoals.DELIVER_MAIL), 100))
                .build();

        // Node 2: Goal completed (Priority 90)
        DialogNode alreadyComplete = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<white>Thank you for helping. We done here</white>"))
                .type(SoundEvent.ENTITY_VILLAGER_TRADE).pitch(1.3f)
                .manual()
                .priority(InteractionPriority.condition(event -> goalManager.isGoalCompleted(ServerGoals.DELIVER_MAIL), 90))
                .build();

        // Node 3: Player already has too many packages (Priority 80)
        DialogNode tooManyPackages = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<red>You already have too many packages!</red>"))
                .type(SoundEvent.ENTITY_VILLAGER_NO).pitch(0.4f)
                .manual()
                .priority(InteractionPriority.condition(event -> countPlayerPackages(event.getPlayer()) >= 3, 80))
                .build();


        // --- Low Priority Action Sequence (Giving the Package) ---

        // Node 4B: Confirmation Dialog (DialogNode)
        // This node is the final step in the sequence.
        DialogNode confirmDialogNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<green>Deliver this to the right address!</green>"))
                .type(SoundEvent.ENTITY_VILLAGER_YES)
                .manual() // It's the end of the sequence, so it requires the next click to reset state.
                .priority(InteractionPriority.always(0)) // Default priority
                .build();

        // Node 4A: Give Item (Custom Action Node)
        // This node performs the inventory action and then instantly auto-advances to the dialog node.
        InteractionNode giveItemNode = InteractionNode.create(
                event -> {
                    Player player = event.getPlayer();

                    // Safety Check (redundant if Node 3 is linked correctly, but good practice)
                    if (countPlayerPackages(player) >= 3) return;

                    // 1. Select a random mailbox destination
                    Mailbox mailbox = Utils.randomElement(mailboxes);
                    // 2. Create the package item with destination data
                    PackageItem item = new PackageItem(mailbox.getAddress(), mailbox.getUUID());
                    // 3. Give item to player
                    player.getInventory().addItemStack(item.getPlayerItem(itemManager));
                },
                InteractionPriority.always(0), // Lowest priority, wins if nodes 1, 2, 3 fail
                true,                        // *** AUTO-ADVANCE: TRUE ***
                TaskSchedule.immediate()     // *** DELAY: IMMEDIATE (instant switch) ***
        );

        // --- Link Nodes ---

        // 1. Link the Action Node to the Confirmation Dialog
        giveItemNode.connect(confirmDialogNode);

        // 2. Link all high-priority nodes and the starting action node to the root
        InteractionNode root = npc.getInteractionGraph().getRoot();

        // High-Priority Dialogue
        root.link(noPackages);
        root.connect(alreadyComplete);
        root.link(tooManyPackages);

        // Low-Priority Action Sequence Start
        root.connect(giveItemNode);
        confirmDialogNode.connect(root);
    }
}