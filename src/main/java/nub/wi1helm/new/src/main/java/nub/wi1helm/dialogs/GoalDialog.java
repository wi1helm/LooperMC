package nub.wi1helm.dialogs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.dialog.*;
import nub.wi1helm.goals.ServerGoal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GoalDialog {

    private static final DialogActionButton EXIT_BUTTON = new DialogActionButton(
            Component.text("Exit", NamedTextColor.RED, TextDecoration.BOLD),
            null,
            100,
            null
    );

    /**
     * Builds the dialog body from a list of server goals.
     */
    private List<DialogBody> buildBody() {
        List<DialogBody> bodies = new ArrayList<>();

        // Header
        bodies.add(new DialogBody.PlainMessage(
                Component.text("Server Goals:", NamedTextColor.GOLD, TextDecoration.BOLD),
                200
        ));

        return bodies;
    }

    /**
     * Builds the metadata for this dialog (with bodies included).
     */
    private DialogMetadata buildMetadata(List<DialogBody> bodies) {
        return new DialogMetadata(
                Component.text("Global Progress", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                null,                     // subtitle
                true,
                false,
                DialogAfterAction.CLOSE,
                bodies,
                List.of()
        );
    }

    private List<DialogActionButton> actions(Collection<ServerGoal> goals) {

        List<DialogActionButton> actions = new ArrayList<>();

        for (ServerGoal goal : goals) {
            // Single body containing all goal lines (use the same width as the header)
            actions.add(new DialogActionButton(goal.getName().append(Component.text(": ",NamedTextColor.GRAY)) .append(goal.getProgress()), goal.getDescription() ,260, null));
        }

        return actions;
    }

    /**
     * Builds the full dialog.
     */
    public Dialog get(Collection<ServerGoal> goals) {

        return new Dialog.MultiAction(buildMetadata(buildBody()), actions(goals) ,EXIT_BUTTON, 1);
    }
}
