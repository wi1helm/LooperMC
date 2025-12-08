package nub.wi1helm.module.modules.wizard.interactions;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.entity.interaction.DialogNode;
import nub.wi1helm.entity.interaction.InteractionNode;
import nub.wi1helm.entity.interaction.InteractionPriority;
import nub.wi1helm.entity.interaction.InventoryNode;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.wizard.entity.WizardNPC;
import nub.wi1helm.module.modules.wizard.guis.GiveMagicWandMenu;

public record WizardInteraction(WizardNPC npc, ItemManager itemManager) {

    public void setup() {

        // Sounds
        Sound wizardSpeak = Sound.sound()
                .type(SoundEvent.ENTITY_WITCH_AMBIENT)
                .pitch(1.2f)
                .source(net.kyori.adventure.sound.Sound.Source.NEUTRAL)
                .build();

        Sound wizardYes = Sound.sound()
                .type(SoundEvent.ENTITY_WITCH_CELEBRATE)
                .pitch(1.0f)
                .source(net.kyori.adventure.sound.Sound.Source.NEUTRAL)
                .build();

        TaskSchedule delay = TaskSchedule.seconds(2);

        // ---------------------------------------------------------
        // FINAL REPEATING NODE (after wand is given)
        // ---------------------------------------------------------
        DialogNode alreadyGiven = DialogNode.builder()
                .text(Component.text("I only had one wand for you, my boy. Use it wisely... or recklessly, I don't care."))
                .type(SoundEvent.ENTITY_WITCH_AMBIENT)
                .pitch(1.0f)
                .delay(delay)
                .manual()
                .build();

        // ---------------------------------------------------------
        // INVENTORY NODE (early exit → goes to final repeating node)
        // ---------------------------------------------------------
        InventoryNode giveWandNode = InventoryNode.builder()
                .inventory(new GiveMagicWandMenu(itemManager))
                .build(); // auto-advance to next node
        giveWandNode.connect(alreadyGiven);

        // ---------------------------------------------------------
        // DIALOG SEQUENCE BEFORE GETTING WAND
        // ---------------------------------------------------------
        DialogNode line1 = DialogNode.builder()
                .text(Component.text("Ahh... you seek power, do you?"))
                .sound(wizardSpeak)
                .delay(delay)
                .autoAdvance(true)
                .build();

        DialogNode line2 = DialogNode.builder()
                .text(Component.text("This wand... is the most powerful thing in the entire universe."))
                .sound(wizardSpeak)
                .delay(delay)
                .autoAdvance(true)
                .build();

        DialogNode line3 = DialogNode.builder()
                .text(Component.text("Handle it with utmost care..."))
                .sound(wizardSpeak)
                .delay(delay)
                .autoAdvance(true)
                .build();

        DialogNode line4 = DialogNode.builder()
                .text(Component.text("Actually—bah! Do whatever you want with it, my boy!"))
                .sound(wizardYes)
                .delay(delay)
                .autoAdvance(true)
                .build();

        // Last line → opens the wand inventory
        line4.connect(giveWandNode);

        // ---------------------------------------------------------
        // ROOT TRIGGER (only hand MAIN)
        // ---------------------------------------------------------
        InteractionNode startNode = InteractionNode.create(
                event -> {},
                InteractionPriority.condition(event -> event.getHand() == PlayerHand.MAIN, 20),
                true,
                TaskSchedule.immediate()
        );

        // Flow: root → line1 → line2 → line3 → line4 → giveWand → repeat node
        startNode.connect(line1);
        line1.connect(line2);
        line2.connect(line3);
        line3.connect(line4);

        npc.getInteractionGraph().getRoot().connect(startNode);
    }
}
