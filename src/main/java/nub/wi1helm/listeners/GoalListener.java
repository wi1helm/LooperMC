package nub.wi1helm.listeners;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import nub.wi1helm.ServerManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.instances.LoopInstance;
import nub.wi1helm.tasks.mail.Mailbox;
import org.jetbrains.annotations.NotNull;

public class GoalListener {

    private final EventNode<@NotNull Event> node = EventNode.all("goal");

    private final ServerManager serverManager = ServerManager.getManager();
    private final GoalManager goalManager = serverManager.goalManager();

    private final LoopInstance instance = serverManager.getInstance();

    public GoalListener() {
        voidJumpers();
        deliverMail();
    }

    public void voidJumpers() {
        node.addListener(PlayerMoveEvent.class, event -> {
           final Player player = event.getPlayer();

           if (player.getPosition().y() > instance.getPlayerBottom()) return;

           player.teleport(instance.getSpawn());

           goalManager.incrementGoal(ServerGoals.VOID_JUMPERS, 1);

        });
    }

    public void deliverMail() {
        node.addListener(PlayerEntityInteractEvent.class, event -> {
            if (event.getHand() != PlayerHand.MAIN) return;

            ItemStack item = event.getEntity().getItemInMainHand();

            if (item.isAir()) return;

            Entity entity = event.getTarget();

            if (!item.getTag(Mailbox.ADDRESS_TAG()).equals(entity.getUuid())) {
                return;
            }

            goalManager.incrementGoal(ServerGoals.DELIVER_MAIL, item.amount());

            event.getPlayer().getInventory().setItemStack(event.getPlayer().getHeldSlot(), ItemStack.AIR);
        });
    }

    public EventNode<@NotNull Event> getNode() {
        return node;
    }
}
