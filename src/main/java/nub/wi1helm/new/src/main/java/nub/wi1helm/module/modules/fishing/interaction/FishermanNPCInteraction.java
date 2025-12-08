package nub.wi1helm.module.modules.fishing.interaction;

import nub.wi1helm.entity.interaction.InventoryNode;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.fishing.core.FishingManager;
import nub.wi1helm.module.modules.fishing.entity.FishermanNPC;
import nub.wi1helm.module.modules.fishing.inventory.FishermanShop;

/**
 * Handles all interactions for the {@link FishermanNPC}.
 * Specifically, setting up the shop GUI interaction node.
 */
public record FishermanNPCInteraction(FishermanNPC npc, ItemManager itemManager, FishingManager fishingManager) {

    /**
     * Defines the interaction flow, which currently only opens the shop GUI.
     */
    public void setup() {
        // Create an InventoryNode that opens the FishermanGUI whenever executed.
        InventoryNode guiNode = InventoryNode.builder()
                // The inventory must be instantiated every time to ensure fresh state if needed.
                .inventory(new FishermanShop(itemManager, fishingManager))
                // Use default priority (0) and always run
                .priority(InteractionPriority.always(0))
                // Do not auto-advance; the interaction chain ends here until the player clicks again.
                .manual()
                .build();

        // Link the GUI node directly to the root.
        // Since it's the only node, it will run on any valid click.
        InteractionNode root = npc.getInteractionGraph().getRoot();
        root.link(guiNode);
    }
}