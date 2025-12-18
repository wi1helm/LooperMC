package nub.wi1helm.module.modules.statuepolish;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.module.modules.statuepolish.entity.HeroStatue;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class StatuePolishModule implements GameModule {

    private final EntityManager entityManager;
    private final WorldManager worldManager;
    private final GoalManager goalManager;

    private final HeroStatue hero;

    public StatuePolishModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.worldManager = worldManager;
        this.goalManager = goalManager;


        this.hero = new HeroStatue(goalManager, ServerGoals.STATUE_POLISH.target());
        this.hero.spawn(entityManager, worldManager.getTownWorld(), new Pos(4.5,-47,38.5));
        this.hero.setupInteractions();
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:statuepolish");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
