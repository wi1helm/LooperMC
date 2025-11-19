package nub.wi1helm.module.modules.agents;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.module.GameModule;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

public class AgentModule implements GameModule {

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;

    private final AgentNPC agent1;
    private final AgentNPC agent2;

    public AgentModule(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;

        this.agent1 = new AgentNPC(goalManager, MiniMessage.miniMessage().deserialize("<light_purple>[T.R.Ö.G.A]</light_purple><white> Agent</white>"), MiniMessage.miniMessage().deserialize("<gold><bold>SERVER GOALS!</bold></gold>"));
        this.agent1.spawn(entityManager, worldManager.getTownWorld(), new Pos(-11.5,-47,35,-50,-2));

        this.agent2 = new AgentNPC(goalManager, MiniMessage.miniMessage().deserialize("<light_purple>[T.R.Ö.G.A]</light_purple><white> Agent</white>"));
        this.agent2.spawn(entityManager, worldManager.getTownWorld(), new Pos(20.5,-47,-3.5,-22,13));
    }

    @Override
    public @NotNull Tag<?> getModuleTag() {
        return Tag.String("module:goalagents");
    }

    @Override
    public void registerListeners(EventNode<@NotNull Event> node) {

    }
}
