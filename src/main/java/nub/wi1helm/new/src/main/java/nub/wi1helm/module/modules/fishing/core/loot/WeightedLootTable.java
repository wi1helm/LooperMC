package nub.wi1helm.module.modules.fishing.core.loot;

import net.minestom.server.item.ItemStack;
import nub.wi1helm.item.GameItem;
import nub.wi1helm.item.ItemManager;

import java.util.ArrayList;
import java.util.List;

public class WeightedLootTable implements LootTable {

    public WeightedLootTable(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    private record Loot(GameItem item, double weight) {}

    private final ItemManager itemManager;
    private final List<Loot> loot = new ArrayList<>();
    private double totalWeight;



    public WeightedLootTable add(GameItem item, double weight) {
        loot.add(new Loot(item, weight));
        totalWeight += weight;
        return this;
    }

    @Override
    public ItemStack roll() {
        double r = Math.random() * totalWeight;
        for (Loot l : loot) {
            if ((r -= l.weight) <= 0) return l.item.getPlayerItem(itemManager);
        }
        return loot.getLast().item.getPlayerItem(itemManager);
    }
}
