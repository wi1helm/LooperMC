package nub.wi1helm.eventnodes;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.validate.Check;
import nub.wi1helm.eventnodes.listeners.InteractEntityEvent;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.instances.LoopInstance;
import nub.wi1helm.content.mail.Mailbox;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GoalEventNode {

    private final EventNode<@NotNull Event> node = EventNode.all("goal");

    private static GoalEventNode instance;

    private final GoalManager manager;
    private GoalEventNode(GoalManager manager) {
        this.manager = manager;

        voidJumpers();
        deliverMail();


        MinecraftServer.getGlobalEventHandler().addChild(node);
    }

    public static GoalEventNode init(GoalManager manager) {
        if (instance==null) instance = new GoalEventNode(manager);
        return instance;
    }
    public static GoalEventNode get() {
        Check.stateCondition(instance == null, "GoalEventNode needs to be initiated before get-ted!");
        return instance;
    }

    public void voidJumpers() {
        node.addListener(PlayerMoveEvent.class, event -> {
           final Player player = event.getPlayer();

           if (player.getPosition().y() > LoopInstance.get().getPlayerBottom()) return;

           player.teleport(LoopInstance.get().getSpawn());

           this.manager.incrementGoal(ServerGoals.VOID_JUMPERS, 1);

        });
    }

    public void deliverMail() {
        node.addListener(InteractEntityEvent.class, event -> {
            if (event.getHand() != PlayerHand.MAIN) return;

            ItemStack item = event.getPlayer().getItemInMainHand();

            if (item.isAir()) return;

            Entity entity = event.getTarget();

            Tag<@NotNull UUID> tag = Mailbox.ADDRESS_TAG();
            if (item.getTag(tag) == null) return;
            if (!item.getTag(Mailbox.ADDRESS_TAG()).equals(entity.getUuid())) {
                return;
            }

            this.manager.incrementGoal(ServerGoals.DELIVER_MAIL, item.amount());

            event.getPlayer().getInventory().setItemStack(event.getPlayer().getHeldSlot(), ItemStack.AIR);
        });
    }

    public void hangClothes() {
        this.manager.incrementGoal(ServerGoals.TOWN_CHORES, 1);
    }

    public EventNode<@NotNull Event> getNode() {
        return node;
    }
}
