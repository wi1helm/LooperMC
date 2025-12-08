package nub.wi1helm.module.modules.mail.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mail.interaction.MailboxInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class Mailbox extends GameEntity {

    private final GoalManager goalManager;
    private final ItemManager itemManager;
    private final Pos position;
    private final String address;

    private final Entity head;
    private final Entity leg;
    private final Entity box;
    private final Entity name;

    private final MailboxInteraction interaction;

    public Mailbox(GoalManager goalManager, ItemManager itemManager, Pos position, String address) {
        this.goalManager = goalManager;
        this.itemManager = itemManager;
        this.position = position;
        this.address = address;

        // Preserve all metadata/entity setup
        this.head = createHead();
        this.leg = createLeg();
        this.box = createBox();
        this.name = createName(Component.text(address));

        // Attach interaction logic via component
        this.interaction = new MailboxInteraction(this, goalManager);
    }

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(head, leg, box, name);
    }

    @Override
    public void setupInteractions() {
        interaction.setup();
    }

    @Override
    public void spawn(EntityManager manager, Instance instance, Pos pos) {
        getMinestomEntities().forEach(entity -> entity.setInstance(instance, pos));
        manager.register(this, box);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        getMinestomEntities().forEach(entity -> entity.setInstance(instance, this.position));
        manager.register(this, box);
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:mail:entity:mailbox");
    }

    // -------------------- Entity Creation --------------------

    private Entity createHead() {
        Entity entity = new Entity(EntityType.ITEM_DISPLAY);
        entity.editEntityMeta(ItemDisplayMeta.class, meta -> {
            PlayerSkin skin = new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTYxOTQ1OTM0Mzg0OCwKICAicHJvZmlsZUlkIiA6ICJiNWRkZTVmODJlYjM0OTkzYmMwN2Q0MGFiNWY2ODYyMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJsdXhlbWFuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NhODcwOTU4ZTZhZDdlMGQ2N2E4NTYwZmQ0MzU1M2Q2NmQyOGY1OTk2MDAyODhhYzA3MDY3MTg1OGJiZTgwNTAiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "");
            ResolvableProfile profile = new ResolvableProfile(skin);
            ItemStack headItem = ItemStack.of(Material.PLAYER_HEAD).with(DataComponents.PROFILE, profile);
            meta.setTranslation(new Pos(0, 1.3, 0));
            meta.setItemStack(headItem);
        });
        return entity;
    }

    private Entity createLeg() {
        Entity entity = new Entity(EntityType.BLOCK_DISPLAY);
        entity.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(Block.SPRUCE_FENCE);
            meta.setTranslation(new Pos(-0.5, 0, -0.5));
        });
        return entity;
    }

    private Entity createBox() {
        Entity entity = new Entity(EntityType.INTERACTION);
        entity.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(1.5F);
            meta.setWidth(0.5F);
            meta.setResponse(true);
        });
        return entity;
    }

    private Entity createName(Component name) {
        Entity entity = new Entity(EntityType.TEXT_DISPLAY);
        entity.editEntityMeta(TextDisplayMeta.class, meta -> {
            boolean goalActive = goalManager != null && goalManager.hasActiveGoal(ServerGoals.DELIVER_MAIL);
            meta.setText(goalActive ? name : Component.empty());
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
            meta.setTranslation(new Pos(0, 1.5, 0));
            meta.setBackgroundColor(0);
            meta.setShadow(true);
        });
        return entity;
    }

    public static final Tag<@NotNull UUID> ADDRESS_TAG = Tag.UUID("module:mail:mailbox:address");

    public String getAddress() { return address; }

    public UUID getUUID() { return box.getUuid(); }
}
