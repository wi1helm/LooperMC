package nub.wi1helm.tasks.mail;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.avatar.MannequinMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.player.ResolvableProfile;
import net.minestom.server.tag.Tag;
import nub.wi1helm.ServerManager;
import nub.wi1helm.goals.ServerGoals;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MailManager {
    private ServerManager manager = ServerManager.getManager();
    private Map<Mailbox, String> mailboxes = new HashMap<>();
    private Entity postmanNPC;

    public MailManager() {
        mailboxes.put(new Mailbox(new Pos(-8.5, -46.5, 16.5), manager.getInstance(), Component.text("Tailor's Workshop")), "tailor" );
        mailboxes.put(new Mailbox(new Pos(16.5,-48,-13.5, -125,0), manager.getInstance(), Component.text("Townhall")), "townhall");
        mailboxes.put(new Mailbox(new Pos(14.5,-53,9.5,-90,0), manager.getInstance(), Component.text("Dungeon")), "dungeon");
        mailboxes.put(new Mailbox(new Pos(9.5,-51,-24.3,90,0), manager.getInstance(), Component.text("Blazing Pub")), "pub");
        mailboxes.put(new Mailbox(new Pos(9.5,-51,-22.8,90,0), manager.getInstance(), Component.text("Kondis Bakery")), "bakery");

        mailboxes.put(new Mailbox(new Pos(-18.8,-48,-2.5,90,0), manager.getInstance(), Component.text("Stom's Blacksmith")), "blacksmith");

        spawnMailBoxes();

        if (ServerManager.getManager().goalManager().hasActiveGoal(ServerGoals.DELIVER_MAIL)) {
            postman();
        }
    }

    public void spawnMailBoxes() {
        mailboxes.forEach((mailbox, s) -> {mailbox.spawn();});
    }

    public void postman() {
        Entity entity = new Entity(EntityType.MANNEQUIN);
        entity.editEntityMeta(MannequinMeta.class, meta -> {
            meta.setProfile(new ResolvableProfile(new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTcxNjU3MTg1MTA4MCwKICAicHJvZmlsZUlkIiA6ICIyNDY1ODI2NWVjMjg0NTY4YTg3MDJkOTVlYzdlYTc4MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmdvc1oxMiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81NDBjZjg1MjAyMGY0NmYxNWI4ZWJmODc2Y2ZmYTRiMjEwYjNlOWJmMGE2YzJlN2IyYWU2NzkwOTMzMmM1YTM3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=","wO5wJ3LziLvy8ZmTrOXx5T5WOA6FvUmCop3Nrz3hB1re6qVFhv9NjPb7f3X+RxgJQYFmsmhDq0uy3Ne1EpC+0/Nx4WolBXZbShebFzsnhDxK8jEn9LrABJ2Spkt82oXC6r5HWnFT59/0Q1OVGSlDDzxJPXtDJQaVrXtGv7+pu1YbzSY2mS2NobCeihwNQuU2+nnb2EkPF1nznoKo+5OpkP7u57Gvww5PJdkxMuihCHXwKO2a6snZw5FA5gEqnf+mDk4K616cWpq5P3ziWmC275mp5uIqKtM1T1jWztV2zCjCuzjOiNFjsSMfg6Su9K8BqPonHHL5prW2w8AeZgfKHeqSEMJFBebMQ0TuWtKd88b6b2lKGU1Hrmdc2dEwnjc01zpSotOjzYmdsegxZW7OCL90wMZsk7u9ZsD3XZvbWlRGSaDkdm1ahI0qE6Ow79B1n+pttQgJZpVwnohajBbqQF33bGjil6BrGjnI7C48r0dhW6HalV+oDUuYHUQmkrpdB6WF9ejsPObuiGLaNyQ4sSQGq6UpT4KIcppSuHN4hzAlpxmsqxTn/Wpk94rO/pD3r8stB/etX6tfYMc63qVLpsqDjo4bCetug+PWWmJ0NLwV5E7CeGjCQgEwvoKAh49po2Wbho/8N4QNmdFz5tOIxprLgrnCWVV4CqIagMiyGoU=")));
        });
        entity.setTag(MAILMAN_TAG(), entity.getUuid());

        Entity space = new Entity(EntityType.INTERACTION);
        space.editEntityMeta(InteractionMeta.class, meta -> {
            meta.setHeight(0.3F);
        });


        Entity name = new Entity(EntityType.TEXT_DISPLAY);
        name.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(Component.text("Mailman Matt"));
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        });


        entity.setInstance(manager.getInstance(), new Pos(9.5, -51, -30.5,-25,4));
        space.setInstance(manager.getInstance());
        name.setInstance(manager.getInstance());
        entity.addPassenger(space);
        space.addPassenger(name);

        this.postmanNPC = entity;
    }

    public static Tag<@NotNull UUID> MAILMAN_TAG(){
        return Tag.UUID("mailman");
    }

    public Map<Mailbox, String> getMailboxes() {
        return mailboxes;
    }
}
