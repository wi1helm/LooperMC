package nub.wi1helm.module.modules.chores.pondside.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.modules.chores.pondside.gui.PartBoxInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PartBox extends GameEntity {

    private final EntityManager entityManager;
    private final GoalManager goalManager;
    private final Inventory inventory;

    private final Entity hitbox;
    private final Entity label;

    public PartBox(EntityManager entityManager, GoalManager goalManager) {
        this.entityManager = entityManager;
        this.goalManager = goalManager;
        this.inventory = new PartBoxInventory();

        this.hitbox = createHitbox();
        this.label = createLabel();
    }

    @Override
    public void setupInteractions() {

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

        Component name = goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES) ? Component.text("Part Box").color(NamedTextColor.YELLOW) : Component.empty();

        e.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
            meta.setTranslation(new Pos(0, 1.2, 0));
            meta.setBackgroundColor(0);
            meta.setShadow(true);
            meta.setText(name);
        });
        return e;
    }

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(hitbox, label);
    }


    @Override
    public void spawn(EntityManager manager, Instance instance, Pos position) {
        getMinestomEntities().forEach(e -> e.setInstance(instance, position));
        manager.register(this, hitbox); // use hitbox as the main entity
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {

    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return null;
    }
}
