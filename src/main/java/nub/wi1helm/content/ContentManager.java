package nub.wi1helm.content;

import net.minestom.server.utils.validate.Check;
import nub.wi1helm.content.chores.laundry.LaundryManager;
import nub.wi1helm.content.mail.MailManager;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.instances.LoopInstance;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ContentManager {

    private static ContentManager instance;

    private List<Content> contentList = new LinkedList<>();

    private ContentManager(LoopInstance contentInstance, GoalManager goalManager) {
        register(new MailManager(contentInstance, goalManager));
        register(new LaundryManager(contentInstance, goalManager));
    }

    private void register(Content content){
        this.contentList.add(content);
    }

    public static ContentManager init(LoopInstance contentInstance, GoalManager goalManager) {
        if (instance == null) {
            instance = new ContentManager(contentInstance, goalManager);
        }
        return instance;
    }
    public static ContentManager get() {
        Check.stateCondition(instance == null, "ContentManager needs to be initiated before get-ted!");
        return instance;
    }
}
