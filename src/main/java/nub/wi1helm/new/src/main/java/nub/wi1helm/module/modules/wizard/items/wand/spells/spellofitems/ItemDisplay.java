package nub.wi1helm.module.modules.wizard.items.wand.spells.spellofitems;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemDisplay extends GameEntity {

    private final Entity model;
    private final Entity hitbox;

    private final ItemManager itemManager;

    public ItemDisplay(GameItem item, ItemManager itemManager) {
        this.model = createModel(item);
        this.itemManager = itemManager;
        this.hitbox = createHitbox();
    }

    private Entity createModel(GameItem item){
        Entity model = new Entity(EntityType.ITEM_DISPLAY);
        model.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setItemStack(item.getPlayerItem(itemManager));
            meta.setHasNoGravity(true);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        });
        return model;
    }
    private Entity createHitbox() {
        Entity hitbox = new Entity(EntityType.INTERACTION);
        hitbox.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(1);
            meta.setWidth(1);
            meta.setResponse(true);
        });
        return hitbox;
    }

    @Override
    public void setupInteractions() {

    }

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(hitbox, model);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance, Pos pos) {
        manager.register(this, hitbox);
        hitbox.setInstance(instance, pos);
        model.setInstance(instance, pos);
        hitbox.addPassenger(model);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {

    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:wizard:spell:spellofitems:entity:itemdisplay");
    }

    public Entity getHitbox() {
        return hitbox;
    }
}
