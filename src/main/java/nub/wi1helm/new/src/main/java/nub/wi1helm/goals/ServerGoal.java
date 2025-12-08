package nub.wi1helm.goals;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ServerGoal {
    private final Component name;
    private final Component description;
    private final Integer target;
    private Integer progress;
    private final ServerGoals type;

    public ServerGoal(Component name, Component description, int target, ServerGoals type) {
        this.name = name;
        this.description = description;
        this.target = target;
        this.progress = 0;
        this.type = type;
    }

    public boolean increment(int amount) {
        this.progress = Math.min(this.progress + amount, target);
        return isComplete();
    }

    public boolean isComplete() {
        return progress >= target;
    }

    public Component getName() {
        return name.color(NamedTextColor.WHITE);
    }

    public Component getProgressComponent() {

        if (isComplete()) {
            return Component.text("✔",NamedTextColor.GREEN);
        }

        NamedTextColor color = isComplete() ? NamedTextColor.GREEN : NamedTextColor.AQUA;
        return Component.text(progress + "/" + target, color);
    }

    public Component getDescription() {
        return description.color(NamedTextColor.GRAY);
    }

    public ServerGoals getType() {
        return type;
    }

    public Double getProgressRatio() {
        return progress.doubleValue() / target.doubleValue();
    }

    public Integer getTarget() {
        return target;
    }
    public Integer getProgress() {
        return progress;
    }
}

