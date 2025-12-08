package nub.wi1helm.module.modules.agents;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.dialogs.GoalDialog;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.goals.GoalManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AgentNPC extends NPC {

    private static final PlayerSkin skin = new PlayerSkin("ewogICJ0aW1lc3RhbXAiIDogMTczNDcwNjI1ODM0NiwKICAicHJvZmlsZUlkIiA6ICJlODE1MGY1MjlmZGU0YzdkYjI3OTAyZjJjNmU3NTc5ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCcmFkQm90XzIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDEzNDcxMDRmN2JlZTNiNjUzZmM0YTI0NzcxYTk3ZGQ5ZGVmYWYwY2RkOGVkZjVhYjQ1NWEzOTcwZTBhNDcwYiIKICAgIH0KICB9Cn0=", "LUhdxeZ0fKExU/Pmxib8lqCJJBz/brRVWg7tLr/krZNwTkCuPd7luPZI20Esft8KijgkPO/VsYRuP9qhfyVO1zxWvCKhbJW7LmrTyGoLY6HQ4GQ/iMpzdyprsd3jE+KPNZPzZD1eTfy9/YlTMJAjNswif/vuZGV9hk6BrRu41AmP3E5TeCgvm6ivjuABsJqCU+ZGjRFifQaVdWeuDOmSfWCtsg7R7qOa4sZvjfeGY0O7aTK2ZxnInw7F2N+rjWxdXAhT+f+QjaDZgcmjpbCKeOLdwhrKTSpzB+UgVLQPbH5RbsqIly8wyNzv8Qq1Bd37/fOraDbbJn/EYOb8BQewbuWFR6pV1kQPCqfZdYI98welH1wuRg2kt9l6324/4/qmH61zP/thy+uW2ucLyyBHLqzSWng5IaAnhBhByWBZ1eADYMNZMpGrCYXEXqNS4TQEUF2g9/6Zgj2dXpRFse+DYeP9HpN4h7Gq0xzXrNdB/rlAamqQH81OevLa3OJWqsYupOsBi8omtEIvfycI7WPKnEyxU7rFzvd22QP1rUA6wRbuzW1VBZ98b4ENnEl759bSbI2+7vplYguP5BCuU9dTk5PzpIjKtdd/zWQEj7OvUFY/D7xIYl+Cse+L8jMJLHjQCvbG+c5rR+fKqtdrCMCtoirj2gytVA9lcW9KKEIlCmE=");

    private GoalManager goalManager;

    public AgentNPC(@NotNull GoalManager goalManager, @NotNull Component... name) {
        super(skin, name);
        this.goalManager = goalManager;
        setupInteractions(); // Initialize the graph on creation
    }

    /**
     * Replaces onPlayerInteract with the InteractionGraph pattern.
     * The primary action is to show the goal dialog.
     */
    @Override
    public void setupInteractions() {
        // --- 1. Main Interaction: Show Goal Dialog (Priority 10) ---
        Consumer<PlayerEntityInteractEvent> dialogAction = event -> {
            // This is the original logic from onPlayerInteract
            event.getPlayer().showDialog(new GoalDialog().get(goalManager.getActiveGoals()));
            event.getPlayer().playSound(Sound.sound().type(SoundEvent.BLOCK_CHEST_OPEN).pitch(1.6F).build());
        };

        // Priority: Only run if the player uses the main hand (prevents off-hand misclicks)
        InteractionNode dialogNode = InteractionNode.create(
                dialogAction,
                InteractionPriority.condition(event -> event.getHand() == PlayerHand.MAIN, 10),
                false,
                null
        );

        getInteractionGraph().getRoot().connect(dialogNode);
    }

    // The previous onPlayerInteract method is removed, as its logic is now in setupInteractions.

    @Override
    public void spawn(EntityManager manager, Instance instance) {
    }

    @Override
    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        super.spawn(entityManager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 5F);
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:goalagents:npc:goalagent");
    }
}