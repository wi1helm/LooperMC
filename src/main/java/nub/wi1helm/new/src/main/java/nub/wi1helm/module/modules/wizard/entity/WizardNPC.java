package nub.wi1helm.module.modules.wizard.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.wizard.interactions.WizardInteraction;
import org.jetbrains.annotations.NotNull;

public class WizardNPC extends NPC {

    private WizardInteraction interaction;

    private static PlayerSkin skin = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc2MzM5NjMzMjE1NSwKICAicHJvZmlsZUlkIiA6ICI3ZGEyYWIzYTkzY2E0OGVlODMwNDhhZmMzYjgwZTY4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHb2xkYXBmZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2ZiNjk3ZWU0OWM5NDIzM2JmM2Q2ZTE2MjI5ZTUzMzdmNzA3NjcyYWI3NjJmN2I2NGE1ZWY0OGE0YzkyYzNhIgogICAgfQogIH0KfQ==",
            "h++JeUtSWg8nijWDAKJbi9nCQ2rv1zM1IuoSP3ARa2bOWF/cPIlX6yGuCKSsDWhty2O3qVU4l43IteYGdRz3KLI+GQ+T1jabnSBgyQ3lZZ2YARC46RaA6XK/xhg/trH62Hai0qupIbc7xN5Lk4UnGTNcJvjFkkuv/Gxe12tUfzBEVsY+1qqBnVn5YmjI6HdI0t0ojIEj2jviicoJ3cypX5oPPjg2Ma16gYzkMb5e4Q1ikAuZ+EcT7B6CVbsVCXHTnVPSdnahURQeARUqJn6Utyd05qZxSI413ENJnCtMSI8BV8RQ4ld2VKCHBsma/S7ZC9s7d/sddk3CP0FtSCCr8Ewl/a0QxgjeJGH7qstoBuMXqzSYHLNobUI8cBTlVSIMvSDC7mL+a3mBM90q9Sv3N1BbWqJC/qy+kOmttHxVd4m/tJIPKGqN410FDjd7g1fuYp9zOJtl7PlmrTUnntGKOA71nsuerUTLSf9cJQU7BwIPO8WLfOeV/QT5ger0mJTRgdMgLclZdU1f2G8SXP1Gw6S8Du5Y97QoSqYJKjw7CGUIle0ys2NDLRajksGNpeqU4RR+HV7cPjlBNaefhVeHHg1tWHGVL7m8M0FxeCSj1OT/ns5kuG1WCzgZebt7iaSS+yz43xuB5vzmuA9k7DjyBMIvmUF/0tmrq3zJKTSljUk=");

    public WizardNPC(ItemManager itemManager) {
        super(skin, Component.text("Wizard"));

        this.interaction = new WizardInteraction(this, itemManager);
    }

    @Override
    public void setupInteractions() {
        this.interaction.setup();
    }

    @Override
    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        super.spawn(entityManager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 8.0F);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {

    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:wizard:npc:wizard");
    }
}
