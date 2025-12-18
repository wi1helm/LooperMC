package nub.wi1helm.module.modules.statuepolish.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import nub.wi1helm.module.modules.statuepolish.interaction.HeroStatueInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HeroStatue extends GameEntity {

    private final Entity hitbox;
    private final Entity text;
    private final Entity info;

    private final Integer max;
    private Integer polished = 0;

    private final HeroStatueInteraction interaction;
    private final GoalManager goalManager;

    public HeroStatue(GoalManager goalManager, Integer max) {
        this.goalManager = goalManager;

        this.max = max;

        this.hitbox = createHitbox();
        this.text = createText(Component.text("Hero Statue").color(NamedTextColor.GOLD));
        this.info = createText(Component.empty());

        this.interaction = new HeroStatueInteraction(this, goalManager);

        if (goalManager.hasActiveGoal(ServerGoals.STATUE_POLISH)) updateInfo();
    }

    private Entity createText(Component component) {
        Entity entity = new Entity(EntityType.TEXT_DISPLAY);
        entity.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(component);
            meta.setHasNoGravity(true);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setShadow(true);
            meta.setBackgroundColor(0);
        });
        return entity;
    }

    private Entity createHitbox() {
        Entity entity = new Entity(EntityType.INTERACTION);
        entity.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(6);
            meta.setWidth(3);
            meta.setResponse(true);
            meta.setHasNoGravity(true);
        });

        return entity;
    }

    @Override
    public void setupInteractions() {
        interaction.setup();
    }

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(hitbox, text);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance, Pos pos) {
        manager.register(this, hitbox);
        hitbox.setInstance(instance, pos);
        text.setInstance(instance, pos.add(-2,2.5,0));
        info.setInstance(instance, pos.add(-2,2.0,0));
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {

    }

    public void updateInfo() {
        info.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(Component.text("Polished: " + polished + "/" + max).color(NamedTextColor.YELLOW));
        });
    }

    public void polish() {
        if (polished >= max) {
            info.editEntityMeta(TextDisplayMeta.class, meta -> {
                meta.setText(Component.text("Fucking Done").color(NamedTextColor.YELLOW));
            });
            return;
        }
        polished++;

        updateInfo();

    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:statuepolish:entity:herostatue");
    }
}
