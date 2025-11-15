package nub.wi1helm.npc;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NPCManager {
    private static final Map<Integer, NPC> NPCS = new ConcurrentHashMap<>();

    public static void registerNPC(NPC npc) {
        NPCS.put(npc.getEntityId(), npc);
    }

    public static void unregisterNPC(NPC npc) {
        NPCS.remove(npc.getEntityId());
    }

    public static NPC getNPC(int entityId) {
        return NPCS.get(entityId);
    }

    public static Collection<NPC> getAllNPCs() {
        return NPCS.values();
    }
}
