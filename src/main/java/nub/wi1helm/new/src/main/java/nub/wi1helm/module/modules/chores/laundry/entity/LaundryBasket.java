package nub.wi1helm.module.modules.chores.laundry.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.chores.laundry.interaction.LaundryBasketInteraction; // New Import
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LaundryBasket extends GameEntity {

    private final GoalManager goalManager;
    private final ItemManager itemManager;
    private final Pos position;
    private final int capacity = 30;

    private final Entity hitbox;   // interaction target
    private final Entity label;    // text display
    private final Set<LaundryLine> lines;

    private final LaundryBasketInteraction interaction; // New field

    // Tags
    public static final Tag<@NotNull Integer> CAPACITY = Tag.Integer("module:laundry:basket:capacity");
    public static final Tag<@NotNull UUID> IDENTIFIER = Tag.UUID("module:laundry:basket:id");
    public static final Tag<@NotNull String> CLOTHES_TAG = Tag.String("module:laundry:clothes");


    public LaundryBasket(GoalManager goalManager, ItemManager itemManager, Pos position, Set<LaundryLine> lines) {
        this.goalManager = goalManager;
        this.itemManager = itemManager;
        this.position = position;
        this.lines = lines;

        this.hitbox = createHitbox();
        this.label = createLabel();

        hitbox.setTag(CAPACITY, capacity);
        hitbox.setTag(IDENTIFIER, hitbox.getUuid());

        // Initialize the InteractionGraph using the new class
        this.interaction = new LaundryBasketInteraction(this, goalManager, itemManager, lines);
    }

    // -------------------- Entities --------------------

    private Entity createHitbox() {
        Entity e = new Entity(EntityType.INTERACTION);
        e.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(1.2f);
            meta.setWidth(1.1f);
            meta.setResponse(true); // required for interaction events
        });
        return e;
    }

    private Entity createLabel() {
        Entity e = new Entity(EntityType.TEXT_DISPLAY);
        e.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
            meta.setTranslation(new Pos(0, 1.2, 0));
            meta.setBackgroundColor(0);
            meta.setShadow(true);
        });
        return e;
    }

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(hitbox, label);
    }

    // -------------------- Interaction Graph --------------------

    @Override
    public void setupInteractions() {
        // Delegate setup to the interaction handler
        interaction.setup();
    }

    // Public getters for the interaction logic to access required fields
    public Entity getHitbox() {
        return hitbox;
    }

    public Entity getLabel() {
        return label;
    }

    // This method needs to be public so the interaction class can call it to update the display
    public void updateLabel() {
        label.editEntityMeta(TextDisplayMeta.class, meta -> {
            if (goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES)) {
                int amount = hitbox.getTag(CAPACITY);
                meta.setText(Component.text("🧺 Clothes: " + amount));
            } else {
                meta.setText(Component.text(""));
            }
        });
    }

    // -------------------- Spawn --------------------

    @Override
    public void spawn(EntityManager manager, Instance instance, Pos pos) {
        // Not used — we spawn using the other method.
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        getMinestomEntities().forEach(e -> e.setInstance(instance, position));
        manager.register(this, hitbox); // use hitbox as the main entity
        updateLabel();
    }

    // -------------------- Helpers --------------------

    // updateLabel was moved to be a public method above

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:laundry:basket");
    }
}