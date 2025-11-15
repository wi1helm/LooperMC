package nub.wi1helm.content.chores.laundry;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import nub.wi1helm.Utils;
import nub.wi1helm.eventnodes.listeners.InteractEntityEvent;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class LaundryBasket {

    // Use Tag.Integer for capacity persistence across server restarts if needed
    // For simplicity here, we'll use a field, but a Tag is better practice.
    public static final Tag<Integer> CAPACITY_TAG = Tag.Integer("basket_capacity");
    public static final Tag<@NotNull UUID> BASKET_TAG = Tag.UUID("basket");
    public static final Tag<@NotNull String> CLOTHES_TAG = Tag.String("clothes");

    private int capacity = 30; // Current capacity, initialized
    private final Instance instance;
    private final Pos position;
    private final Entity hitbox; // Renamed 'box' to 'hitbox' for clarity
    private final Entity text;

    public LaundryBasket(Instance instance, Pos position) {
        this.instance = instance;
        this.position = position;

        // Initialize entities
        this.hitbox = createHitbox();
        this.text = createTextDisplay();

        // Set initial capacity tag/data
        this.hitbox.setTag(CAPACITY_TAG, this.capacity);

        // Add the listener to the shared static node
        // NOTE: We attach the listener to the static node and filter inside handlePlayerInteract
        this.hitbox.eventNode().addListener(InteractEntityEvent.class, this::handlePlayerInteract);

        // Initial text update
        updateText();
    }

    private Entity createHitbox() {
        Entity hitbox = new Entity(EntityType.INTERACTION);

        hitbox.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setResponse(true); // Ensure it generates interaction events
            meta.setHeight(1.2F);
            meta.setWidth(1.1F);
        });

        // Tag the hitbox for filtering in listeners
        hitbox.setTag(BASKET_TAG, hitbox.getUuid());
        return hitbox;
    }

    private Entity createTextDisplay() {
        Entity text = new Entity(EntityType.TEXT_DISPLAY);

        text.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
            // Offset the text display above the hitbox
            meta.setTranslation(new Pos(0, 1.2, 0));
            meta.setBackgroundColor(0);
        });

        return text;
    }

    /**
     * Updates the text display based on the active goal and current capacity.
     */
    private void updateText() {
        if (GoalManager.get().hasActiveGoal(ServerGoals.TOWN_CHORES)) {
            this.capacity = hitbox.getTag(CAPACITY_TAG); // Read current capacity from tag
            Component message = Component.text("🧺 Clothes: " + this.capacity + " left");
            this.text.editEntityMeta(TextDisplayMeta.class, meta -> meta.setText(message));
        } else {
            // Default text when goal is inactive
            this.text.editEntityMeta(TextDisplayMeta.class, meta -> meta.setText(Component.text("")));
        }
    }

    /**
     * Handles the interaction logic when the hitbox is clicked.
     * This method is called from the static event node and MUST filter the target.
     */
    private void handlePlayerInteract(InteractEntityEvent event) {
        if (event.getHand() != PlayerHand.MAIN) return;

        // Filter: Check if the clicked entity is THIS basket's hitbox
        if (!event.getTarget().getUuid().equals(this.hitbox.getUuid())) {
            return;
        }

        // Get current capacity
        int currentCapacity = this.hitbox.getTag(CAPACITY_TAG);

        if (currentCapacity > 0) {
            // Decrement capacity
            currentCapacity--;
            this.hitbox.setTag(CAPACITY_TAG, currentCapacity);

            // Give player the item (Assuming a static method exists for the item)
            // Replace with your actual item logic
            ItemStack laundryItem = getClothesItem();
            event.getPlayer().getInventory().addItemStack(laundryItem);

            // Update text display
            updateText();

            event.getPlayer().sendMessage(Component.text("You grabbed some clothes! (" + currentCapacity + " left)"));
        } else {
            event.getPlayer().sendMessage(Component.text("This basket is empty!"));
        }
    }

    public void spawn() {
        hitbox.setInstance(this.instance, this.position);

        // The TextDisplay entity should be spawned at the same position
        text.setInstance(this.instance, this.position);
    }

    public Entity getHitbox() {
        return this.hitbox;
    }

    public ItemStack getClothesItem() {
        List<Material> types = List.of(Material.LEATHER_HELMET,Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

        return ItemStack.builder(Utils.randomElement(types))
                .amount(1)
                .set(DataComponents.DYED_COLOR, Utils.randomColor())
                .set(CLOTHES_TAG, "clothes")
                .build();
    }

    // Static Tag Getter (renamed to align with field)
    public static Tag<@NotNull UUID> getBasketTag(){
        return BASKET_TAG;
    }

    public static Tag<@NotNull String> getClothesTag() {return CLOTHES_TAG; }
}