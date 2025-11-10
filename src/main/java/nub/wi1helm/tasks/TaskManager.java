package nub.wi1helm.tasks;

import nub.wi1helm.ServerManager;
import nub.wi1helm.tasks.mail.MailManager;

public class TaskManager {

    private static TaskManager instance;

    // References to individual managers
    private final MailManager mailManager;

    private TaskManager() {
        // Initialize managers here
        this.mailManager = new MailManager();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    // Getters for managers
    public MailManager getMailManager() {
        return mailManager;
    }


    /** Call this after server and goals are loaded */
    public void initialize() {

    }
}
