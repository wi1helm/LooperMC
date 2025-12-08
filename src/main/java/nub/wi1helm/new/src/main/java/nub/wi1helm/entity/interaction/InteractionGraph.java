package nub.wi1helm.entity.interaction;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A per-entity interaction graph with:
 * - a root node (always present)
 * - any number of linked nodes
 * - per-player node progress tracking
 * - automatic chain execution with delays
 */
public class InteractionGraph {

    /** The root node of this graph. */
    private InteractionNode root;

    /** Tracks where each player currently is in this graph. */
    private final Map<UUID, InteractionNode> progress = new ConcurrentHashMap<>();

    /** Tracks pending scheduled tasks for players in a delayed chain. */
    private final Map<UUID, Task> pendingTasks = new ConcurrentHashMap<>();

    public InteractionGraph() {
        this.root = null;
    }

    // ------------------------------------------------------------------------
    // Root Management
    // ------------------------------------------------------------------------

    public void setRoot(InteractionNode root) {
        this.root = root;
    }

    public InteractionNode getRoot() {
        return root;
    }

    // ------------------------------------------------------------------------
    // Execution
    // ------------------------------------------------------------------------

    /**
     * Executes the current node and handles progression.
     *
     * Flow:
     * 1. Check for and cancel any pending scheduled task (Skip behavior).
     * 2. Execute current node's action.
     * 3. Find next node based on priority.
     * 4. Move player to next node.
     * 5. If current node auto-advances, schedule next execution.
     */
    public void execute(PlayerEntityInteractEvent event) {
        if (root == null) return;

        UUID playerId = event.getPlayer().getUuid();

        // 1. If the player is currently waiting for a scheduled task (a delay is active),
        // we cancel the old task and continue the chain immediately (Skip behavior).
        Task pending = pendingTasks.remove(playerId);
        if (pending != null) {
            pending.cancel();
            // Continue the chain immediately from the *next* expected node
            continueChain(event, progress.getOrDefault(playerId, root));
            return; // Exit execution since continueChain handles the rest.
        }

        // Get current node (start at root if no progress)
        InteractionNode current = progress.getOrDefault(playerId, root);

        // Execute current node's action
        current.execute(event);

        // Find next node based on priority
        InteractionNode next = current.findNext(event);
        // Stay at the current node
        if (next == null) return;

        progress.put(playerId, next);

        // If current node auto-advances, schedule next execution
        if (current.shouldAutoAdvance()) {
            scheduleExecution(event, next, current.getDelay());
        }
    }

    /**
     * Schedule execution of a node with optional delay.
     */
    private void scheduleExecution(PlayerEntityInteractEvent event,
                                   InteractionNode node,
                                   TaskSchedule delay) {
        UUID playerId = event.getPlayer().getUuid();

        if (delay == null || delay == TaskSchedule.immediate()) {
            // Execute immediately
            continueChain(event, node);
        } else {
            // Execute after delay
            Task task = MinecraftServer.getSchedulerManager().scheduleTask(
                    () -> continueChain(event, node),
                    delay,
                    TaskSchedule.stop()
            );
            // Store the task so it can be canceled if the player interacts again
            pendingTasks.put(playerId, task);
        }
    }

    /**
     * Continue executing the auto-advance chain.
     */
    private void continueChain(PlayerEntityInteractEvent event, InteractionNode current) {
        UUID playerId = event.getPlayer().getUuid();

        // If this execution came from a scheduled task, remove the task tracker
        // as it has successfully completed (or been cancelled and immediately executed)
        pendingTasks.remove(playerId);

        // Execute current node
        current.execute(event);

        // Find next node
        InteractionNode next = current.findNext(event);

        // Move to next if exists
        if (next != null) {
            progress.put(playerId, next);

            // Continue chain if auto-advancing
            if (current.shouldAutoAdvance()) {
                scheduleExecution(event, next, current.getDelay());
            }
            // Chain stops here if not auto-advancing
        }
        // Chain ends if no next node
    }

    // ------------------------------------------------------------------------
    // Progress
    // ------------------------------------------------------------------------

    public void reset(UUID playerId) {
        // Cancel any pending task when resetting player progress
        Task pending = pendingTasks.remove(playerId);
        if (pending != null) {
            pending.cancel();
        }
        progress.remove(playerId);
    }

    public void resetAll() {
        // Cancel all pending tasks
        pendingTasks.values().forEach(Task::cancel);
        pendingTasks.clear();

        progress.clear();
    }

    public InteractionNode getPlayerNode(UUID playerId) {
        return progress.getOrDefault(playerId, root);
    }
}