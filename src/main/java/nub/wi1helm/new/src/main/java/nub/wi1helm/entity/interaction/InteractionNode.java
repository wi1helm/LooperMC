package nub.wi1helm.entity.interaction;

import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

/**
 * A node in an interaction graph.
 *
 * - Has an action that executes when visited
 * - Has a priority evaluator (for choosing between multiple linked nodes)
 * - Can auto-advance to next node (for chained sequences)
 * - Can have a delay before auto-advancing
 */
public class InteractionNode {

    private final List<InteractionNode> nodes = new ArrayList<>();
    private final Consumer<PlayerEntityInteractEvent> action;
    private final ToIntFunction<PlayerEntityInteractEvent> priority;
    private final boolean autoAdvance;
    private final @NotNull TaskSchedule delay;

    public InteractionNode(Consumer<PlayerEntityInteractEvent> action,
                           ToIntFunction<PlayerEntityInteractEvent> priority,
                           boolean autoAdvance,
                           TaskSchedule delay) {
        this.action = action;
        this.priority = priority;
        this.autoAdvance = autoAdvance;
        this.delay = delay != null ? delay : TaskSchedule.immediate();
    }

    /**
     * Creates a bidirectional connection between this node and the given node.
     * <p>
     * After calling this, both nodes will consider each other as reachable.
     * This is useful when you want navigation/cycles in your interaction graph.
     * <p>
     * Example:
     * <pre>
     * root.link(noPackages);
     * </pre>
     * After this, the player can move from root to noPackages or back from noPackages to root.
     *
     * @param node the node to link to this node bidirectionally
     */
    public void link(InteractionNode node) {
        this.connect(node);
        node.connect(this);
    }

    /**
     * Creates a unidirectional connection from this node to the given node.
     * <p>
     * Only this node will have the other node as a child/reachable node.
     * The other node does not know about this node.
     * <p>
     * Example:
     * <pre>
     * givePackage.connect(root);
     * </pre>
     * After this, the player can move from givePackage to root, but not automatically back.
     *
     * @param node the node to connect from this node
     */
    public void connect(InteractionNode node) {
        nodes.add(node);
    }


    /**
     * Execute this node's action.
     */
    public void execute(PlayerEntityInteractEvent event) {
        if (action != null) {
            action.accept(event);
        }
    }

    /**
     * Find the next node based on priority evaluation.
     * Only nodes returning a priority score of 0 or greater are considered valid.
     *
     * @return The highest priority linked node (>= 0), or null if no valid links exist.
     */
    public InteractionNode findNext(PlayerEntityInteractEvent event) {
        return nodes.stream()
                // 1. Filter: Only keep nodes with a priority score >= 0.
                .filter(node -> node.priority.applyAsInt(event) >= 0)
                // 2. Max: Find the highest priority among the remaining nodes (0 or positive).
                .max(Comparator.comparingInt(node -> node.priority.applyAsInt(event)))
                // 3. OrElse: If the filtered stream is empty (i.e., all nodes returned negative), return null.
                .orElse(null);
    }

    public boolean shouldAutoAdvance() {
        return autoAdvance;
    }

    public @NotNull TaskSchedule getDelay() {
        return delay;
    }

    public ToIntFunction<PlayerEntityInteractEvent> getPriority() {
        return priority;
    }

    // ------------------------------------------------------------------------
    // Factory methods
    // ------------------------------------------------------------------------

    public static InteractionNode create(Consumer<PlayerEntityInteractEvent> action,
                                         ToIntFunction<PlayerEntityInteractEvent> priority,
                                         boolean autoAdvance,
                                         TaskSchedule delay) {
        return new InteractionNode(action, priority, autoAdvance, delay);
    }

    public static InteractionNode create(Consumer<PlayerEntityInteractEvent> action,
                                         ToIntFunction<PlayerEntityInteractEvent> priority) {
        return new InteractionNode(action, priority, false, null);
    }

    public static InteractionNode create(Consumer<PlayerEntityInteractEvent> action) {
        return new InteractionNode(action, event -> -1, false, null);
    }

    public static InteractionNode root() {
        return new InteractionNode(null, event -> 0, true, null);
    }
}