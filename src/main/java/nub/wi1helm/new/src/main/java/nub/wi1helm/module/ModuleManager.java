package nub.wi1helm.module;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import nub.wi1helm.core.GameService;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.chores.laundry.LaundryModule;
import nub.wi1helm.module.modules.fountain.FountainModule;
import nub.wi1helm.module.modules.fishing.FishingModule;
import nub.wi1helm.module.modules.hideandseek.HideAndSeekModule;
import nub.wi1helm.module.modules.mail.MailModule;
import nub.wi1helm.module.modules.agents.AgentModule;
import nub.wi1helm.module.modules.mayor.MayorModule;
import nub.wi1helm.module.modules.chores.pondside.PondSideModule;
import nub.wi1helm.module.modules.voidjumpers.VoidJumperModule;
import nub.wi1helm.module.modules.wizard.WizardModule;
import nub.wi1helm.world.WorldManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager implements GameService {

    private final EventNode<@NotNull Event> node = EventNode.all("module");

    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;

    private final Map<GameModule, Tag<?>> modules = new HashMap<>();

    public ModuleManager(WorldManager worldManager, GoalManager goalManager, EntityManager entityManager, ItemManager itemManager) {
        this.worldManager = worldManager;
        this.goalManager = goalManager;
        this.entityManager = entityManager;
        this.itemManager = itemManager;

        MailModule mailModule = register(new MailModule(worldManager, goalManager, entityManager, itemManager));
        VoidJumperModule voidJumperModule = register(new VoidJumperModule(worldManager, goalManager));
        AgentModule agentModule = register(new AgentModule(worldManager, goalManager, entityManager));
        LaundryModule laundryModule = register(new LaundryModule(worldManager, goalManager, entityManager, itemManager));
        FishingModule fishingModule = register(new FishingModule(worldManager, entityManager, itemManager));
        FountainModule fountainModule = register(new FountainModule(fishingModule, worldManager, goalManager, entityManager, itemManager));
        HideAndSeekModule hideAndSeekModule = register(new HideAndSeekModule(worldManager, goalManager, entityManager));
        WizardModule wizardModule = register(new WizardModule(worldManager, goalManager, entityManager, itemManager));

        PondSideModule pondSideModule = register(new PondSideModule(worldManager, goalManager, entityManager, itemManager, fishingModule));
        MayorModule mayorModule = register(new MayorModule(worldManager, goalManager, entityManager, itemManager, fishingModule));
    }

    /**
     * Registers a module in the system and returns it for DI
     */
    private <T extends GameModule> T register(T module) {
        modules.put(module, module.getModuleTag());
        return module;
    }

    @Override
    public void registerListeners() {
        modules.forEach((module, tag) -> {
            module.registerListeners(node);
        });
        MinecraftServer.getGlobalEventHandler().addChild(node);
    }
}
