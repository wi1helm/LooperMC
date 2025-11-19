package nub.wi1helm.item;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import nub.wi1helm.core.GameService;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.module.modules.fishfountain.FishingRodItem;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemManager implements GameService {

    private static final Logger logger = LoggerFactory.getLogger(ItemManager.class);

    private final EventNode<@NotNull Event> node = EventNode.all("item");

    private final Map<Tag<@NotNull Boolean>, GameItem> registry = new ConcurrentHashMap<>();

    public ItemManager() {
        onItemUse();
        onSlotHeldChange();

        logger.info("ItemManager initialized.");
    }

    public void registerItem(GameItem item) {
        registry.put(item.getItemTag(), item);
        logger.info("Registered: {}", item.getItemTag().getKey());
    }

    public void onItemUse() {
        node.addListener(PlayerUseItemEvent.class, event -> {
            ItemStack item = event.getItemStack();

            registry.forEach((tag, handler) -> {
                Boolean hasTag = item.getTag(tag);
                if (hasTag != null && hasTag) {
                    handler.onPlayerUse(event);
                }
            });
        });
    }

    public void onSlotHeldChange() {
        node.addListener(PlayerChangeHeldSlotEvent.class, event -> {
            registry.forEach((booleanTag, gameItem) -> gameItem.onPlayerChangeHeldSlot(event));
        });

    }

    @Override
    public void registerListeners() {
        MinecraftServer.getGlobalEventHandler().addChild(node);

    }
}
