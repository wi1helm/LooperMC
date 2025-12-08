package nub.wi1helm.module.modules.fishing.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.fishing.core.FishingManager;
import nub.wi1helm.module.modules.fishing.interaction.FishermanNPCInteraction;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the Fisherman NPC, who provides a shop to players.
 * The interaction logic is handled by {@link FishermanNPCInteraction}.
 */
public class FishermanNPC extends NPC {

    private static final PlayerSkin skin = new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTY2MDU1NTI1NzE1MSwKICAicHJvZmlsZUlkIiA6ICI2NmI0ZDRlMTFlNmE0YjhjYTFkN2Q5YzliZTBhNjQ5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmFzdG9vWXNmIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQzYjhjZTJkNTRmNDNhNThiYjY0ZjViMGMzODY4MjMyMGM0OTAzNjA4NWE2ZTc3M2I4OTdhYmUzZjhiM2QyNmIiCiAgICB9CiAgfQp9",
            "t3Bw/OYPC33/SJSJh9vDqFttSpI+1w/QPq26cdLU8RsfeFIPvHi91ft4uhci6CTnIOJCCxYHtBXezlWQ76hD/IUon9m/NeyT48MNAOhXLGspct1Do3msa3dl35hCLaciKGII3H7Eat32rcLATe9dNs/ltt54ShNV1EtgV9eBamiIhQk2qgmQv0LxSV/rBvjuPHr/j/DFwVFXtrvTGlw6Yaj8M+V0yV4acmqQ6bQ3sJv4Uk2LMlzjCN+ZZ+SF007d5N031PoaI/inXzawuT72/QadR+HvmAUqEkbkBXwXTJOMs2duHDi+opHkrCHWhkj6S74tmziwIVcHl364d1vQmc8imBMfD9upxnKDT9wBI4R8I1zyrGlj4jVXKn97pdk6UfwxLSrVuB7IsniqqVNDm4dfNv5UMdDWzQf2VrvS7MdsttodXVZfrtabkkTytN8nIOccDmoQ5mpduhMRwNyEJ4mlPbGxoPmTHPjLENDH896gsiOHScr61hDQFpUrmLdeblvO0UNFDSUgLjBo9CpnsbfC7jzYbqQdc2/0w/gBJmum7qla9dsIAVtXIc9l9mV5Umz52fvP/eq0TeU+tS0HkD+O5EPob0S1UPZKfB0RbqR0tR78/gUCm7fSL9FN0zhih9jAtzHiKYHYPCr0q+uTKgCys2x1i6qV7yz+gXGmD54=");

    private final FishermanNPCInteraction interaction;

    public FishermanNPC(ItemManager itemManager, FishingManager fishingManager) {
        super(skin, Component.text("Fisherman"));

        // Delegate interaction setup to the dedicated class
        this.interaction = new FishermanNPCInteraction(this, itemManager, fishingManager);
    }

    // Since interaction is handled externally, this is empty now.
    @Override
    public void setupInteractions() {
        // Handled by FishermanNPCInteraction
        interaction.setup();
    }

    // --- Abstract Implementations ---

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        // Implementation from old code - left empty
    }

    @Override
    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        super.spawn(entityManager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 5F);
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:fountain:npc:fisherman");
    }
}