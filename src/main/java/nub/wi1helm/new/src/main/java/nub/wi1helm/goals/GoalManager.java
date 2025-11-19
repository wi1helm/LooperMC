package nub.wi1helm.goals;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.Notification;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import nub.wi1helm.Utils;

import java.util.*;

public class GoalManager {
    // Active goals stored in a map for O(1) lookup
    private final Map<ServerGoals, ServerGoal> activeGoals = new HashMap<>();
    // Recommended goal boss bar
    private BossBar recommendedBar;

    public GoalManager() {
        generateRandomGoals();
        updateRecommendedBar();
    }

    // -----------------------------------------------------
    // Active goal retrieval
    // -----------------------------------------------------

    public Collection<ServerGoal> getActiveGoals() {
        return activeGoals.values();
    }

    public ServerGoal getGoal(ServerGoals type) {
        return activeGoals.get(type);
    }

    public boolean hasActiveGoal(ServerGoals type) {
        return activeGoals.containsKey(type);
    }

    public boolean isGoalCompleted(ServerGoals type) {
        ServerGoal goal = activeGoals.get(type);
        return goal != null && goal.isComplete();
    }

    // -----------------------------------------------------
    // Increment logic
    // -----------------------------------------------------

    public void incrementGoal(ServerGoals type, int amount) {

        ServerGoal goal = activeGoals.get(type);
        if (goal == null || goal.isComplete())
            return;

        boolean completed = goal.increment(amount);

        if (completed) {
            sendCompletionNotification(goal);

            if (areAllGoalsCompleted()) {
                onAllGoalsCompleted();
            }
        }

        updateRecommendedBar();
    }

    // -----------------------------------------------------
    // Goal initialization
    // -----------------------------------------------------

    private void generateRandomGoals() {
        List<ServerGoal> allGoals = Arrays.stream(ServerGoals.values())
                .map(ServerGoals::goal)
                .toList();

        List<ServerGoal> selected = Utils.randomSubList(allGoals, 5);

        for (ServerGoal goal : selected) {
            activeGoals.put(goal.getType(), goal);
        }
    }

    // -----------------------------------------------------
    // Notifications
    // -----------------------------------------------------

    private void sendCompletionNotification(ServerGoal goal) {
        Component title = Component.empty()
                .appendNewline()
                .append(goal.getName().color(NamedTextColor.GREEN));

        Notification notification = new Notification(
                title,
                FrameType.GOAL,
                ItemStack.of(Material.BAKED_POTATO)
        );

        MinecraftServer.getConnectionManager()
                .getOnlinePlayers()
                .forEach(player -> player.sendNotification(notification));
    }

    // -----------------------------------------------------
    // Recommended Goal BossBar
    // -----------------------------------------------------

    private ServerGoal getRecommendedGoal() {
        return activeGoals.values().stream()
                .filter(goal -> !goal.isComplete())
                .min(Comparator.comparingDouble(ServerGoal::getProgressRatio))
                .orElse(null);
    }

    public void updateRecommendedBar() {
        ServerGoal recommended = getRecommendedGoal();
        var players = MinecraftServer.getConnectionManager().getOnlinePlayers();

        if (recommended == null) {
            // No goals left -> remove bar
            if (recommendedBar != null) {
                players.forEach(p -> p.hideBossBar(recommendedBar));
                recommendedBar = null;
            }
            return;
        }

        Component name = Component.text("Recommended Goal: ")
                .append(recommended.getName())
                .color(NamedTextColor.YELLOW);

        float progress = recommended.getProgressRatio().floatValue();

        if (recommendedBar == null) {
            recommendedBar = BossBar.bossBar(
                    name,
                    progress,
                    BossBar.Color.GREEN,
                    BossBar.Overlay.NOTCHED_10
            );
            players.forEach(p -> p.showBossBar(recommendedBar));
        } else {
            recommendedBar.name(name);
            recommendedBar.progress(progress);
        }
    }

    public void showRecommendedBarTo(Player player) {
        if (recommendedBar != null) {
            player.showBossBar(recommendedBar);
        }
    }

    // -----------------------------------------------------
    // All goals completed
    // -----------------------------------------------------

    public boolean areAllGoalsCompleted() {
        return activeGoals.values().stream().allMatch(ServerGoal::isComplete);
    }

    // Override this later with portal logic
    private void onAllGoalsCompleted() {
        System.out.println("All goals completed! Triggering end-of-cycle logic...");
        // TODO: open your portal, teleport players, etc.
    }
}
