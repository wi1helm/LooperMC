package nub.wi1helm.listeners;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemStack;
import nub.wi1helm.ServerManager;
import nub.wi1helm.Utils;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.tasks.TaskManager;
import nub.wi1helm.tasks.mail.MailManager;
import nub.wi1helm.tasks.mail.Mailbox;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class NPCListener {

    private final EventNode<@NotNull Event> node = EventNode.all("npc");

    private final ServerManager manager = ServerManager.getManager();

    public NPCListener() {

        node.addListener(PlayerEntityInteractEvent.class, event -> {
            mailman(event);
        });
    }

    private void mailman(PlayerEntityInteractEvent event) {
        if (!event.getTarget().hasTag(MailManager.MAILMAN_TAG())) return;
        if (event.getHand() != PlayerHand.MAIN) return;
        if (manager.goalManager().isGoalCompleted(ServerGoals.DELIVER_MAIL)) {
            event.getPlayer().sendMessage(Component.text("Sorry no more mail for today. See you some other time:)"));
            return;
        }

        long letterCount = Arrays.stream(event.getPlayer().getInventory().getItemStacks())
                .filter(item -> item.hasTag(Mailbox.LETTER_TAG()))
                .count();

        if (letterCount >= 3) {
            event.getPlayer().sendMessage("You already got some letters, man!");
            return; // Stop processing further
        }



        event.getPlayer().getInventory().addItemStack(Utils.randomKey(TaskManager.getInstance().getMailManager().getMailboxes()).letterItem());
    }

    public EventNode<@NotNull Event> getNode() {
        return node;
    }
}
