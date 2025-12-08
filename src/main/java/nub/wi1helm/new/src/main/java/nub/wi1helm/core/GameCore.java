package nub.wi1helm.core;


import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.ModuleManager;
import nub.wi1helm.player.PlayerManager;
import nub.wi1helm.player.chat.ChatManager;
import nub.wi1helm.player.sidebar.SidebarManager;
import nub.wi1helm.player.tablist.TablistManager;
import nub.wi1helm.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

/**
 * The central service locator for all major, singleton game components.
 * This class ensures that managers are initialized in the correct order
 * and provides a single access point for dependencies.
 */
public class GameCore {
    private static GameCore INSTANCE;

    private final List<GameService> services = new ArrayList<>();

    // Core Managers
    private final WorldManager worldManager;
    private final GoalManager goalManager;
    private final PlayerManager playerManager;
    private final EntityManager entityManager;
    private final ItemManager itemManager;
    private final ModuleManager moduleManager;
    private final SidebarManager sidebarManager;
    private final ChatManager chatManager;
    private final TablistManager tablistManager;

    public GameCore() {
        if (INSTANCE != null) {
            throw new IllegalStateException("GameCore already initialized.");
        }
        INSTANCE = this;

        // 1. Initialize core infrastructure managers (Order matters for dependencies)
        this.worldManager = new WorldManager();
        this.goalManager = new GoalManager();
        this.sidebarManager = new SidebarManager();
        this.chatManager = register(new ChatManager());
        this.tablistManager = new TablistManager();

        // 2. Initialize entity/item systems
        this.entityManager = register(new EntityManager());
        this.itemManager = register(new ItemManager());


        // PlayerManager depends on WorldManager and GoalManager
        this.playerManager = register(new PlayerManager(this.worldManager, this.goalManager, this.sidebarManager, chatManager, tablistManager));
        // 3. Initialize Content/Modules (Modules depend on all core systems)
        this.moduleManager = register(
                new ModuleManager(this.worldManager, this.goalManager, this.entityManager, this.itemManager)
        );


        // 4. Final step: Register all event listeners
        this.registerAllListeners();
    }

    public static GameCore getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("GameCore not yet initialized.");
        }
        return INSTANCE;
    }

    /**
     * Helper method to create a service, add it to the services list, and return it.
     */
    private <T extends GameService> T register(T service) {
        // We only register services that actually need to register listeners.
        // If a service has a registerListeners method, it must be added.
        this.services.add(service);
        return service;
    }

    private void registerAllListeners() {
        // Iterate over the list populated in the constructor
        services.forEach(GameService::registerListeners);
        System.out.println("All game services have registered their listeners.");
    }

    // Public Getters for all services
    public WorldManager getWorldManager() { return worldManager; }
    public GoalManager getGoalManager() { return goalManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public EntityManager getEntityManager() { return entityManager; }
    public ItemManager getItemManager() { return itemManager; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public ChatManager getChatManager() {
        return chatManager;
    }
}