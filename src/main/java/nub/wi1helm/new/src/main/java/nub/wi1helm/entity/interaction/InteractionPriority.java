package nub.wi1helm.entity.interaction;

import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Common priority evaluator functions to make building interaction graphs easier.
 *
 * Usage:
 *   node.connectTo(target, InteractionPriority.ifHolding(Material.DIAMOND, 100));
 *   node.connectTo(target, InteractionPriority.condition(myCustomCheck, 50));
 */
public class InteractionPriority {

    /**
     * Returns priority if player holds specified item, otherwise -1 (won't activate)
     */
    public static ToIntFunction<PlayerEntityInteractEvent> ifHolding(Material material, int priority) {
        return event -> {
            Player player = event.getPlayer();
            return player.getItemInMainHand().material() == material ? priority : -1;
        };
    }

    /**
     * Returns priority if player holds ANY of the specified items
     */
    public static ToIntFunction<PlayerEntityInteractEvent> ifHoldingAny(int priority, Material... materials) {
        return event -> {
            Material held = event.getPlayer().getItemInMainHand().material();
            for (Material mat : materials) {
                if (held == mat) return priority;
            }
            return -1;
        };
    }

    /**
     * Returns priority if player is sneaking
     */
    public static ToIntFunction<PlayerEntityInteractEvent> ifSneaking(int priority) {
        return event -> event.getPlayer().isSneaking() ? priority : -1;
    }

    /**
     * Returns priority if player has a specific tag
     */
    public static ToIntFunction<PlayerEntityInteractEvent> ifHasTag(Tag<?> tag, int priority) {
        return event -> event.getPlayer().hasTag(tag) ? priority : -1;
    }

    /**
     * Returns priority if custom condition is met.
     * Use this when you need custom logic that isn't covered by the predefined methods.
     *
     * Example:
     *   InteractionPriority.condition(event ->
     *       event.getPlayer().getLevel() > 10,
     *       100
     *   )
     */
    public static ToIntFunction<PlayerEntityInteractEvent> condition(
            Predicate<PlayerEntityInteractEvent> condition,
            int priority
    ) {
        return event -> condition.test(event) ? priority : -1;
    }

    /**
     * Always returns the same priority (useful for default paths)
     */
    public static ToIntFunction<PlayerEntityInteractEvent> always(int priority) {
        return event -> priority;
    }

    /**
     * Combine multiple evaluators - returns the highest priority among them.
     * Useful for "OR" logic (any condition can trigger).
     */
    @SafeVarargs
    public static ToIntFunction<PlayerEntityInteractEvent> anyOf(ToIntFunction<PlayerEntityInteractEvent>... evaluators) {
        return event -> {
            int maxPriority = -1;
            for (ToIntFunction<PlayerEntityInteractEvent> eval : evaluators) {
                maxPriority = Math.max(maxPriority, eval.applyAsInt(event));
            }
            return maxPriority;
        };
    }

    /**
     * Requires ALL conditions to be met - returns priority only if all return positive values.
     * Useful for "AND" logic (all conditions must be true).
     */
    @SafeVarargs
    public static ToIntFunction<PlayerEntityInteractEvent> allOf(ToIntFunction<PlayerEntityInteractEvent>... evaluators) {
        return event -> {
            int minPriority = Integer.MAX_VALUE;
            for (ToIntFunction<PlayerEntityInteractEvent> eval : evaluators) {
                int p = eval.applyAsInt(event);
                if (p < 0) return -1; // One failed, abort
                minPriority = Math.min(minPriority, p);
            }
            return minPriority;
        };
    }
}