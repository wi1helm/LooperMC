package nub.wi1helm.content.mail;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.tag.Tag;
import nub.wi1helm.content.Content;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.instances.LoopInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MailManager implements Content {

    private final Map<Mailbox, String> mailboxes = new HashMap<>();
    private final PostmasterNPC npc;

    private final GoalManager goalManager;
    private final LoopInstance instance;

    public MailManager(@NotNull LoopInstance instance, @NotNull GoalManager goalManager) {
        this.instance = instance;
        this.goalManager = goalManager;

        setupMailboxes();
        this.npc = new PostmasterNPC(instance, mailboxes);

        this.npc.spawn();
        spawnMailBoxes();
    }

    /**
     * Creates and registers all mailboxes in the world.
     */
    private void setupMailboxes() {
        mailboxes.put(new Mailbox(new Pos(-8.5, -46.5, 16.5), instance, Component.text("Tailor's Workshop")), "tailor");
        mailboxes.put(new Mailbox(new Pos(16.5, -48, -13.5, -125, 0), instance, Component.text("Townhall")), "townhall");
        mailboxes.put(new Mailbox(new Pos(14.5, -53, 9.5, -90, 0), instance, Component.text("Dungeon")), "dungeon");
        mailboxes.put(new Mailbox(new Pos(9.5, -51, -24.3, 90, 0), instance, Component.text("Blazing Pub")), "pub");
        mailboxes.put(new Mailbox(new Pos(9.5, -51, -22.8, 90, 0), instance, Component.text("Kondis Bakery")), "bakery");
        mailboxes.put(new Mailbox(new Pos(-18.8, -48, -2.5, 90, 0), instance, Component.text("Stom's Blacksmith")), "blacksmith");
    }

    /**
     * Spawns all mailboxes in the world.
     */
    public void spawnMailBoxes() {
        mailboxes.keySet().forEach(Mailbox::spawn);
    }

    public Map<Mailbox, String> getMailboxes() {
        return mailboxes;
    }

    public PostmasterNPC getNPC() {
        return npc;
    }

    @Override
    public Tag<?> getManagerTag() {
        return Tag.String("DELIVER_MAIL_CONTENT");
    }
}
