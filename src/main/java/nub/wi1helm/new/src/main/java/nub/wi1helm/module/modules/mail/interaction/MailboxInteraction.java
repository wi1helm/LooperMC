package nub.wi1helm.module.modules.mail.interaction;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag; // New Import
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority; // New Import
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.modules.mail.entity.Mailbox;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

/**
 * Handles player interaction with a mailbox using a priority graph.
 * This separates the logic into distinct nodes for better management.
 */
public class MailboxInteraction {

    private final Mailbox mailbox;
    private final GoalManager goalManager;
    private final UUID mailboxUUID; // Cached for cleaner checks

    public MailboxInteraction(Mailbox mailbox, GoalManager goalManager) {
        this.mailbox = mailbox;
        this.goalManager = goalManager;
        this.mailboxUUID = mailbox.getUUID();
    }

    /** Helper function to check if the held item is a mail package. */
    private boolean isHoldingMail(ItemStack item) {
        return !item.isAir() && item.hasTag(Mailbox.ADDRESS_TAG);
    }

    /** Sets up the mailbox interaction node graph. */
    public void setup() {
        InteractionNode root = mailbox.getInteractionGraph().getRoot();

        // --- Base Condition ---
        // Must be main hand and goal must be active. Priority 1 (just for filtering)
        // If this returns -1, the interaction is ignored (no fallback node needed).
        ToIntFunction<PlayerEntityInteractEvent> baseCondition = InteractionPriority.condition(
                event -> event.getHand() == PlayerHand.MAIN && goalManager.hasActiveGoal(ServerGoals.DELIVER_MAIL),
                1
        );

        // --- 1. Wrong Mail (Priority 20) ---
        // Wins over Correct Mail if the UUIDs don't match.
        Consumer<PlayerEntityInteractEvent> wrongMailAction = event -> {
            Player player = event.getPlayer();
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<white>[Mailbox]</white> <yellow>This mail is not for this address!</yellow>"));
            player.playSound(Sound.sound().type(SoundEvent.ITEM_ARMOR_EQUIP_IRON).pitch(1.4F).source(Sound.Source.NEUTRAL).build());
        };

        InteractionNode wrongMailNode = InteractionNode.create(
                wrongMailAction,
                InteractionPriority.allOf(
                        baseCondition,
                        InteractionPriority.condition(event -> {
                            ItemStack item = event.getPlayer().getItemInMainHand();
                            return isHoldingMail(item) && !item.getTag(Mailbox.ADDRESS_TAG).equals(mailboxUUID);
                        }, 20) // Priority 20
                ),
                false,
                null
        );

        // --- 2. Correct Mail (Priority 10) ---
        // Only runs if base condition is met, player holds mail, and it's the right address.
        // Node 1's condition (Priority 20) will have failed (returned -1) if the mail is correct.
        Consumer<PlayerEntityInteractEvent> correctMailAction = event -> {
            Player player = event.getPlayer();
            ItemStack item = player.getItemInMainHand();

            // Deliver mail
            goalManager.incrementGoal(ServerGoals.DELIVER_MAIL, item.amount());
            player.getInventory().setItemStack(player.getHeldSlot(), ItemStack.AIR);

            player.sendActionBar(MiniMessage.miniMessage().deserialize("<white>[Mailbox]</white> <green>Great! Mail delivered!</green>"));
            player.playSound(Sound.sound().type(SoundEvent.ENTITY_VILLAGER_CELEBRATE).source(Sound.Source.NEUTRAL).build());
        };

        InteractionNode correctMailNode = InteractionNode.create(
                correctMailAction,
                InteractionPriority.allOf(
                        baseCondition,
                        InteractionPriority.condition(event -> {
                            ItemStack item = event.getPlayer().getItemInMainHand();
                            // Check if the item is mail AND the UUID matches this mailbox
                            return isHoldingMail(item) && item.getTag(Mailbox.ADDRESS_TAG).equals(mailboxUUID);
                        }, 10) // Priority 10
                ),
                false,
                null
        );

        // --- 3. Fallback/Ignored Click (Base Condition only) ---
        // This node is not explicitly created. If the player is not holding mail,
        // both Node 1 and Node 2 return -1, and the baseCondition for the interaction is
        // not met, causing findNext to return null and the interaction to be ignored.

        // Link the nodes. Highest priority wins.
        root.link(wrongMailNode);
        root.link(correctMailNode);
    }
}