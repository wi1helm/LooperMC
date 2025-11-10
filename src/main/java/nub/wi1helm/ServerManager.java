package nub.wi1helm;

import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.instances.LoopInstance;
import nub.wi1helm.tasks.mail.MailManager;

public class ServerManager {

    private final LoopInstance instance = new LoopInstance();
    private final GoalManager goalManager;

    private static ServerManager manager;

    private ServerManager() {
        this.goalManager = new GoalManager();
    }

    public static ServerManager getManager() {
        if (manager != null) return manager;
        manager = new ServerManager();
        return manager;
    }

    public LoopInstance getInstance() {
        return instance;
    }

    public GoalManager goalManager() {
        return goalManager;
    }
}
