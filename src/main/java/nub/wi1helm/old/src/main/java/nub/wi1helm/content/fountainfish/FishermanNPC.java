package nub.wi1helm.content.fountainfish;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import nub.wi1helm.entity.npc.LoopNPC;
import org.jetbrains.annotations.NotNull;

public class FishermanNPC extends LoopNPC {


    public FishermanNPC(@NotNull Instance instance) {
        super(Component.text("Fisherman"), skin, instance, );

        this.lookAtPlayerInit(120,3);

        eventNode().addListener(NPCInteractedEvent.class, event -> {
            event.getPlayer().openInventory(new FisherManGUI());
        });

    }
}
