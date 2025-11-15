package nub.wi1helm.goals;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.Notification;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import nub.wi1helm.Utils;
import nub.wi1helm.instances.LoopInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GoalManager {

    private static GoalManager instance;

    private final List<ServerGoal> activeGoals = new ArrayList<>();

    // The recommended goal boss bar
    private BossBar recommendedBar;

    private GoalManager() {
        this.activeGoals.addAll(generateRandomGoals());
        this.activeGoals.add(ServerGoals.TOWN_CHORES.goal());
        updateRecommendedBar(); // Initialize the boss bar on creation
    }

    public static GoalManager get() {
        if (instance == null) {
            instance = new GoalManager();
        }
        return instance;
    }

    public List<ServerGoal> getActiveGoals() {
        return activeGoals;
    }

    public boolean hasActiveGoal(ServerGoals type) {
        return activeGoals.stream().anyMatch(goal -> goal.getType() == type);
    }

    public void incrementGoal(ServerGoals type, Integer amount) {
        for (ServerGoal goal : activeGoals) {
            if (goal.getType() == type) {

                // Only increment if not completed
                if (goal.isComplete()) return;

                boolean completed = goal.increment(amount);
                if (completed) sendCompletionNotification(goal);

                // Update boss bar whenever progress changes
                updateRecommendedBar();
                break;
            }
        }
    }

    private List<ServerGoal> generateRandomGoals() {
        List<ServerGoal> allGoals = Arrays.stream(ServerGoals.values())
                .map(ServerGoals::goal)
                .toList();

        return Utils.randomSubList(allGoals, 5);
    }

    private void sendCompletionNotification(ServerGoal goal) {
        Component title = Component.empty().appendNewline().append(goal.getName().color(NamedTextColor.GREEN));
        ItemStack icon = ItemStack.of(Material.BAKED_POTATO);
        Notification notification = new Notification(title, FrameType.GOAL, icon);

        for (Player player : LoopInstance.get().getPlayers()) {
            player.sendNotification(notification);
        }
    }

    /**
     * Returns the goal with the least progress
     */
    private ServerGoal getRecommendedGoal() {
        return activeGoals.stream()
                .filter(goal -> !goal.isComplete())
                .min(Comparator.comparingDouble(ServerGoal::getProgressRatio)) // least progress
                .orElse(null);
    }

    /**
     * Updates or creates the recommended goal boss bar
     */
    public void updateRecommendedBar() {
        ServerGoal recommended = getRecommendedGoal();
        var players = LoopInstance.get().getPlayers();

        if (recommended == null) {
            // No more goals left, hide bar
            if (recommendedBar != null) {
                players.forEach(player -> player.hideBossBar(recommendedBar));
                recommendedBar = null;
            }
            return;
        }

        Component name = Component.text("Recommended Goal: ").append(recommended.getName()).color(NamedTextColor.YELLOW);
        float progress = recommended.getProgressRatio().floatValue();
        BossBar.Color color = BossBar.Color.GREEN;
        BossBar.Overlay overlay = BossBar.Overlay.NOTCHED_10;

        if (recommendedBar == null) {
            // Create new boss bar and show to all players
            recommendedBar = BossBar.bossBar(name, progress, color, overlay);
            players.forEach(player -> player.showBossBar(recommendedBar));
        } else {
            // Update existing boss bar and automatically updates all viewers
            recommendedBar.name(name);
            recommendedBar.progress(progress);
        }
    }

    /**
     * Call this when a new player joins to show the current boss bar
     */
    public void showRecommendedBarTo(Player player) {
        if (recommendedBar != null) {
            player.showBossBar(recommendedBar);
        }
    }

    // reurns if goal is completed, if not active goal will still return false
    public boolean isGoalCompleted(ServerGoals type) {
        for (ServerGoal goal : activeGoals) {
            if (goal.getType() == type) {

                // Only increment if not completed
                if (goal.isComplete()) return true;
            }
        }
        return false;
    }
}
