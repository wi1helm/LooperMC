package nub.wi1helm.module.modules.mail;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Represents a mailbox composed of multiple entities.
 */
public class Mailbox extends GameEntity {
    private final GoalManager goalManager;
    private final ItemManager itemManager;

    private final Component address;
    private final Pos position;

    private final Entity head;
    private final Entity leg;
    private final Entity box;
    private final Entity name;

    public Mailbox(GoalManager goalManager, ItemManager itemManager, Pos position, Component address) {
        this.goalManager = goalManager;
        this.itemManager = itemManager;
        this.address = address;
        this.position = position;

        // Initialize entities
        this.head = createHead();
        this.leg = createLeg();
        this.box = createBox();
        this.name = createName(address);
    }

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(head, leg, box, name);
    }

    @Override
    public void onPlayerInteract(PlayerEntityInteractEvent event) {
        if (event.getHand() != PlayerHand.MAIN) return;

        final Player player = event.getPlayer();
        final Entity entity = event.getTarget();
        final ItemStack item = player.getItemInMainHand();

        if (item.isAir()) return;

        if (item.getTag(ADDRESS_TAG) == null) return;
        if (!item.getTag(ADDRESS_TAG).equals(entity.getUuid())) return;

        this.goalManager.incrementGoal(ServerGoals.DELIVER_MAIL, item.amount());

        player.getInventory().setItemStack(event.getPlayer().getHeldSlot(), ItemStack.AIR);
        player.sendActionBar(MiniMessage.miniMessage().deserialize("<white>[Matt]</white> <green>Great you delivered to the right address</green>"));
        player.playSound(Sound.sound().type(SoundEvent.ENTITY_VILLAGER_CELEBRATE).source(Sound.Source.NEUTRAL).build());

    }

    @Override
    public void spawn(EntityManager manager, Instance instance, Pos pos) {

    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        // Spawn all parts of the mailbox in the instance
        getMinestomEntities().forEach(entity -> entity.setInstance(instance, this.position));
        manager.register(this, box); // Use the box as the "main" entity reference
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
            ItemStack head = ItemStack.of(Material.PLAYER_HEAD).with(DataComponents.PROFILE, profile);
            meta.setTranslation(new Pos(0,1.3,0));
            meta.setItemStack(head);
        });
        return entity;
    }

    private Entity createLeg() {
        Entity entity = new Entity(EntityType.BLOCK_DISPLAY);
        entity.editEntityMeta(BlockDisplayMeta.class, meta -> {
            Block leg = Block.SPRUCE_FENCE;
            meta.setBlockState(leg);
            meta.setTranslation(new Pos(-0.5,0,-0.5));
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

    private Entity createName(Component name){
        Entity entity = new Entity(EntityType.TEXT_DISPLAY);
        entity.editEntityMeta(TextDisplayMeta.class, meta -> {
            if (this.goalManager.hasActiveGoal(ServerGoals.DELIVER_MAIL)) meta.setText(name);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
            meta.setTranslation(new Pos(0,1.5,0));
        });
        return entity;
    }

    public static Tag<@NotNull UUID> ADDRESS_TAG = Tag.UUID("module:mail:mailbox:address");

    public Component getAddress() {
        return address;
    }

    public UUID getUUID() {
        return box.getUuid();
    }
}
