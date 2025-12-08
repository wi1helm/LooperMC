package nub.wi1helm.module.modules.chores.laundry.interaction;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule; // New Import for delay control
import nub.wi1helm.entity.interaction.DialogNode;      // New Import for dialogue abstraction
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.chores.laundry.entity.LaundryBasket;
import nub.wi1helm.module.modules.chores.laundry.entity.LaundryLine;
import nub.wi1helm.module.modules.chores.laundry.items.ClothesItem;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

/**
 * Handles all interactions for the {@link LaundryBasket}.
 * <p>
 * Uses a graph of {@link InteractionNode} objects to determine
 * the appropriate response when a player interacts with the basket.
 */
public class LaundryBasketInteraction {

    private final LaundryBasket basket;
    private final GoalManager goalManager;
    private final ItemManager itemManager;
    private final Set<LaundryLine> lines;

    // ... (Constructor remains the same) ...
    public LaundryBasketInteraction(LaundryBasket basket, GoalManager goalManager, ItemManager itemManager, Set<LaundryLine> lines) {
        this.basket = basket;
        this.goalManager = goalManager;
        this.itemManager = itemManager;
        this.lines = lines;
    }

    /**
     * Helper method to count the number of ClothesItem a player is currently holding.
     */
    private long countPlayerClothes(Player player) {
        return Arrays.stream(player.getInventory().getItemStacks())
                .filter(item -> item.hasTag(ClothesItem.TAG))
                .mapToLong(ItemStack::amount)
                .sum();
    }

    /**
     * Defines the interaction flow and sets up the interaction graph for the Laundry Basket.
     */
    public void setup() {
        // Predicate base for all valid clicks: must be main hand and the chore goal must be active
        ToIntFunction<PlayerEntityInteractEvent> baseCondition = InteractionPriority.condition(
                event -> event.getHand() == PlayerHand.MAIN && goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES),
                1
        );

        // --- 1. Basket is empty (Highest Priority: 30) ---
        // P30 wins conflict with P20 (Player Full) when capacity hits 0.
        DialogNode emptyNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<gray>The laundry basket is empty.</gray>"))
                .type(SoundEvent.ENTITY_ITEM_FRAME_REMOVE_ITEM).pitch(1.0F)
                .manual() // Stops the chain
                .priority(InteractionPriority.allOf(
                        baseCondition,
                        InteractionPriority.condition(event -> basket.getHitbox().getTag(LaundryBasket.CAPACITY) <= 0, 30)
                ))
                .build();

        // --- 2. Player is holding too many clothes (Mid Priority: 20) ---
        DialogNode tooManyNode = DialogNode.builder()
                .text(MiniMessage.miniMessage().deserialize("<red>You already have some clothes man!!</red>"))
                .type(SoundEvent.ENTITY_VILLAGER_NO).pitch(1.4F)
                .manual()
                .priority(InteractionPriority.allOf(
                        baseCondition,
                        InteractionPriority.condition(event -> countPlayerClothes(event.getPlayer()) >= 5, 20)
                ))
                .build();


        // --- 3B. Successful Take Dialogue (DialogNode - Priority 0) ---
        // This is the second step of the successful take sequence.
        DialogNode successDialogNode = DialogNode.builder()
                // The message content is dynamic, so we'll set it in the builder but let 3A set the final text.
                // For simplicity here, we'll keep the text abstract and rely on 3A's action bar for dynamic info.
                .text(MiniMessage.miniMessage().deserialize("<green>You took some clothes!</green>"))
                .type(SoundEvent.ENTITY_ITEM_PICKUP).pitch(0.75F)
                .manual() // Stops the chain
                .priority(InteractionPriority.always(0))
                .build();

        // --- 3A. Successful Take Action (InteractionNode - Default Priority: 1) ---
        // Runs if capacity > 0 AND player clothes < 5. Auto-advances to 3B.
        Consumer<PlayerEntityInteractEvent> successAction = event -> {
            Player player = event.getPlayer();

            // 1. Decrement capacity and update label
            int amount = basket.getHitbox().getTag(LaundryBasket.CAPACITY);
            amount--;
            basket.getHitbox().setTag(LaundryBasket.CAPACITY, amount);
            basket.updateLabel();

            // 2. Give item to player
            player.getInventory().addItemStack(new ClothesItem(goalManager, lines).getPlayerItem(itemManager));
        };

        InteractionNode successActionNode = InteractionNode.create(
                successAction,
                baseCondition, // Runs if the base condition is met (Priority 1)
                true,          // *** AUTO-ADVANCE IS TRUE ***
                TaskSchedule.immediate() // *** NO DELAY ***
        );


        // --- Link the Nodes ---

        // 1. Link the Action Node to the Dialogue Node for the sequence
        successActionNode.connect(successDialogNode);


        // 2. Link all starting nodes to the root by priority (30 > 20 > 1)
        InteractionNode root = basket.getInteractionGraph().getRoot();
        root.link(emptyNode);        // P30
        root.link(tooManyNode);      // P20
        root.connect(successActionNode); // P1 (Start of P1 sequence)
        successDialogNode.connect(root);
    }
}