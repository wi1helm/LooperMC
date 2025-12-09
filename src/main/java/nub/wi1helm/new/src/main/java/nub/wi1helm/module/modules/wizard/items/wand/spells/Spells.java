package nub.wi1helm.module.modules.wizard.items.wand.spells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spells {
    public static final Spell NOTHINGNESS = new SpellOfNothingness();
    public static final Spell TELEPORT = new SpellOfTeleportation();

    private static final Map<String, Spell> BY_ID = new HashMap<>();

    static {
        register(NOTHINGNESS);
        register(TELEPORT);
    }


    public static final List<Spell> ALL = new ArrayList<>(BY_ID.values());

    private static void register(Spell spell) {
        BY_ID.put(spell.getId(), spell);
    }

    public static Spell fromId(String id) {
        return BY_ID.get(id);
    }

}
