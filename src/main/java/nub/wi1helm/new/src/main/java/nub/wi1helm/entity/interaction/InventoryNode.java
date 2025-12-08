package nub.wi1helm.entity.interaction;

import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.timer.TaskSchedule;

import java.util.function.ToIntFunction;

/**
 * A node that opens an inventory GUI.
 * Typically does NOT auto-advance (waits for player to close inventory).
 */
public class InventoryNode extends InteractionNode {

    private InventoryNode(Inventory inventory,
                          ToIntFunction<PlayerEntityInteractEvent> priority,
                          boolean autoAdvance,
                          TaskSchedule delay) {
        super(
                event -> event.getPlayer().openInventory(inventory),
                priority,
                autoAdvance,
                delay
        );
    }

    // ------------------------------------------------------------------------
    // Builder
    // ------------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Inventory inventory;
        private ToIntFunction<PlayerEntityInteractEvent> priority = InteractionPriority.always(0);
        private boolean autoAdvance = false; // Inventories typically wait for close
        private TaskSchedule delay = TaskSchedule.immediate();

        public Builder inventory(Inventory inventory) {
            this.inventory = inventory;
            return this;
        }

        public Builder priority(ToIntFunction<PlayerEntityInteractEvent> priority) {
            this.priority = priority;
            return this;
        }

        public Builder autoAdvance(boolean autoAdvance) {
            this.autoAdvance = autoAdvance;
            return this;
        }

        public Builder delay(TaskSchedule delay) {
            this.delay = delay;
            return this;
        }
        /**
         * Convenience: manual advancement (requires click)
         */
        public Builder manual() {
            this.autoAdvance = false;
            return this;
        }

        public InventoryNode build() {
            if (inventory == null) {
                throw new IllegalStateException("Inventory must be set");
            }
            return new InventoryNode(inventory, priority, autoAdvance, delay);
        }
    }
}