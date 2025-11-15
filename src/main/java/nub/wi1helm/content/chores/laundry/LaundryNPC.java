package nub.wi1helm.content.chores.laundry;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.npc.NPC;
import nub.wi1helm.npc.NPCInteractedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LaundryNPC extends NPC {

    public static Tag<@NotNull UUID> ALEX_TAG() {
        return Tag.UUID("alex");
    }
    // Alex's skin data (using the data provided previously)
    private static final PlayerSkin ALEX_SKIN = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc2MTY2NTI2NTIzOSwKICAicHJvZmlsZUlkIiA6ICIyYjcyZWYyYWUzMmQ0Zjc1OGEyMThlMDI4MTViYmNjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ2b2xrb2RhZl82MyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mM2U4MGQxNGY5ZGVhZjA2ODY5ODE0ZWI4MGI5NWNlYWJkOWI5MjY4NWFlYzMzYmJjMTcyMzhhYzg2Mjk3ZTkxIgogICAgfQogIH0KfQ==",
            "dhfN7QCPkRXVSNdIo7cOvUzuGpHXZPR8gl6jNWLNoiWdLrV+scXNdcHn4lPDHyYaLLocw2595fifHbnSNfanC3Q1R6+J9NRNoohkVuUWlXOS9pytluFfOGr/Zwm2QiSvriXX1133GOA7QOKU5XEthDUGDIQqzdlU5y5VyruyyAv0MZ/W0DtkNI7HnI1uyZ8wwEzlpKMYdCg/RpXjh046lBufcyumog3HQbO8MSDWwk2lSjHSkCsJalzNiE6pWRq2gvfX6omYLRbc5MxrU00MeyEVtMVwj3cife1jz6KsY1jASKDXMciPZeCsnmpFh+SU3xsnRrwBmH1KhYz3De5pab8WgCoXe6j4vYdMyKb1rla0vpOkzAYuoMXKkw3QtJoMtx+SVhkU9ZDhqLLRj/J9uTVKruqWjmX7NcuN/o8tucnmpFh+SU3xsnRrwBmH1KhYz3De5pab8WgCoXe6j4vYdMyKb1rla0vpOkzAYuoMXKkw3QtJoMtx+SVhkU9ZDhqLLRj/J9uTVKruqWjmX7NcuN/o8tucnTruqWjmX7NcuN/o8tucnROqGjGXUh8ts39Yhv/2n+ejv6xx3WTLhsiaaUsPDZayMBpWPiX0l0q2ZKVn9Ue3VXlLrFhB6bJMk5NmETuKBdjIDoYXQn9fdl1yykMvlo3N/zLtAsuckdVPUf5pFLjvLf2GJprZXZTASYkq+0NfveQ7xuuZSuQqfT+k2SSdRDeFye6Xb5CKYpXobzX156f2HFAsHO7/6Zsk2zJ7SH5Upo0YZecA2eSocRi9ViCFeqEb5TE4QmrSY0="
    );

    public LaundryNPC(@NotNull Instance instance) {
        super(Component.text("Alex"), ALEX_SKIN, instance, new Pos(-24.5, -42.5, 3.5, -120, -16));

        setTag(ALEX_TAG(), getUuid());

        this.eventNode().addListener(NPCInteractedEvent.class, this::handlePlayerInteract);
    }

    private void handlePlayerInteract(NPCInteractedEvent event) {
        if (event.getHand() != PlayerHand.MAIN) return;

        if (!GoalManager.get().hasActiveGoal(ServerGoals.TOWN_CHORES)) return;

        if (GoalManager.get().isGoalCompleted(ServerGoals.TOWN_CHORES)) {
            event.getPlayer().sendMessage(Component.text("Thanks again for helping with the laundry!"));
        }
        else {
            event.getPlayer().sendMessage(Component.text("Could you help me dry my clothes?"));
        }
    }
}