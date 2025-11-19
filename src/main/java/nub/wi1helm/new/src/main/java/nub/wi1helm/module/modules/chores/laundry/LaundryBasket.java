package nub.wi1helm.module.modules.chores.laundry;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.Utils;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.GameEntity;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mail.PackageItem;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LaundryBasket extends GameEntity {

    private final GoalManager goalManager;
    private final ItemManager itemManager;
    private final Pos position;
    private int capacity = 30;

    private final Entity hitbox;   // interaction target
    private final Entity label;    // text display
    private final Set<LaundryLine> lines;
    // Tags
    public static final Tag<Integer> CAPACITY = Tag.Integer("module:laundry:basket:capacity");
    public static final Tag<UUID> IDENTIFIER = Tag.UUID("module:laundry:basket:id");
    public static final Tag<String> CLOTHES_TAG = Tag.String("module:laundry:clothes");



    public LaundryBasket(GoalManager goalManager, ItemManager itemManager, Pos position, Set<LaundryLine> lines) {
        this.goalManager = goalManager;
        this.itemManager = itemManager;
        this.position = position;
        this.lines = lines;

        this.hitbox = createHitbox();
        this.label = createLabel();

        hitbox.setTag(CAPACITY, capacity);
        hitbox.setTag(IDENTIFIER, hitbox.getUuid());
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
        });
        return e;
    }

    @Override
    public List<Entity> getMinestomEntities() {
        return List.of(hitbox, label);
    }

    // -------------------- Interaction --------------------

    @Override
    public void onPlayerInteract(PlayerEntityInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getHand() != PlayerHand.MAIN) return;

        if (!event.getTarget().getUuid().equals(hitbox.getUuid())) return;

        // Only active during the laundry chore goal
        if (!goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES)) return;

        long num = Arrays.stream(player.getInventory().getItemStacks())
                .filter(item -> item.hasTag(ClothesItem.TAG))
                .mapToLong(ItemStack::amount) // sum the number of items in the stack
                .sum();


        if (num >= 5) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<red>You already have some clothes man!!</red>"));
            player.playSound(Sound.sound().type(SoundEvent.ENTITY_VILLAGER_NO).pitch(1.4F).source(Sound.Source.NEUTRAL).build());
            return;
        }

        int amount = hitbox.getTag(CAPACITY);
        if (amount <= 0) {
            player.sendMessage(Component.text("The basket is empty!"));
            return;
        }

        amount--;
        hitbox.setTag(CAPACITY, amount);
        updateLabel();

       player.getInventory().addItemStack(new ClothesItem(goalManager, lines).getPlayerItem(itemManager));
       player.sendActionBar(MiniMessage.miniMessage().deserialize("<green>You took some clothes (" + amount + " left)</green>"));
       player.playSound(Sound.sound().type(SoundEvent.ENTITY_ITEM_PICKUP).pitch(0.75F).source(Sound.Source.NEUTRAL).build());
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

    private void updateLabel() {
        label.editEntityMeta(TextDisplayMeta.class, meta -> {
            if (goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES)) {
                int amount = hitbox.getTag(CAPACITY);
                meta.setText(Component.text("🧺 Clothes: " + amount));
            } else {
                meta.setText(Component.text(""));
            }
        });
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:laundry:basket");
    }
}
