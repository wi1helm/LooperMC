package nub.wi1helm.module.modules.mail;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.mail.entity.Mailbox;
import nub.wi1helm.module.modules.mail.entity.PostmasterNPC;
import nub.wi1helm.world.TownWorld;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MailModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    private final PostmasterNPC npc;
    private final List<Mailbox> mailboxes = new ArrayList<>();

    public MailModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;

        TownWorld instance = worldManager.getTownWorld();

        this.npc = new PostmasterNPC(mailboxes, goalManager, itemManager);
        this.npc.spawn(entityManager, instance, new Pos(9.5, -51, -30.5, -25, 4));
        this.npc.setupInteractions();

        setupMailboxes();
        spawnMailBoxes();
        setupMailBoxesInteraction();
    }

    /**
     * Creates and registers all mailboxes in the world.
     */
    private void setupMailboxes() {
        mailboxes.add(new Mailbox(goalManager, itemManager,  new Pos(-8.5, -46.5, 16.5), "Tailor's Workshop"));
        mailboxes.add(new Mailbox(goalManager, itemManager,  new Pos(16.5, -48, -13.5, -125, 0) ,"Townhall"));
        mailboxes.add(new Mailbox(goalManager, itemManager,  new Pos(14.5, -53, 9.5, -90, 0) ,"Dungeon"));
        mailboxes.add(new Mailbox(goalManager, itemManager,  new Pos(9.5, -51, -24.3, 90, 0) ,"Blazing Pub"));
        mailboxes.add(new Mailbox(goalManager, itemManager,  new Pos(9.5, -51, -22.8, 90, 0) , "Kondis Bakery"));
        mailboxes.add(new Mailbox(goalManager, itemManager,  new Pos(-18.8, -48, -2.5, 90, 0),"Stom's Blacksmith"));
        mailboxes.add(new Mailbox(goalManager, itemManager, new Pos(-2.5, -47, 45.7, 180, 0), "Portal"));
    }

    /**
     * Spawns all mailboxes in the world.
     */
    public void spawnMailBoxes() {
        mailboxes.forEach(mailbox -> mailbox.spawn(entityManager, worldManager.getTownWorld()));
    }

    /**
     * setups the interaction graph for the entities.
     */
    public void setupMailBoxesInteraction() {
        mailboxes.forEach(Mailbox::setupInteractions);
    }


    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:mail");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }

}
