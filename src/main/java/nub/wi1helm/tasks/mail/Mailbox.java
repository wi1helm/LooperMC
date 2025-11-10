package nub.wi1helm.tasks.mail;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
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
import net.minestom.server.tag.Tag;
import nub.wi1helm.ServerManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.listeners.GlobalListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Mailbox {
    private Pos position;
    private Instance instance;
    private Entity head;
    private Entity leg;
    private Entity box;
    private Entity name;
    private Component address;

    public Mailbox(Pos position, Instance instance, Component name) {
        this.position = position;
        this.instance = instance;
        this.head = head();
        this.leg = leg();
        this.box = box();
        this.name = name(name);
        this.address = name;
    }

    public void spawn() {
        head.setInstance(this.instance, this.position);
        leg.setInstance(this.instance, this.position);
        box.setInstance(this.instance, this.position);
        name.setInstance(this.instance, this.position);
    }

    private Entity head() {

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

    private Entity leg() {

        Entity entity = new Entity(EntityType.BLOCK_DISPLAY);
        entity.editEntityMeta(BlockDisplayMeta.class, meta -> {

            Block leg = Block.SPRUCE_FENCE;

            meta.setBlockState(leg);
            meta.setTranslation(new Pos(-0.5,0,-0.5));
        });

        return entity;
    }

    private Entity box() {

        Entity entity = new Entity(EntityType.INTERACTION);
        entity.editEntityMeta(InteractionMeta.class, meta -> {

            meta.setHeight(1.5F);
            meta.setWidth(0.5F);
            meta.setResponse(true);
        });

        return entity;
    }

    private Entity name(Component name){
        Entity entity = new Entity(EntityType.TEXT_DISPLAY);

        entity.editEntityMeta(TextDisplayMeta.class, meta -> {
            if (ServerManager.getManager().goalManager().hasActiveGoal(ServerGoals.DELIVER_MAIL)) {
                meta.setText(name);
            }
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
            meta.setTranslation(new Pos(0,1.5,0));
        });

        return entity;
    }

    public UUID UUID() {
        return box.getUuid();
    }

    public static Tag<@NotNull UUID> ADDRESS_TAG(){
        return Tag.UUID("address");
    }
    public static Tag<@NotNull String> LETTER_TAG(){
        return Tag.String("letter");
    }

    public ItemStack letterItem() {
        return ItemStack.of(Material.PAPER)
                .withTag(ADDRESS_TAG() , UUID())
                .withTag(LETTER_TAG(),"")
                .with(DataComponents.CUSTOM_NAME, Component.text("Letter To ").append(address));
    }
}
