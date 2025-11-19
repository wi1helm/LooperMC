package nub.wi1helm.entity.npc;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.player.ResolvableProfile;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for all non-player characters.
 * Now supports multiple stacked text displays above the mannequin.
 */
public abstract class NPC extends GameEntity {

    protected final Entity mannequin;
    protected PlayerSkin skin;

    // A list of spacers and displays for stacked text
    protected final List<Entity> spacers = new ArrayList<>();
    protected final List<Entity> displays = new ArrayList<>();

    protected final List<Component> texts;

    public NPC(@NotNull PlayerSkin skin, Component... texts) {
        this(texts);
        setSkin(skin);
    }
    // Constructor now takes multiple components
    public NPC(Component... texts) {
        this.texts = Arrays.stream(texts).toList();

        // 1. The main visible entity
        this.mannequin = new Entity(EntityType.MANNEQUIN);
        this.mannequin.editEntityMeta(MannequinMeta.class, meta -> {
            if (skin != null) meta.setProfile(new ResolvableProfile(skin));
        });

        // 2. Create spacers and displays for each component
        for (Component text : texts) {
            // InteractionEntity acts as offset
            Entity spacer = new Entity(EntityType.INTERACTION);
            spacer.editEntityMeta(InteractionMeta.class, meta -> {
                meta.setHeight(0.3F); // adjust spacing if needed
                meta.setWidth(0.2F);
            });

            // TextDisplay entity
            Entity display = new Entity(EntityType.TEXT_DISPLAY);
            display.editEntityMeta(TextDisplayMeta.class, meta -> {
                meta.setText(text);
                meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            });

            spacers.add(spacer);
            displays.add(display);
        }
    }

    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        // 1. Spawn the mannequin into the world
        this.mannequin.setInstance(instance, position);

        // 2. Attach all spacers/displays as stacked passengers
        Entity currentParent = mannequin;
        for (int i = 0; i < spacers.size(); i++) {
            Entity spacer = spacers.get(i);
            Entity display = displays.get(i);

            spacer.setInstance(instance); // attach to world
            display.setInstance(instance);

            currentParent.addPassenger(spacer);
            spacer.addPassenger(display);

            currentParent = spacer; // next spacer will attach to this one
        }

        entityManager.register(this, this.mannequin);
    }

    public void setSkin(PlayerSkin skin) {
        this.skin = skin;
        mannequin.editEntityMeta(MannequinMeta.class, mannequinMeta -> {
            mannequinMeta.setProfile(new ResolvableProfile(skin));
        });
    }

    @Override
    public abstract void onPlayerInteract(PlayerEntityInteractEvent event);

    @Override
    public List<Entity> getMinestomEntities() {
        List<Entity> allEntities = new ArrayList<>();
        allEntities.add(mannequin);
        allEntities.addAll(spacers);
        allEntities.addAll(displays);
        return allEntities;
    }

    public Entity getMannequin() {
        return mannequin;
    }
}
