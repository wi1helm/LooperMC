package nub.wi1helm.npc; // Adjust package as needed

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public abstract class NPC extends Entity {

    public static final Tag<@NotNull UUID> NPC_TAG = Tag.UUID("npc");

    // Store the required spawn information
    private final Instance targetInstance;
    private final Pos spawnPosition;

    // References to the passengers
    private final Entity name;
    private final Entity spacer;

    /**
     * Constructs a new NPC, initializing all objects but NOT spawning them.
     * Spawning is deferred to the public spawn() method.
     */
    public NPC(@NotNull Component name, @NotNull PlayerSkin skin, @NotNull Instance instance, @NotNull Pos spawnPosition) {
        super(EntityType.MANNEQUIN);

        NPCManager.registerNPC(this);

        // Store target instance and position
        this.targetInstance = instance;
        this.spawnPosition = spawnPosition;

        // 1. Set the skin/profile
        editEntityMeta(MannequinMeta.class, meta -> {
            meta.setProfile(new ResolvableProfile(skin));
        });

        // 2. Set the custom tag
        setTag(NPC_TAG, getUuid());

        // 3. Create the passenger entities
        this.name = createName(name);
        this.spacer = createSpacer();


    }

    /**
     * Spawns the NPC and all its passengers into the target instance at the spawn position.
     * This is the method external classes will call to make the NPC appear.
     */
    public void spawn() {
        // 1. Spawn the root NPC. This automatically spawns all attached passengers
        // and sets their instance and position, simplifying the required steps.
        this.setInstance(this.targetInstance, this.spawnPosition);

        // 2. Attach passengers to establish the hierarchy
        this.addPassenger(this.spacer);
        spacer.addPassenger(this.name);

    }

    private Entity createName(@NotNull Component name) {
        Entity nameEntity = new Entity(EntityType.TEXT_DISPLAY);
        nameEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(name);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        });
        return nameEntity;
    }

    private Entity createSpacer() {
        Entity interaction = new Entity(EntityType.INTERACTION);
        interaction.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(0.2F);
            meta.setWidth(0.1F);
        });
        return interaction;
    }
}