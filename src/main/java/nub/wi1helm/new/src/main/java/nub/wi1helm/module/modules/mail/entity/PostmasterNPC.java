package nub.wi1helm.module.modules.mail.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mail.interaction.PostmasterNPCInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostmasterNPC extends NPC {


    private final PostmasterNPCInteraction interaction;

    public PostmasterNPC(List<Mailbox> mailboxes, GoalManager goalManager, ItemManager itemManager) {
        super(new PlayerSkin(
                        "ewogICJ0aW1lc3RhbXAiIDogMTcxNjU3MTg1MTA4MCwKICAicHJvZmlsZUlkIiA6ICIyNDY1ODI2NWVjMjg0NTY4YTg3MDJkOTVlYzdlYTc4MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmdvc1oxMiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81NDBjZjg1MjAyMGY0NmYxNWI4ZWJmODc2Y2ZmYTRiMjEwYjNlOWJmMGE2YzJlN2IyYWU2NzkwOTMzMmM1YTM3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                        "wO5wJ3LziLvy8ZmTrOXx5T5WOA6FvUmCop3Nrz3hB1re6qVFhv9NjPb7f3X+RxgJQYFmsmhDq0uy3Ne1EpC+0/Nx4WolBXZbShebFzsnhDxK8jEn9LrABJ2Spkt82oXC6r5HWnFT59/0Q1OVGSlDDzxJPXtDJQaVrXtGv7+pu1YbzSY2NobCeihwNQuU2+nnb2EkPF1nznoKo+5OpkP7u57Gvww5PJdkxMuihCHXwKO2a6snZw5FA5gEqnf+mDk4K616cWpq5P3ziWmC275mp5uIqKtM1T1jWztV2zCjCuzjOiNFjsSMfg6Su9K8BqPonHHL5prW2w8AeZgfKHeqSEMJFBebMQ0TuWtKd88b6b2lKGU1Hrmdc2dEwnjc01zpSotOjzYmdsegxZW7OCL90wMZsk7u9ZsD3XZvbWlRGSaDkdm1ahI0qE6Ow79B1n+pttQgJZpVwnohajBbqQF33bGjil6BrGjnI7C48r0dhW6HalV+oDUuYHUQmkrpdB6WF9ejsPObuiGLaNyQ4sSQGq6UpT4KIcppSuHN4hzAlpxmsqxTn/Wpk94rO/pD3r8stB/etX6tfYMc63qVLpsqDjo4bCetug+PWWmJ0NLwV5E7CeGjCQgEwvoKAh49po2Wbho/8N4QNmdFz5tOIxprLgrnCWVV4CqIagMiyGoU="
                ),
                Component.text("Postmaster Matt")
        );

        this.interaction = new PostmasterNPCInteraction(this, mailboxes, goalManager, itemManager);
    }

    @Override
    public void setupInteractions() {
        // Delegate to interaction component
        interaction.setup();
    }

    @Override
    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        super.spawn(entityManager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 5F);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        // empty, or could default to some position
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:mail:npc:postmaster");
    }
}
