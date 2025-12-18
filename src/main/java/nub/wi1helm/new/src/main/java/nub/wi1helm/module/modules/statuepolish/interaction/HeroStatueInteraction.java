package nub.wi1helm.module.modules.statuepolish.interaction;

import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.entity.interaction.DialogNode;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.modules.statuepolish.entity.HeroStatue;

import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class HeroStatueInteraction {

    private final HeroStatue heroStatue;
    private final GoalManager goalManager;

    public HeroStatueInteraction(HeroStatue heroStatue, GoalManager goalManager) {
        this.heroStatue = heroStatue;
        this.goalManager = goalManager;
    }

    public void setup() {
        InteractionNode root = heroStatue.getInteractionGraph().getRoot();

        Consumer<PlayerEntityInteractEvent> polishAction = event -> {
            heroStatue.polish();
            goalManager.incrementGoal(ServerGoals.STATUE_POLISH, 1);
        };

        ToIntFunction<PlayerEntityInteractEvent> condition = event -> {
            if (goalManager.hasActiveGoal(ServerGoals.STATUE_POLISH)) return 10;

            return -1;
        };

        InteractionNode polish = new InteractionNode(
            polishAction, condition, false, TaskSchedule.immediate()
        );

        root.link(polish);

    }
}
