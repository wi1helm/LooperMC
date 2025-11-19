package nub.wi1helm.core;

/**
 * Defines the contract for all major game components managed by the GameKernel.
 * This enforces a clear separation between construction and event registration.
 */
public interface GameService {

    /**
     * Registers all Minestom-specific event listeners and custom EventNodes
     * associated with this service. This method is called by the GameKernel
     * only after all core services have been constructed.
     */
    void registerListeners();

}