package nub.wi1helm.content.fountainfish;

import net.minestom.server.tag.Tag;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.instances.LoopInstance;
import nub.wi1helm.entity.npc.LoopNPC;

public class FountainFishManager implements FountainFishContent{

    private final LoopNPC fisherman;
    private final LoopInstance instance;
    private final GoalManager goalManager;
    public FountainFishManager(LoopInstance instance, GoalManager goalManager) {
        this.instance = instance;
        this.goalManager = goalManager;
        this.fisherman = new FishermanNPC(this.instance);

        fisherman.spawn();
    }

    @Override
    public LoopNPC getFisherManNPC() {
        return null;
    }

    @Override
    public Tag<?> getManagerTag() {
        return null;
    }
}
