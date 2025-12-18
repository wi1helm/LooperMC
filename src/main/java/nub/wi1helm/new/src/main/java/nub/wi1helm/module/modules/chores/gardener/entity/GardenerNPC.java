package nub.wi1helm.module.modules.chores.gardener.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class GardenerNPC extends NPC {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final ItemManager itemManager;

    private final static PlayerSkin skin = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTcyMzMzMjk1NzgyNSwKICAicHJvZmlsZUlkIiA6ICJkNDI0Njg2YWNmNGU0NDA2YWQwNmNlNmE4ODhlYThkNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYXRDYWtlODk1OSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NTBiMjg3NjRjYTlhMDFjYzkxODA1ZWZmNzQxNzQ0MTVhOWMyMDk5MTQ5YzY5YTRkODI2NjVlZjc0NDJhMTA5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "cIPl3QleQtXK27c+gCsZhLGY4kakDy6P8gbbo5j+aS8XISKvw7FaTvXSUraVqph7I/BX5FtxSlhxRbSbQpfL7DTMhnfxHpa3jDsILlLId4T6vEbds/Q+KCn5YSwneEZNU8b095tGWg2ndBbs40ylYA2SI74FxG+Vr6D5IMY5MOAiMKNfZIFqAGfC2xtY0TaPEk3POmYYc5HN3H9Gnqf4wTsEgIC6y/xpaazdw9aua3j79uRV5gufp+YgrAfBdm3+b3VzE2jh4ttd1U/RM0BGgzEEiVK7H6S44ojtvjiw9SyDuw3oQSWHUIG897inTj9H4iE5oOCfJI19JoBM3Tmztsf8Qjn6dhYkPNiPAIPMu9jFYeEgcj5ky1t1aIiJ1JzNYF1+EyW5FNLaYc0Wy/wfedkrO6WlmnRaEduOhmuT8mu6m9QjR8uxgXaicGGqiZYjNCy3i6KYM6cTa5KIef10emAzhX0ISJQ3aExXH0qh6cv03kLlqAtHAAUPQlLwChbLNwGxYDSIsm/W9Y+PsFpWsJtVoJD/p/FMOKY9+7+YJ5KB8CyRrSkNpwmfyssh5+kttBryPOvIxSCXF5pn60Z0AHrrhuf+eqNEwLZjguQlH4NHIipZeYYkzIu0AIJyHQnN8Id6cnJL4N1N7L6XxMQ1/+EUbtEyv4VruL0JVcj1sQI="
    );

    public GardenerNPC(WorldManager worldManager, GoalManager goalManager, ItemManager itemManager) {
        super(skin, Component.text("Gardener").color(NamedTextColor.GREEN));
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.itemManager = itemManager;

        getMannequin().setItemInMainHand(ItemStack.builder(Material.SHEARS).build());
    }

    @Override
    public void setupInteractions() {

    }

    @Override
    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        super.spawn(entityManager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 5F);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {

    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:gardener:entity:gardenernpc");
    }
}
